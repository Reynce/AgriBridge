package com.reyn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.GroupPurchaseRequest;

import java.util.List;

public interface GroupPurchaseRequestService {

    /**
     * 创建求购请求
     */
    GroupPurchaseRequest createRequest(GroupPurchaseRequest request);

    /**
     * 查询求购请求列表（分页）
     */
    Page<GroupPurchaseRequest> getRequestList(Page<GroupPurchaseRequest> page, GroupPurchaseRequest request);

    /**
     * 根据ID获取求购请求详情
     */
    GroupPurchaseRequest getRequestById(Long id);

    /**
     * 查询用户发起的求购请求
     */
    List<GroupPurchaseRequest> getUserRequests(Long userId);

    /**
     * 更新求购请求
     */
    boolean updateRequest(GroupPurchaseRequest request);

    /**
     * 删除求购请求
     */
    boolean deleteRequest(Long id);

    /**
     * 更新求购请求状态
     */
    boolean updateRequestStatus(Long id, Byte status);

    /**
     * 处理过期的求购请求
     */
    void processExpiredRequests();
}
