package com.reyn.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;
import com.reyn.service.PayService;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pay")
public class PayController {

    private final PayService payService;

    @GetMapping("/order")
    public SaResult pay(@RequestParam @NotNull(message = "订单号不能为空") Long orderId){
        return payService.payOrder(orderId);
    }

    @SaIgnore
    @PostMapping("/success")
    public String paySuccess(HttpServletRequest request){
        // 获取支付宝回调参数
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            params.put(paramName, request.getParameter(paramName));
        }
        return payService.success(params);
    }
}
