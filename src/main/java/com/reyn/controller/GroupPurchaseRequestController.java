package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.GroupPurchaseRequest;
import com.reyn.common.core.controller.BaseController;
import com.reyn.objects.page.TableDataInfo;
import com.reyn.service.GroupPurchaseRequestService;
import com.reyn.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group-purchase/request")
public class GroupPurchaseRequestController extends BaseController {

    @Autowired
    private GroupPurchaseRequestService requestService;

    /**
     * 创建求购请求
     */
    @PostMapping
    public SaResult createRequest(@RequestBody GroupPurchaseRequest request) {
        request.setUserId(LoginHelper.getLoginUserId());
        GroupPurchaseRequest result = requestService.createRequest(request);
        return SaResult.data(result);
    }

    /**
     * 查询求购请求列表
     */
    @GetMapping("/list")
    public TableDataInfo getList(GroupPurchaseRequest request) {
        Page<GroupPurchaseRequest> page = startPage();
        Page<GroupPurchaseRequest> result = requestService.getRequestList(page, request);
        return getDataTable(result);
    }

    /**
     * 获取求购请求详情
     */
    @GetMapping("/{id}")
    public SaResult getDetail(@PathVariable Long id) {
        GroupPurchaseRequest request = requestService.getRequestById(id);
        if (request == null) {
            return SaResult.error("求购请求不存在");
        }
        return SaResult.data(request);
    }

    /**
     * 查询当前用户发起的求购请求
     */
    @GetMapping("/my")
    public SaResult getMyRequests() {
        Long userId = LoginHelper.getLoginUserId();
        List<GroupPurchaseRequest> requests = requestService.getUserRequests(userId);
        return SaResult.data(requests);
    }

    /**
     * 更新求购请求
     */
    @PutMapping
    public SaResult updateRequest(@RequestBody GroupPurchaseRequest request) {
        boolean result = requestService.updateRequest(request);
        return result ? SaResult.ok("更新成功") : SaResult.error("更新失败");
    }

    /**
     * 删除求购请求
     */
    @DeleteMapping("/{id}")
    public SaResult deleteRequest(@PathVariable Long id) {
        boolean result = requestService.deleteRequest(id);
        return result ? SaResult.ok("删除成功") : SaResult.error("删除失败");
    }

    /**
     * 更新求购请求状态
     */
    @PutMapping("/status/{id}")
    public SaResult updateStatus(@PathVariable Long id, @RequestParam Byte status) {
        boolean result = requestService.updateRequestStatus(id, status);
        return result ? SaResult.ok("状态更新成功") : SaResult.error("状态更新失败");
    }
}
