package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.GroupPurchaseQuote;
import com.reyn.objects.vo.GroupPurchaseQuoteVO;

import java.util.List;

public interface GroupPurchaseQuoteService {

    /**
     * 创建报价
     */
    GroupPurchaseQuote createQuote(GroupPurchaseQuote quote);

    /**
     * 查询某个求购请求的所有报价
     */
    List<GroupPurchaseQuote> getQuotesByRequestId(Long requestId);

    /**
     * 查询商家的报价记录
     */
    List<GroupPurchaseQuote> getQuotesBySellerId(Long sellerId);

    /**
     * 查询商家的报价记录（含关联信息）
     */
    List<GroupPurchaseQuoteVO> getMyQuotesVO(Long sellerId);

    /**
     * 获取某个求购请求的最低报价
     */
    GroupPurchaseQuote getLowestQuote(Long requestId);

    /**
     * 更新报价
     */
    boolean updateQuote(GroupPurchaseQuote quote);

    /**
     * 删除报价
     */
    boolean deleteQuote(Long id);

    /**
     * 根据报价创建订单
     */
    SaResult createOrderFromQuote(Long quoteId, Long addressId, String remark);
}
