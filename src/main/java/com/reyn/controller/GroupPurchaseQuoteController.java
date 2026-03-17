package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.GroupPurchaseQuote;
import com.reyn.objects.dto.GroupPurchaseOrderCreateDTO;
import com.reyn.objects.vo.GroupPurchaseQuoteVO;
import com.reyn.service.GroupPurchaseQuoteService;
import com.reyn.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/group-purchase/quote")
public class GroupPurchaseQuoteController {

    @Autowired
    private GroupPurchaseQuoteService quoteService;

    /**
     * 创建报价
     */
    @PostMapping
    public SaResult createQuote(@RequestBody GroupPurchaseQuote quote) {
        quote.setSellerId(LoginHelper.getLoginUserId());
        GroupPurchaseQuote result = quoteService.createQuote(quote);
        return SaResult.data(result);
    }

    /**
     * 查询某个求购请求的所有报价
     */
    @GetMapping("/request/{requestId}")
    public SaResult getQuotesByRequest(@PathVariable Long requestId) {
        List<GroupPurchaseQuote> quotes = quoteService.getQuotesByRequestId(requestId);
        return SaResult.data(quotes);
    }

    /**
     * 查询当前商家的报价记录
     */
    @GetMapping("/my")
    public SaResult getMyQuotes() {
        Long sellerId = LoginHelper.getLoginUserId();
        List<GroupPurchaseQuoteVO> quotes = quoteService.getMyQuotesVO(sellerId);
        return SaResult.data(quotes);
    }

    /**
     * 获取某个求购请求的最低报价
     */
    @GetMapping("/lowest/{requestId}")
    public SaResult getLowestQuote(@PathVariable Long requestId) {
        GroupPurchaseQuote quote = quoteService.getLowestQuote(requestId);
        return SaResult.data(quote);
    }

    /**
     * 更新报价
     */
    @PutMapping
    public SaResult updateQuote(@RequestBody GroupPurchaseQuote quote) {
        boolean result = quoteService.updateQuote(quote);
        return result ? SaResult.ok("更新成功") : SaResult.error("更新失败");
    }

    /**
     * 删除报价
     */
    @DeleteMapping("/{id}")
    public SaResult deleteQuote(@PathVariable Long id) {
        boolean result = quoteService.deleteQuote(id);
        return result ? SaResult.ok("删除成功") : SaResult.error("删除失败");
    }

    /**
     * 根据报价创建订单
     */
    @PostMapping("/create-order")
    public SaResult createOrderFromQuote(@Valid @RequestBody GroupPurchaseOrderCreateDTO orderCreateDTO) {
        return quoteService.createOrderFromQuote(
                orderCreateDTO.getQuoteId(),
                orderCreateDTO.getAddressId(),
                orderCreateDTO.getRemark()
        );
    }
}
