package com.reyn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.GroupPurchaseRequest;
import com.reyn.mapper.GroupPurchaseRequestMapper;
import com.reyn.service.GroupPurchaseRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupPurchaseRequestServiceImpl implements GroupPurchaseRequestService {

    @Autowired
    private GroupPurchaseRequestMapper requestMapper;

    @Override
    @Transactional
    public GroupPurchaseRequest createRequest(GroupPurchaseRequest request) {
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setStatus((byte) 1); // 默认状态为进行中
        requestMapper.insert(request);
        return request;
    }

    @Override
    public Page<GroupPurchaseRequest> getRequestList(Page<GroupPurchaseRequest> page, GroupPurchaseRequest request) {
        LambdaQueryWrapper<GroupPurchaseRequest> wrapper = new LambdaQueryWrapper<>();

        if (request.getStatus() != null) {
            wrapper.eq(GroupPurchaseRequest::getStatus, request.getStatus());
        }
        if (request.getUserId() != null) {
            wrapper.eq(GroupPurchaseRequest::getUserId, request.getUserId());
        }
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            wrapper.like(GroupPurchaseRequest::getTitle, request.getTitle());
        }
        if (request.getRegion() != null && !request.getRegion().isEmpty()) {
            wrapper.like(GroupPurchaseRequest::getRegion, request.getRegion());
        }

        wrapper.orderByDesc(GroupPurchaseRequest::getCreatedAt);
        return requestMapper.selectPage(page, wrapper);
    }

    @Override
    public GroupPurchaseRequest getRequestById(Long id) {
        return requestMapper.selectById(id);
    }

    @Override
    public List<GroupPurchaseRequest> getUserRequests(Long userId) {
        return requestMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public boolean updateRequest(GroupPurchaseRequest request) {
        request.setUpdatedAt(LocalDateTime.now());
        return requestMapper.updateById(request) > 0;
    }

    @Override
    @Transactional
    public boolean deleteRequest(Long id) {
        return requestMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean updateRequestStatus(Long id, Byte status) {
        return requestMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional
    public void processExpiredRequests() {
        List<GroupPurchaseRequest> expiredRequests = requestMapper.selectExpiredRequests();
        for (GroupPurchaseRequest request : expiredRequests) {
            requestMapper.updateStatus(request.getId(), (byte) 3); // 设置为已过期
        }
    }
}
