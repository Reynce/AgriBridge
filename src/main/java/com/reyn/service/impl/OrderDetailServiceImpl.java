package com.reyn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.OrderDetailMapper;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
