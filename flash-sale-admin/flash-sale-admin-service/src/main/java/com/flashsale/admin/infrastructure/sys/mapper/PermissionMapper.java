package com.flashsale.admin.infrastructure.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flashsale.admin.infrastructure.sys.PermissionDO;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionDO> {

    List<String> selectPermCodesByUserId(@Param("userId") Long userId);

    List<PermissionDO> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
}
