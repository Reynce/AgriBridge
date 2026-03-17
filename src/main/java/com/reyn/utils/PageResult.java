package com.reyn.utils;

import lombok.Data;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> data;
    private long total;
    private long current;
    private long size;
    private long pages;

    public static PageResult data(Page page){
        PageResult pageResult = new PageResult<>();
        BeanUtils.copyProperties(page, pageResult);

        pageResult.setData(page.getRecords());
        return pageResult;
    }
}

