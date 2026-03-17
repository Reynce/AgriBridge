package com.reyn.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PayUtil {

    // appid
    private final String APP_ID = "2021000149617024";
    // 应用私钥
    private final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC6MtrpxOexMNapxC8uS6bIiFFI/g09Jn1d6swFSX0QIusPh3YgoV+VefyFWKTLA7NVEJmkv/gl7HkPXUse2yEOah4sNoQdSF7CezcQp9ZItvU1mY6iSeE/MgMPAO2bpB2k7X3v/yZ1utw180G/HXzdaBxoWt7eJb6KyGrbGNXm7nVwmDf9U9ukOa7oqXdocGHy7BngE/MU+VXu7vmOBX0NtMU2MFOlzxxObNJr+od0vTzzkscHKvVbXUUdIwo/urce8P2zvaq8Vr7ZOozpSGUp77fsnTKYFO+xzDuH+NKli4t2BSBZoteAGtMtpcazVNaWHRX1SfvVS1ElKcyafKdDAgMBAAECggEAUXu/4V2SJzDAQDriitgAfPFq4Q6MrJ4Tbu45qnamjFE7nOgjAdSdB8UChThtpYpRCIDjO/3mknIR6G9jWHV82doGqkKRPl9VWLajOl64n8mxNWulx8+dCxLP09kMIniHoI1t94EpS4wioll6BDDbW+kbxW8+6OiLoiBi4s7TjOM+0QLpIMa/+iWM21vFh+u9w5+0PdDtF8gBbCE8/ioZ9dz2fCVYNHscfpktRK0R/pzbf/p5xEKKgIjGvfQqPB3nOImFX67rWgPxAJX9rHtMVGT/sFtUuJmK/I+ZN62Zmq6J61R6zCdHKEVeBCwQklXPYsVMG9xSq6OuJGAlX/4iAQKBgQDet2Ej+RXxrv7lMAaCm+tdYdEvInCE4ovhRU+hSTJu0wfAZ/WO70rCd5ZcC+EWrQxcb0O1yPeT78UwnG3FAJ8/LRxSBKVdkSEZ2uwIlJyetk23fW8/egGzHTPfA0C/CAetay5tuKFOdQ3gLGRukCTFrd+rVMQzpqwTy3j+TLCEIQKBgQDWBmR5mSFFtcT4/FOKuV7oAk95lIhE6p3J4zm4Y+UxOEHwOuEcmcf3Wfb9f+1AHi1SzIVkzWhyvsGA6ZTZarybqbOOQLrxeMQBpDKu3kRL/RUlu7Pd8ggQnBHrMwnBtQHcz8xJ8C3+TNlWCPLlLw5Yhl5GRQtUSUWtY9iQmwe+4wKBgBUEx3Ump8yEUGf+zU58Oo04kw5YuLfBzaO379jv2IOfBOdCqGkR6kNKF/Wd5WNHd5gto/0+9yaOBVUeHjnuYhkLP9X5vEWFUdNn1sjEhbdwywrbGqmp8QkfY2rTmsOslVTnfZ07RHXsFCAo8F2C2wWQ8D/yIwYg37KKjGOrhykhAoGBAKyof4nxVPwtxXev5510KfI1WvQUNVKTizOBUeFFBXKNlfQ9K7ms/bq4Ix8igr1FGv8+6yYE54waA3UB+wjiqMFTR/0sjyOab1V7pz+XI7XBt7u3D0WvqX3syi/Z/rb8VUJTiNTlI9WRHYvgsELfkU0tKQS7UoZujf1xHRabWsZNAoGAcMPtem796IZADJqg9nsJUuYMEzFq0mfH/oEbJbthKbuuAx6KqjpVpBXhtDOAg/XbMWO2DJYkvNzkuy5YiA6mjP/nTpELYnstzstIv6BUOXeJT8CKKSAXuzWaIrhkKCA2Z3GWdmqRLpGU+cETgU3lAAVdqrKyGakM9Xk6pbuZCYs=";

    private final String CHARSET = "UTF-8";
    // 支付宝公钥
    private final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgeY2OhX0K7dDdi3pu5jrDM0j8KCSr2Ootdxo/+KlBHtP2NMPfmXuiPKzrdviVNo9eBnYeqPdjWW4VmdvFBwsr+lTwE5THpdKRBF1KmSrXV+A6Dc1jJPTLIPFa5GgjOb5qPVpufoPVO3Z/wx1OwyZJ3NAR5Ksr7e+hAViLd/rCCua+Cu3ZSNpKQTAjEjdWoTUyxvfVHtY3Vrt6kTtlkpvNoxA/yyTxfEgyKJMvN4w47O8MJ35+ynefDciqAf5EBotRXbUCKiyNBUhlkvNKqy9TgWc6Io6Uf49VsG/Jri8a9AGTs54k9FTxONsOANlDBg1eurpdJWkXrCPobjdkqturwIDAQAB";
    // 这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    private final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private final String FORMAT = "JSON";
    // 签名方式
    private final String SIGN_TYPE = "RSA2";
    // 支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
    private final String NOTIFY_URL = "http://zc349a45.natappfree.cc/pay/success";
    // 支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    private final String RETURN_URL = "http://localhost:5173/";
    private AlipayClient alipayClient = null;

    // 验证支付宝回调请求签名的方法
    public boolean verifyCallback(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 支付宝官方提供的接口
    public String sendRequestToAlipay(String outTradeNo, Float totalAmount, String subject) throws AlipayApiException {
        // 获得初始化的AlipayClient
        alipayClient = new DefaultAlipayClient(GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);

        // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(RETURN_URL);
        alipayRequest.setNotifyUrl(NOTIFY_URL);

        // 商品描述（可空）
        String body = "";
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        // 请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        return result;
    }

    // 通过订单编号查询
    public String query(String id) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", id);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        String body = null;
        try {
            response = alipayClient.execute(request);
            body = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return body;
    }
}
