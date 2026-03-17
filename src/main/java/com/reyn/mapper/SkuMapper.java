package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SkuMapper extends BaseMapper<Sku> {
    List<Sku> selectByIds(@Param("ids") List<Long> ids);

    String getSpecificationByIdString(@Param("id") Long id);
}
