package com.reyn.objects.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoriteVO {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productBrief;
    private String mainImageUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
