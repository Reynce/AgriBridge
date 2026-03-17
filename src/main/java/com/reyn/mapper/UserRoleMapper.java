package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.UserRole;
import com.reyn.objects.vo.RoleVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);

    List<RoleVO> selectRoleKeysAndNamesByUserId(@Param("userId") Long userId);
}
