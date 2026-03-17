package com.reyn.utils;

import com.reyn.objects.vo.AddressVO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressParser {

    public static AddressVO parseAddress(String addressStr) {
        // 创建正则表达式模式匹配地址格式
        // 匹配模式：地址信息 (收件人 手机号)
        Pattern pattern = Pattern.compile("(.+)\\((.+)\\s+(.+)\\)");
        Matcher matcher = pattern.matcher(addressStr);

        AddressVO addressVO = new AddressVO();

        if (matcher.find()) {
            // 提取完整地址
            String fullAddress = matcher.group(1).trim();
            // 提取收件人姓名
            String recipientName = matcher.group(2).trim();
            // 提取手机号
            String phone = matcher.group(3).trim();

            addressVO.setFullAddress(fullAddress);
            addressVO.setRecipientName(recipientName);
            addressVO.setPhone(phone);
        }

        return addressVO;
    }
}

