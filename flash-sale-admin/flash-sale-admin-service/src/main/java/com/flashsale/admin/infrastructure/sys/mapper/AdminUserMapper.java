package com.flashsale.admin.infrastructure.sys.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flashsale.admin.infrastructure.sys.AdminUserDO;

@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUserDO> {

    @Update("UPDATE sys_admin_user SET last_login_time = NOW() WHERE id = #{id}")
    int updateLastLoginTime(Long id);
}
