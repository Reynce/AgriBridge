package com.reyn.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.common.core.controller.BaseController;
import com.reyn.mapper.OrderDetailMapper;
import com.reyn.mapper.ProductMapper;
import com.reyn.mapper.SkuMapper;
import com.reyn.mapper.UserOrderMapper;
import com.reyn.objects.dto.CreateReviewDTO;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.ProductReview;
import com.reyn.service.ProductReviewService;
import com.reyn.utils.LoginHelper;
import com.reyn.utils.PageResult;
import com.reyn.utils.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ProductReviewController extends BaseController {
    @Autowired
    private ProductReviewService productReviewService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserOrderMapper userOrderMapper;

    @GetMapping("/productReview")
    @SaIgnore
    public SaResult getProductReviews(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return productReviewService.getProductReviews(productId, page, size);
    }

    @GetMapping("/merchant")
    public SaResult getMerchantReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "false") Boolean recentOnly) {
        Long sellerId = LoginHelper.getLoginUserId();
        return productReviewService.getMerchantReviews(sellerId, page, size, recentOnly);
    }

    @GetMapping("/admin/list")
    @SaCheckRole("ROLE_ADMIN")
    public SaResult getAdminReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer rating) {
        return productReviewService.getAdminReviews(page, size, keyword, rating);
    }

    @DeleteMapping("/admin/{id}")
    @SaCheckRole("ROLE_ADMIN")
    public SaResult deleteReviewByAdmin(@PathVariable Long id) {
        return productReviewService.deleteReviewByAdmin(id);
    }

    @GetMapping("/reviewSummary")
    @SaIgnore
    public SaResult calculateReviewSummary(@RequestParam Long productId){
        return productReviewService.calculateReviewSummary(productId);
    }

    @GetMapping("/merchant/summary")
    public SaResult calculateMerchantReviewSummary(){
        Long sellerId = LoginHelper.getLoginUserId();
        return productReviewService.calculateMerchantReviewSummary(sellerId);
    }

    @PostMapping
    public SaResult insertProductReview(@RequestBody CreateReviewDTO createReviewDTO){
        // 检查
        OrderDetail orderDetail = orderDetailMapper.selectById(createReviewDTO.getOrderDetailId());
        if (orderDetail == null) throw new BusinessException("订单项不存在");

        // 构造评论表数据
        ProductReview productReview = new ProductReview();
        BeanUtils.copyProperties(createReviewDTO, productReview);
        productReview.setUserId(LoginHelper.getLoginUserId());
        productReview.setOrderId(userOrderMapper.selectById(orderDetail.getOrderId()).getId());
        productReview.setProductId(productMapper.selectById(skuMapper.selectById(orderDetail.getSkuId()).getProductId()).getId());

        // 标记订单详情已评价
        orderDetail = new OrderDetail();
        orderDetail.setId(createReviewDTO.getOrderDetailId());
        orderDetail.setIsReview((byte) 1);
        orderDetailMapper.updateById(orderDetail);

        return SaResult.data(productReviewService.insertProductReview(productReview));
    }

    @GetMapping("/my")
    public SaResult listMyReview(){
        ProductReview productReview = new ProductReview();
        productReview.setUserId(LoginHelper.getLoginUserId());

        Page page = startPage();

        return SaResult.data(PageResult.data(productReviewService.listReview(page, productReview)));
    }
}
