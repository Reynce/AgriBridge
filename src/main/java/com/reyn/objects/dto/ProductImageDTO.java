package com.reyn.objects.dto;

import lombok.Data;

@Data
public class ProductImageDTO {
    private Long id;
    private Long productId;
    private String url;
    private Byte imageType;
}
