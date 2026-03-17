// ProductCategoryVO.java
package com.reyn.objects.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProductCategoryVO {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private List<ProductCategoryVO> children;
}
