package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
