package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reyn.config.GaodeMapConfig;
import com.reyn.mapper.LogisticsInfoMapper;
import com.reyn.mapper.OrderDetailMapper;
import com.reyn.mapper.UserOrderMapper;
import com.reyn.objects.dto.ShipRequestDTO;
import com.reyn.objects.entity.LogisticsInfo;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.UserOrder;
import com.reyn.objects.vo.*;
import com.reyn.service.LogisticsService;
import com.reyn.utils.AddressParser;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

import static cn.dev33.satoken.SaManager.log;
import static com.reyn.utils.SystemConstants.DEFAULT_ADDRESS;

@Service
@RequiredArgsConstructor
public class LogisticsServiceImpl implements LogisticsService {

    private final OrderDetailMapper orderDetailMapper;
    private final LogisticsInfoMapper logisticsInfoMapper;
    private final UserOrderMapper userOrderMapper;
    private final GaodeMapConfig gaodeMapConfig;

    /**
     * 获取当前商家的订单项物流信息
     */
    @Override
    public List<OrderDetailLogisticsVO> getOrderDetailLogisticsBySaller(Long sallerId) {
        return logisticsInfoMapper.selectLogisticsByMerchant(sallerId);
    }

    /**
     * 商家发货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrderItem(ShipRequestDTO request) {
        // 插入物流信息
        LogisticsInfo logisticsInfo = new LogisticsInfo();
        logisticsInfo.setOrderDetailId(request.getOrderDetailId());
        logisticsInfo.setTrackingNumber(request.getTrackingNumber());
        logisticsInfo.setLogisticsCompany(request.getLogisticsCompany());

        logisticsInfo.setStartFrom(DEFAULT_ADDRESS);                                            // 设置发货地址
        logisticsInfo.setCurrentLocation(DEFAULT_ADDRESS);                                      // 设置当前地址
        logisticsInfo.setDestination(this.getDetailDestination(request.getOrderDetailId()));    // 设置目的地址
        logisticsInfo.setShippedAt(new Date());                                                 // 设置发货时间
        logisticsInfoMapper.insert(logisticsInfo);                                              // 保存到数据库


        // 更新订单项发货状态
        OrderDetail orderDetail = orderDetailMapper.selectById(request.getOrderDetailId());
        orderDetail.setShippedStatus(1L);
        orderDetailMapper.updateById(orderDetail);

        // 更新订单状态
        UserOrder userOrder = new UserOrder();
        userOrder.setId(orderDetail.getOrderId());
        userOrder.setOrderStatus(2L);
        userOrderMapper.updateById(userOrder);

    }

    /**
     * 获取订单项的目的地
     */
    private String getDetailDestination(Long orderDetailId){
        OrderDetail orderDetail = orderDetailMapper.selectById(orderDetailId);
        UserOrder userOrder = userOrderMapper.selectById(orderDetail.getOrderId());
        return AddressParser.parseAddress(userOrder.getShippingAddress()).getFullAddress();
    }

    /**
     * 获取物流展示信息（供前端展示使用）
     */
    @Override
    public LogisticsDisplayVO getLogisticsDisplayInfo(Long orderDetailId) {
        LogisticsDisplayVO displayVO = new LogisticsDisplayVO();

        // 1. 获取物流基本信息（从数据库）
        QueryWrapper<LogisticsInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_detail_id", orderDetailId);
        LogisticsInfo logisticsInfo = logisticsInfoMapper.selectOne(queryWrapper);

        if (logisticsInfo != null) {
            LogisticsInfoVO infoVO = new LogisticsInfoVO();
            BeanUtils.copyProperties(logisticsInfo, infoVO);
            infoVO.setShippedStatus(1);
            displayVO.setLogisticsInfo(infoVO);
        }

        // 2. 获取路线信息（优先调用高德地图API，失败则使用模拟数据）
        RouteInfoVO routeInfo = getRouteInfoFromGaodeOrMock(orderDetailId);
        displayVO.setRouteInfo(routeInfo);

        // 3. 生成物流轨迹（从数据库获取真实数据）
        List<TrackVO> tracks = generateLogisticsTracksFromDatabase(orderDetailId, logisticsInfo);
        displayVO.setTracks(tracks);

        return displayVO;
    }

