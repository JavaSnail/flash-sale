package com.flashsale.admin.infrastructure.sys;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.admin.domain.sys.Permission;
import com.flashsale.admin.domain.sys.PermissionRepository;
import com.flashsale.admin.infrastructure.sys.mapper.PermissionMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisPermissionRepository implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> findAll() {
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionDO>()
                .orderByAsc(PermissionDO::getPermCode))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Permission> findByRoleIds(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return permissionMapper.selectByRoleIds(roleIds).stream().map(this::toDomain).toList();
    }

    @Override
    public List<String> findPermCodesByUserId(Long userId) {
        return permissionMapper.selectPermCodesByUserId(userId);
    }

    private Permission toDomain(PermissionDO permDO) {
        return Permission.builder()
                .id(permDO.getId())
                .permCode(permDO.getPermCode())
                .permName(permDO.getPermName())
                .apiPath(permDO.getApiPath())
                .apiMethod(permDO.getApiMethod())
                .description(permDO.getDescription())
                .createTime(permDO.getCreateTime())
                .updateTime(permDO.getUpdateTime())
                .build();
    }
}
