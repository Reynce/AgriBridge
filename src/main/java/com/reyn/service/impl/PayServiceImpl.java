package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reyn.mapper.ProductMapper;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.UserOrder;
import com.reyn.service.OrderDetailService;
import com.reyn.service.PayService;
import com.reyn.service.UserOrderService;
import com.reyn.utils.statusConstants.OrderStatusConstants;
import com.reyn.utils.PayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayServiceImpl implements PayService {

    private final UserOrderService userOrderService;
    private final OrderDetailService orderDetailService;
    private final ProductMapper productMapper;
    private final PayUtil payUtil;

    @Override
    @Transactional
    public SaResult payOrder(long orderId) {
        // 1. 查询订单是否存在
        UserOrder order = userOrderService.getById(orderId);
        if (order == null) {
            return SaResult.error("订单不存在");
        }

        // 2. 检查订单状态（必须是未支付状态）
        if (order.getPaymentStatus() != OrderStatusConstants.PAYMENT_STATUS_UNPAID ||
                order.getOrderStatus() != OrderStatusConstants.ORDER_STATUS_PENDING_SHIP) {
            return SaResult.error("订单状态异常，无法支付");
        }

        // 3. 调用支付宝支付接口
        try {
            String orderNo = "ORD" + System.currentTimeMillis() + "_" + orderId;
            String payHtml = payUtil.sendRequestToAlipay(
                    orderNo,
                    order.getTotalPrice().floatValue(),
                    "农产品订单支付 - " + orderNo
            );

            // 更新订单号（可选：将支付宝订单号也存储起来）
            UserOrder updateOrder = new UserOrder();
            updateOrder.setId(orderId);
            updateOrder.setOrderNo(orderNo); // 假设有orderNo字段，如果没有则可以添加
            userOrderService.updateById(updateOrder);

            return SaResult.data(payHtml); // 返回支付页面HTML
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("支付请求失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String success(Map<String, String> params) {
        try {
            // 1. 验证支付宝回调签名
            if (!payUtil.verifyCallback(params)) {
                log.error("支付宝回调签名验证失败");
                return "failure"; // 验证失败，返回failure让支付宝继续回调
            }

            // 2. 获取订单号（out_trade_no 是商户订单号）
            String outTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");

            log.info("收到支付宝回调，订单号：" + outTradeNo + "，状态：" + tradeStatus);

            // 3. 检查交易状态
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                log.warn("交易状态不是成功状态：" + tradeStatus);
                return "success"; // 即使失败也要返回success，避免支付宝重复回调
            }

            // 4. 解析订单号，提取原始订单ID（根据你生成订单号的规则）
            Long orderId = parseOrderIdFromTradeNo(outTradeNo);
            if (orderId == null) {
                log.error("无法解析订单号：" + outTradeNo);
                return "success";
            }

            // 5. 查询订单
            UserOrder order = userOrderService.getById(orderId);
            if (order == null) {
                log.error("订单不存在：" + orderId);
                return "success";
            }

            // 6. 检查订单是否已经支付（防止重复处理）
            if (order.getPaymentStatus() == OrderStatusConstants.PAYMENT_STATUS_PAID) {
               log.warn("订单已支付，无需重复处理：" + orderId);
                return "success";
            }

            // 7. 更新订单支付状态
            UserOrder updateOrder = new UserOrder();
            updateOrder.setId(orderId);
            updateOrder.setPaymentStatus(OrderStatusConstants.PAYMENT_STATUS_PAID);
            // 订单状态可以改为待发货或根据业务需求调整
            updateOrder.setOrderStatus(OrderStatusConstants.ORDER_STATUS_PENDING_SHIP); // 支付后仍为待发货

            boolean updateResult = userOrderService.updateById(updateOrder);
            if (!updateResult) {
                log.error("更新订单支付状态失败：" + orderId);
                return "failure";
            }

            // 8. 支付成功，更新商品销量
            try {
                QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
                detailWrapper.eq("order_id", orderId);
                List<OrderDetail> details = orderDetailService.list(detailWrapper);
                for (OrderDetail detail : details) {
                    if (detail.getProductId() != null && detail.getQuantity() != null) {
                        productMapper.updateProductSales(detail.getProductId(), detail.getQuantity());
                        log.info("商品销量更新成功：productId={}, quantity={}", detail.getProductId(), detail.getQuantity());
                    }
                }
            } catch (Exception e) {
                log.error("更新商品销量异常：orderId={}, error={}", orderId, e.getMessage());
                // 销量更新异常不应导致支付处理失败，所以这里只打印日志
            }

            log.info("订单支付成功处理完成：" + orderId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理支付宝回调异常：" + e.getMessage());
            return "success"; // 出现异常也要返回success，避免重复回调
        }
    }

    /**
     * 从交易号解析出订单ID
     * 注意：你需要根据实际的订单号生成规则来解析
     */
    private Long parseOrderIdFromTradeNo(String tradeNo) {
        try {
            if (tradeNo.startsWith("ORD")) {
                // 假设格式是 ORD + 时间戳 + 订单ID
                String suffix = tradeNo.substring(3); // 去掉"ORD"
                // 提取最后的数字部分作为订单ID（这取决于你的生成规则）
                int lastUnderscoreIndex = suffix.lastIndexOf("_");
                if (lastUnderscoreIndex != -1) {
                    return Long.parseLong(suffix.substring(lastUnderscoreIndex + 1));
                } else {
                    // 如果没有下划线，则可能是时间戳+ID的组合，取最后几位
                    if (suffix.length() >= 10) {
                        String orderIdStr = suffix.substring(suffix.length() - 10); // 假设订单ID是10位以内
                        return Long.parseLong(orderIdStr);
                    }
                }
            }
            // 如果无法解析，可以根据tradeNo查询数据库中的订单
            QueryWrapper<UserOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no", tradeNo); // 假设你有orderNo字段
            UserOrder order = userOrderService.getOne(wrapper);
            if (order != null) {
                return order.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
