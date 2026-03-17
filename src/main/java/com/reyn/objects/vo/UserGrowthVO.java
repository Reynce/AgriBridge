package com.reyn.objects.vo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserGrowthVO {
    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 当日新增用户数
     */
    private Long newUserCount;

    /**
     * 累计用户总数
     */
    private Long totalUserCount;
}