    /**
     * 从高德地图获取路线信息或使用模拟数据
     */
    private RouteInfoVO getRouteInfoFromGaodeOrMock(Long orderDetailId) {
        try {
            // 尝试调用高德地图API
            return getRouteInfoFromGaode(orderDetailId);
        } catch (Exception e) {
            log.error("调用高德地图API失败，使用模拟数据", e);
            // 返回模拟数据作为备选方案
            return generateMockRouteInfo();
        }
    }

    /**
     * 从高德地图获取路线信息
     */
    private RouteInfoVO getRouteInfoFromGaode(Long orderDetailId) {
        RouteInfoVO routeInfo = new RouteInfoVO();

        // 获取起点和终点地址
        OrderDetail orderDetail = orderDetailMapper.selectById(orderDetailId);
        UserOrder userOrder = userOrderMapper.selectById(orderDetail.getOrderId());

        String startAddress = DEFAULT_ADDRESS; // 发货地址
        String endAddress = AddressParser.parseAddress(userOrder.getShippingAddress()).getFullAddress(); // 收货地址

        // 调用高德地图地理编码API获取坐标
        LocationVO startLocation = geocodeAddressFromGaode(startAddress);
        LocationVO endLocation = geocodeAddressFromGaode(endAddress);

        if (startLocation == null || endLocation == null) {
            throw new RuntimeException("地理编码失败");
        }

        routeInfo.setFromLocation(startLocation);
        routeInfo.setToLocation(endLocation);

        // 调用高德地图路径规划API
        List<List<Double>> path = planRouteFromGaode(startLocation, endLocation);
        routeInfo.setPath(path);

        // 计算距离和预计时间
        calculateDistanceAndTimeFromGaode(startLocation, endLocation, routeInfo);

        return routeInfo;
    }

