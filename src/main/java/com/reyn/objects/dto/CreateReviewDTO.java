package com.reyn.objects.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class CreateReviewDTO {
    private Long orderDetailId;
    private String content;
    private Short rating;
    private List<String> reviewImages;
}
