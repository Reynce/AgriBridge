package com.reyn.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.Sku;
import com.reyn.objects.entity.UserOrder;
import com.reyn.service.OrderDetailService;
import com.reyn.service.SkuService;
import com.reyn.service.UserOrderService;
import com.reyn.utils.statusConstants.OrderStatusConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final UserOrderService userOrderService;
    private final OrderDetailService orderDetailService;
    private final SkuService skuService;

    /**
     * 每分钟执行一次，检查超时未支付订单
     */
    @Scheduled(fixedRate = 60000) // 每60秒执行一次
    @Transactional
    public void checkAndCancelTimeoutOrders() {
        log.info("开始执行订单超时检查任务");

        try {
            // 查询所有未支付且已超时的订单
            QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("payment_status", OrderStatusConstants.PAYMENT_STATUS_UNPAID) // 未支付
                    .eq("order_status", OrderStatusConstants.ORDER_STATUS_PENDING_SHIP) // 待发货
                    .lt("timeout_at", new Date()) // 超时时间小于当前时间
                    .orderByAsc("created_at"); // 按创建时间排序

            List<UserOrder> timeoutOrders = userOrderService.list(queryWrapper);

            if (timeoutOrders.isEmpty()) {
                log.info("没有找到超时订单");
                return;
            }

            log.info("发现 {} 个超时订单，开始处理", timeoutOrders.size());

            int cancelledCount = 0;
            for (UserOrder order : timeoutOrders) {
                try {
                    cancelTimeoutOrder(order);
                    cancelledCount++;
                } catch (Exception e) {
                    log.error("处理超时订单失败，订单ID: {}", order.getId(), e);
                }
            }

            log.info("订单超时检查任务完成，共处理 {} 个超时订单", cancelledCount);
        } catch (Exception e) {
            log.error("订单超时检查任务执行异常", e);
        }
    }

    /**
     * 取消超时订单并释放库存
     */
    @Transactional
    public void cancelTimeoutOrder(UserOrder order) {
        log.info("开始处理超时订单，订单ID: {}", order.getId());

        // 更新订单状态为已取消
        UserOrder updateOrder = new UserOrder();
        updateOrder.setId(order.getId());
        updateOrder.setOrderStatus(OrderStatusConstants.ORDER_STATUS_CANCELLED);
        updateOrder.setPaymentStatus(OrderStatusConstants.PAYMENT_STATUS_UNPAID);

        boolean orderUpdated = userOrderService.updateById(updateOrder);
        if (!orderUpdated) {
            throw new RuntimeException("更新订单状态失败，订单ID: " + order.getId());
        }

        // 查询订单详情，释放库存
        QueryWrapper<OrderDetail> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.eq("order_id", order.getId());
        List<OrderDetail> orderDetails = orderDetailService.list(detailQueryWrapper);

        for (OrderDetail detail : orderDetails) {
            releaseInventory(detail.getSkuId(), detail.getQuantity());
        }

        log.info("超时订单处理完成，订单ID: {}，已释放库存", order.getId());
    }

    /**
     * 释放库存（增加SKU库存）
     */
    private void releaseInventory(Long skuId, Integer quantity) {
        log.debug("释放库存，SKU ID: {}, 数量: {}", skuId, quantity);

        // 使用乐观锁更新库存
        Sku sku = skuService.getById(skuId);
        if (sku == null) {
            log.warn("SKU不存在，无法释放库存，SKU ID: {}", skuId);
            return;
        }

        // 增加库存
        Sku updateSku = new Sku();
        updateSku.setId(skuId);
        updateSku.setStock((int) (sku.getStock() + quantity));
        updateSku.setVersion(sku.getVersion()); // 设置当前版本号用于乐观锁

        boolean skuUpdated = skuService.updateById(updateSku);
        if (!skuUpdated) {
            log.warn("释放库存失败，可能是并发修改，SKU ID: {}", skuId);
            // 这里可以选择重试机制
            retryReleaseInventory(skuId, Math.toIntExact(quantity));
        }
    }

    /**
     * 重试释放库存（简单重试机制）
     */
    private void retryReleaseInventory(Long skuId, Integer quantity) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            retryCount++;
            Sku currentSku = skuService.getById(skuId);
            if (currentSku == null) {
                log.warn("重试释放库存时，SKU不存在，SKU ID: {}", skuId);
                return;
            }

            Sku updateSku = new Sku();
            updateSku.setId(skuId);
            updateSku.setStock(currentSku.getStock() + quantity);
            updateSku.setVersion(currentSku.getVersion());

            if (skuService.updateById(updateSku)) {
                log.debug("重试释放库存成功，SKU ID: {}", skuId);
                return;
            }

            log.debug("重试释放库存失败，第{}次尝试", retryCount);
        }

        log.error("重试释放库存失败，超过最大重试次数，SKU ID: {}", skuId);
    }
}