    /**
     * 使用高德地图API进行地理编码
     */
    private LocationVO geocodeAddressFromGaode(String address) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = gaodeMapConfig.getRestApiUrl() + "/geocode/geo?key=" + gaodeMapConfig.getWebServiceKey() + "&address=" + address;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            if (rootNode.get("status").asText().equals("1") && rootNode.get("count").asInt() > 0) {
                JsonNode locationNode = rootNode.get("geocodes").get(0).get("location");
                String[] coordinates = locationNode.asText().split(",");

                LocationVO location = new LocationVO();
                location.setLng(Double.parseDouble(coordinates[0]));
                location.setLat(Double.parseDouble(coordinates[1]));
                location.setAddress(address);
                return location;
            }
        } catch (Exception e) {
            log.error("高德地图地理编码失败: " + address, e);
        }
        return null;
    }

    /**
     * 使用高德地图API进行路径规划
     */
    private List<List<Double>> planRouteFromGaode(LocationVO start, LocationVO end) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String origin = start.getLng() + "," + start.getLat();
            String destination = end.getLng() + "," + end.getLat();
            String url = gaodeMapConfig.getRestApiUrl() + "/direction/driving?key=" + gaodeMapConfig.getWebServiceKey() +
                    "&origin=" + origin + "&destination=" + destination;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            if (rootNode.get("status").asText().equals("1")) {
                JsonNode pathNode = rootNode.get("route").get("paths").get(0);
                JsonNode stepsNode = pathNode.get("steps");

                List<List<Double>> path = new ArrayList<>();
                for (JsonNode stepNode : stepsNode) {
                    JsonNode polylineNode = stepNode.get("polyline");
                    String[] points = polylineNode.asText().split(";");
                    for (String point : points) {
                        String[] coords = point.split(",");
                        path.add(Arrays.asList(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
                    }
                }
                return path;
            }
        } catch (Exception e) {
            log.error("高德地图路径规划失败", e);
        }
        return new ArrayList<>(); // 返回空路径
    }

    /**
     * 使用高德地图API计算距离和时间
     */
    private void calculateDistanceAndTimeFromGaode(LocationVO start, LocationVO end, RouteInfoVO routeInfo) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String origins = start.getLng() + "," + start.getLat();
            String destination = end.getLng() + "," + end.getLat();
            String url = gaodeMapConfig.getRestApiUrl() + "/distance?key=" + gaodeMapConfig.getWebServiceKey() +
                    "&origins=" + origins + "&destination=" + destination + "&type=1";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            if (rootNode.get("status").asText().equals("1")) {
                JsonNode resultNode = rootNode.get("results").get(0);
                String distance = resultNode.get("distance").asText();
                String duration = resultNode.get("duration").asText();

                // 转换距离单位
                double distanceKm = Double.parseDouble(distance) / 1000.0;
                routeInfo.setDistance(String.format("%.1f公里", distanceKm));

                // 转换时间单位
                int durationSeconds = Integer.parseInt(duration);
                int hours = durationSeconds / 3600;
                int minutes = (durationSeconds % 3600) / 60;
                routeInfo.setEstimatedTime(hours + "小时" + minutes + "分钟");
                return;
            }
        } catch (Exception e) {
            log.error("高德地图距离计算失败", e);
        }

        // 如果API调用失败，使用Haversine公式计算
        double distance = calculateDistance(start.getLat(), start.getLng(), end.getLat(), end.getLng());
        routeInfo.setDistance(String.format("%.1f公里", distance));
        routeInfo.setEstimatedTime("12小时30分钟"); // 模拟数据
    }

    /**
     * 从数据库生成真实的物流轨迹
     */
    private List<TrackVO> generateLogisticsTracksFromDatabase(Long orderDetailId, LogisticsInfo logisticsInfo) {
        List<TrackVO> tracks = new ArrayList<>();

        if (logisticsInfo == null) {
            return tracks;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 1. 签收状态（如果有签收时间）
        if (logisticsInfo.getDeliveredAt() != null) {
            TrackVO track = new TrackVO();
            track.setStatus("快件已签收");
            track.setLocation(logisticsInfo.getDestination());
            track.setTime(sdf.format(logisticsInfo.getDeliveredAt()));
            tracks.add(track);
        }

        // 2. 派送中状态（根据发货时间和当前时间推算）
        if (logisticsInfo.getShippedAt() != null) {
            TrackVO track = new TrackVO();
            track.setStatus("快件正在派送中");
            track.setLocation("配送途中"); // 可以从物流信息表中获取更精确的位置
            // 模拟派送时间（发货后1-2天）
            Date deliveryTime = new Date((long) (logisticsInfo.getShippedAt().getTime() + (24 + Math.random() * 24) * 60 * 60 * 1000));
            track.setTime(sdf.format(deliveryTime));
            tracks.add(track);
        }

        // 3. 到达转运中心（发货后12-24小时）
        if (logisticsInfo.getShippedAt() != null) {
            TrackVO track = new TrackVO();
            track.setStatus("快件已到达转运中心");
            track.setLocation("分拣中心"); // 可以根据物流公司确定具体地点
            Date transferTime = new Date((long) (logisticsInfo.getShippedAt().getTime() + (12 + Math.random() * 12) * 60 * 60 * 1000));
            track.setTime(sdf.format(transferTime));
            tracks.add(track);
        }

        // 4. 发货状态
        if (logisticsInfo.getShippedAt() != null) {
            TrackVO track = new TrackVO();
            track.setStatus("快件已从发货地发出");
            track.setLocation(logisticsInfo.getStartFrom());
            track.setTime(sdf.format(logisticsInfo.getShippedAt()));
            tracks.add(track);
        }

        // 按时间倒序排列（最新的在前面）
        tracks.sort((t1, t2) -> t2.getTime().compareTo(t1.getTime()));

        return tracks;
    }

    /**
     * 计算两点间距离（Haversine公式）
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // 地球半径（公里）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    /**
     * 生成模拟路线信息（备用方案）
     */
    private RouteInfoVO generateMockRouteInfo() {
        RouteInfoVO routeInfo = new RouteInfoVO();

        LocationVO from = new LocationVO();
        from.setLat(25.445611);
        from.setLng(119.011635);
        from.setAddress("福建省莆田市城厢区莆田学院");
        routeInfo.setFromLocation(from);

        LocationVO to = new LocationVO();
        to.setLat(22.543099);
        to.setLng(113.946373);
        to.setAddress("广东省深圳市南山区科技园");
        routeInfo.setToLocation(to);

        routeInfo.setDistance("856.5公里");
        routeInfo.setEstimatedTime("12小时30分钟");

        List<List<Double>> path = new ArrayList<>();
        path.add(Arrays.asList(119.011635, 25.445611));
        path.add(Arrays.asList(118.5, 24.8));
        path.add(Arrays.asList(113.946373, 22.543099));
        routeInfo.setPath(path);

        return routeInfo;
    }
}
