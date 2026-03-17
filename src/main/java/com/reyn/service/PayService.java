package com.reyn.service;

import cn.dev33.satoken.util.SaResult;

import java.util.Map;

public interface PayService {
    SaResult payOrder(long orderId);

    String success(Map<String, String> params);
}
