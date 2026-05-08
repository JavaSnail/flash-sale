package com.flashsale.admin.infrastructure.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flashsale.admin.infrastructure.sys.MenuDO;

@Mapper
public interface MenuMapper extends BaseMapper<MenuDO> {

    List<MenuDO> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
}
