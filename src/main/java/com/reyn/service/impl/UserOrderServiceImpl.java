package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.*;
import com.reyn.objects.dto.OrderCreateDTO;
import com.reyn.objects.dto.OrderItemDTO;
import com.reyn.objects.dto.OrderSearchDTO;
import com.reyn.objects.entity.*;
import com.reyn.objects.vo.OrderDetailVO;
import com.reyn.objects.vo.OrderVO;
import com.reyn.objects.vo.PayOrderVO;
import com.reyn.service.UserCartService;
import com.reyn.service.UserOrderService;
import com.reyn.utils.AddressParser;
import com.reyn.utils.LoginHelper;
import com.reyn.utils.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.reyn.utils.SystemConstants.ORDER_TTL_MILLISECONDS;

@Service
@RequiredArgsConstructor
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrder> implements UserOrderService {

    private final OrderDetailMapper orderDetailMapper;
    private final AddressMapper addressMapper;
    private final SkuMapper skuMapper;
    private final UserCartService userCartService;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final UserOrderMapper userOrderMapper;

    @Override
    @Transactional
    public SaResult createOrder(OrderCreateDTO orderCreateDTO) {
        // 1. 获取当前用户ID
        Long userId = LoginHelper.getLoginUserId();

        // 2. 验证收货地址是否存在
        Address address = addressMapper.selectById(orderCreateDTO.getAddressId());
        if (address == null) {
            return SaResult.error("收货地址不存在");
        }

        // 3. 验证商品和库存
        List<OrderItemDTO> orderItems = orderCreateDTO.getOrderItems();
        if (orderItems.isEmpty()) {
            return SaResult.error("订单项不能为空");
        }

        // 4. 获取所有SKU信息并验证库存
        Set<Long> skuIds = new HashSet<>();
        for (OrderItemDTO item : orderItems) {
            skuIds.add(item.getSkuId());
        }

        List<Sku> skus = skuMapper.selectByIds(new ArrayList<>(skuIds));
        Map<Long, Sku> skuMap = new HashMap<>();
        for (Sku sku : skus) {
            skuMap.put(sku.getId(), sku);
        }

        // 5. 验证每个订单项的库存和价格
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDTO item : orderItems) {
            Sku sku = skuMap.get(item.getSkuId());
            if (sku == null) {
                return SaResult.error("商品不存在：" + item.getSkuId());
            }

            if (sku.getStock() < item.getQuantity()) {
                return SaResult.error("商品库存不足：" + sku.getSpecification() + "，库存：" + sku.getStock() + "，需求数量：" + item.getQuantity());
            }

            // 计算价格
            BigDecimal itemTotal = sku.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            // 更新库存
            sku.setStock(sku.getStock() - item.getQuantity());
            skuMapper.updateById(sku);
        }

        // 6. 创建订单
        UserOrder order = new UserOrder();
        order.setBuyerId(userId);   // 买家id
        order.setTotalPrice(totalPrice);    // 总价
        order.setOrderStatus(1L); // 待发货
        order.setPaymentStatus(0L); // 未支付
        order.setTimeoutAt(calculateOrderTimeoutAt());  // 订单超时时间
        order.setShippingAddress(formatAddress(address)); // 格式化地址

        // 保存订单
        this.save(order);

        // 7. 创建订单详情
        for (OrderItemDTO item : orderItems) {
            Sku sku = skuMap.get(item.getSkuId());

            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setSkuId(item.getSkuId());
            detail.setSkuSpecification(skuMapper.getSpecificationByIdString(item.getSkuId()));
            detail.setProductTitle(productMapper.getProductTitleBySkuId(item.getSkuId()));
            detail.setProductMainImage(productImageMapper.getMainImgUrlBySkuId(item.getSkuId()));
            detail.setQuantity(item.getQuantity());
            detail.setPrice(sku.getPrice());
            detail.setProductId(sku.getProductId());
            detail.setSallerId(productMapper.getProductSallerIdBySkuId(item.getSkuId()));

            orderDetailMapper.insert(detail);
        }

        // 8. 从购物车中移除已下单的商品
        try {
            List<Long> orderedSkuIds = orderItems.stream()
                    .map(OrderItemDTO::getSkuId)
                    .collect(java.util.stream.Collectors.toList());

            userCartService.batchRemoveFromCart(userId, orderedSkuIds);
        } catch (Exception e) {
            // 即使清理购物车失败，也不应该影响订单创建的结果
            e.printStackTrace();
        }

        return SaResult.data(buildOrderVO(order, orderItems));
    }

    private String formatAddress(Address address) {
        return address.getProvince() + address.getCity() + address.getDistrict() +
                address.getAddress() + " (" + address.getRecipientName() + " " + address.getPhone() + ")";
    }

    private OrderVO buildOrderVO(UserOrder order, List<OrderItemDTO> orderItems) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setBuyerId(order.getBuyerId());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setPaymentStatus(order.getPaymentStatus());
        vo.setShippingAddress(order.getShippingAddress());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setUpdatedAt(order.getUpdatedAt());

        // 构建订单详情列表（这里简化处理，实际可能需要从数据库查询）
        List<OrderDetailVO> details = new ArrayList<>();
        for (OrderItemDTO item : orderItems) {
            OrderDetailVO detail = new OrderDetailVO();
            detail.setSkuId(item.getSkuId());
            detail.setQuantity(item.getQuantity());
            // 这里需要查询具体的价格信息
            Sku sku = skuMapper.selectById(item.getSkuId());
            if (sku != null) {
                detail.setPrice(sku.getPrice());
            }
            details.add(detail);
        }
        vo.setOrderDetails(details);

        return vo;
    }

    @Override
    public SaResult getUserOrders(Page pageParam, OrderSearchDTO searchDTO) {

        // 执行分页查询
        Page<UserOrder> result = this.page(pageParam, buildOrderSearchQueryWrapper(searchDTO));

        // 转换为 VO 对象
        List<OrderVO> orderVOList = new ArrayList<>();
        for (UserOrder order : result.getRecords()) {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setTotalPrice(order.getTotalPrice());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setPaymentStatus(order.getPaymentStatus());
            vo.setShippingAddress(order.getShippingAddress());
            vo.setCreatedAt(order.getCreatedAt());
            vo.setUpdatedAt(order.getUpdatedAt());

            // 查询订单详情
            QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
            detailWrapper.eq("order_id", order.getId());
            List<OrderDetail> details = orderDetailMapper.selectList(detailWrapper);

            List<OrderDetailVO> detailVOs = new ArrayList<>();
            for (OrderDetail detail : details) {
                // 根据订单详情信息,转化为视图信息
                OrderDetailVO detailVO = this.order2orderVO(detail);
                detailVOs.add(detailVO);
            }
            vo.setOrderDetails(detailVOs);

            orderVOList.add(vo);
        }

        // 封装分页结果
        PageResult pageResult = PageResult.data(result);
        pageResult.setData(orderVOList);

        return SaResult.data(pageResult);
    }

    @Override
    public List<OrderDetailVO> getOrderDetail(Long orderId) {
        UserOrder order = this.getById(orderId);
        if (order == null) {
            return null;
        }

        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setPaymentStatus(order.getPaymentStatus());
        vo.setShippingAddress(order.getShippingAddress());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setUpdatedAt(order.getUpdatedAt());

        // 查询订单详情
        QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("order_id", orderId);
        List<OrderDetail> details = orderDetailMapper.selectList(detailWrapper);

        List<OrderDetailVO> detailVOs = new ArrayList<>();
        for (OrderDetail detail : details) {
            OrderDetailVO detailVO = new OrderDetailVO();
            detailVO.setId(detail.getId());
            detailVO.setOrderId(detail.getOrderId());
            detailVO.setQuantity(detail.getQuantity());
            detailVO.setPrice(detail.getPrice());
            detailVOs.add(detailVO);
        }
        vo.setOrderDetails(detailVOs);

        return detailVOs;
    }

    private Date calculateOrderTimeoutAt(){
        Date now = new Date();
        long milliseconds = now.getTime() + ORDER_TTL_MILLISECONDS;
        return new Date(milliseconds);
    }

    private OrderDetailVO order2orderVO(OrderDetail detail){

        OrderDetailVO detailVO = new OrderDetailVO();
        BeanUtils.copyProperties(detail, detailVO);
        detailVO.setImgUrl(detail.getProductMainImage());

        return detailVO;
    }

    @Override
    public PayOrderVO getOrderDetailViewAndAddressView(Long orderId){
        PayOrderVO payOrderVO = new PayOrderVO();
        // 查询订单详情
        QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("order_id", orderId);
        List<OrderDetail> details = orderDetailMapper.selectList(detailWrapper);

        List<OrderDetailVO> detailVOs = new ArrayList<>();
        for (OrderDetail detail : details) {
            // 根据订单详情信息,转化为视图信息
            OrderDetailVO detailVO = this.order2orderVO(detail);
            detailVOs.add(detailVO);
        }

        UserOrder userOrder = userOrderMapper.selectById(orderId);

        payOrderVO.setOrderDetailVOList(detailVOs);
        payOrderVO.setOrderNo(userOrder.getOrderNo());
        payOrderVO.setAddressVO(AddressParser.parseAddress(userOrder.getShippingAddress()));
        payOrderVO.setCreatedAt(userOrder.getCreatedAt());
        payOrderVO.setTotalPrice(userOrder.getTotalPrice());
        return payOrderVO;
    }

    private QueryWrapper buildOrderSearchQueryWrapper(OrderSearchDTO searchDTO){
        QueryWrapper<UserOrder> wrapper = new QueryWrapper<>();

        if (searchDTO.getBuyerId() != null){
            wrapper.eq("buyer_id", searchDTO.getBuyerId());
        }

        if (searchDTO.getOrderStatus() != null){
            wrapper.eq("order_status", searchDTO.getOrderStatus());
        }

        if (searchDTO.getPaymentStatus() != null){
            wrapper.eq("payment_status", searchDTO.getPaymentStatus());
        }

        // 添加关键词搜索条件
        if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().trim().isEmpty()) {
            String keyword = searchDTO.getKeyword().trim();
            wrapper.and(wq -> {
                wq.like("order_no", keyword)
                        .or()
                        .apply("EXISTS (SELECT 1 FROM order_detail od WHERE od.order_id = user_order.id AND od.product_title LIKE {0})",
                                "%" + keyword + "%")
                        .or()
                        .apply("EXISTS (SELECT 1 FROM order_detail od WHERE od.order_id = user_order.id AND od.sku_specification LIKE {0})",
                                "%" + keyword + "%");
            });
        }

        wrapper.orderByDesc("created_at");
        return wrapper;
    }
}
