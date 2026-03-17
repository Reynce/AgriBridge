package com.reyn.common.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.page.TableDataInfo;
import com.reyn.utils.statusConstants.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    /**
     * 开启分页
     */
    protected <T> Page<T> startPage() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        int pageNum = Integer.parseInt(request.getParameter("pageNum") != null ? request.getParameter("pageNum") : "1");
        int pageSize = Integer.parseInt(request.getParameter("pageSize") != null ? request.getParameter("pageSize") : "10");
        return new Page<>(pageNum, pageSize);
    }

    /**
     * 封装分页数据
     */
    protected <T> TableDataInfo getDataTable(Page<T> page) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords()); // 当前页数据
        rspData.setTotal(page.getTotal());  // 总记录数
        return rspData;
    }
}
