package com.reyn.objects.vo;

import lombok.Data;

@Data
public class AddressVO {
    private Long id;
    private String recipientName;
    private String phone;
    private String fullAddress;
}
