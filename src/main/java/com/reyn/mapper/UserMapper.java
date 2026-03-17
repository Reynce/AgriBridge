package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
