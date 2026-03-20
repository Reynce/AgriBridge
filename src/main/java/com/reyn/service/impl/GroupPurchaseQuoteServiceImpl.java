package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.GroupPurchaseQuote;
import com.reyn.objects.entity.GroupPurchaseRequest;
import com.reyn.objects.vo.GroupPurchaseQuoteVO;
import com.reyn.mapper.AddressMapper;
import com.reyn.mapper.GroupPurchaseQuoteMapper;
import com.reyn.mapper.GroupPurchaseRequestMapper;
import com.reyn.objects.entity.Address;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.UserOrder;
import com.reyn.service.GroupPurchaseQuoteService;
import com.reyn.service.GroupPurchaseRequestService;
import com.reyn.service.OrderDetailService;
import com.reyn.service.UserOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.reyn.utils.SystemConstants.ORDER_TTL_MILLISECONDS;

@Service
public class GroupPurchaseQuoteServiceImpl implements GroupPurchaseQuoteService {

    @Autowired
    private GroupPurchaseQuoteMapper quoteMapper;

    @Autowired
    private GroupPurchaseRequestMapper requestMapper;

    @Autowired
    private UserOrderService userOrderService;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private GroupPurchaseRequestService groupPurchaseRequestService;

    @Override
    @Transactional
    public GroupPurchaseQuote createQuote(GroupPurchaseQuote quote) {
        quote.setCreatedAt(LocalDateTime.now());
        quote.setUpdatedAt(LocalDateTime.now());
        quoteMapper.insert(quote);
        return quote;
    }

    @Override
    public List<GroupPurchaseQuote> getQuotesByRequestId(Long requestId) {
        return quoteMapper.selectByRequestId(requestId);
    }

    @Override
    public List<GroupPurchaseQuote> getQuotesBySellerId(Long sellerId) {
        return quoteMapper.selectBySellerId(sellerId);
    }

    @Override
    public List<GroupPurchaseQuoteVO> getMyQuotesVO(Long sellerId) {
        return quoteMapper.selectVOBySellerId(sellerId);
    }

    @Override
    public GroupPurchaseQuote getLowestQuote(Long requestId) {
        return quoteMapper.selectLowestQuote(requestId);
    }

    @Override
    @Transactional
    public boolean updateQuote(GroupPurchaseQuote quote) {
        quote.setUpdatedAt(LocalDateTime.now());
        return quoteMapper.updateById(quote) > 0;
    }

    @Override
    @Transactional
    public boolean deleteQuote(Long id) {
        return quoteMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public SaResult createOrderFromQuote(Long quoteId, Long addressId, String remark) {
        // 1. 验证报价是否存在
        GroupPurchaseQuote quote = quoteMapper.selectById(quoteId);
        if (quote == null) {
            return SaResult.error("报价不存在");
        }

        // 2. 验证求购请求是否存在且状态为进行中
        GroupPurchaseRequest request = requestMapper.selectById(quote.getRequestId());
        if (request == null) {
            return SaResult.error("关联的求购请求不存在");
        }

        // 验证地址
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            return SaResult.error("地址不存在");
        }

        if (request.getStatus() != 1) { // 1表示进行中
            return SaResult.error("求购请求状态不正确，无法创建订单");
        }

        // 3. 验证报价是否在有效期内
        if (request.getExpireTime().isBefore(LocalDateTime.now())) {
            return SaResult.error("报价已过期");
        }

        // 4. 构建订单
        UserOrder order = new UserOrder();
        BigDecimal totalPrice = quote.getQuotedPrice().multiply(BigDecimal.valueOf(request.getQuantity()));  // 计算价格
        order.setBuyerId(request.getUserId());   // 报价者id
        order.setTotalPrice(totalPrice);    // 总价
        order.setOrderStatus(1L); // 待发货
        order.setPaymentStatus(0L); // 未支付
        order.setTimeoutAt(calculateOrderTimeoutAt());  // 订单超时时间
        order.setShippingAddress(formatAddress(address)); // 格式化地址
        userOrderService.save(order);

        // 构建订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(order.getId());
        detail.setProductTitle(request.getTitle());
        detail.setSkuSpecification(request.getTitle());
        detail.setProductMainImage(quote.getProductImage());
        detail.setQuantity(request.getQuantity());
        detail.setPrice(quote.getQuotedPrice());
        detail.setSallerId(quote.getSellerId());
        detail.setShippedStatus(0L); // 初始状态：未发货

        orderDetailService.save(detail);

        // 更新求购状态
        groupPurchaseRequestService.updateRequestStatus(request.getId(), (byte) 2);

        return SaResult.data(Map.of(
                "orderId", order.getId()
        ));
    }

    private String formatAddress(Address address) {
        return address.getProvince() + address.getCity() + address.getDistrict() +
                address.getAddress() + " (" + address.getRecipientName() + " " + address.getPhone() + ")";
    }

    private Date calculateOrderTimeoutAt(){
        Date now = new Date();
        long milliseconds = now.getTime() + ORDER_TTL_MILLISECONDS;
        return new Date(milliseconds);
    }
}
