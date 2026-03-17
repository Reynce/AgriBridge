package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统配置对象 sys_config
 * 
 * @author reyn
 */
@Data
@TableName("sys_config")
public class SysConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置名称 */
    private String configName;

    /** 配置键名 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 系统内置（1是 0否） */
    private Integer configType;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
