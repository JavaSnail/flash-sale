package com.flashsale.admin.infrastructure.sys;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.admin.domain.sys.AdminUser;
import com.flashsale.admin.domain.sys.AdminUserRepository;
import com.flashsale.admin.infrastructure.sys.mapper.AdminUserMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisAdminUserRepository implements AdminUserRepository {

    private final AdminUserMapper adminUserMapper;

    @Override
    public Optional<AdminUser> findByUsername(String username) {
        AdminUserDO userDO = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUserDO>().eq(AdminUserDO::getUsername, username));
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public Optional<AdminUser> findById(Long id) {
        AdminUserDO userDO = adminUserMapper.selectById(id);
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public List<AdminUser> findAll() {
        return adminUserMapper.selectList(new LambdaQueryWrapper<AdminUserDO>()
                .orderByDesc(AdminUserDO::getCreateTime))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public AdminUser save(AdminUser user) {
        AdminUserDO userDO = toDO(user);
        adminUserMapper.insert(userDO);
        return toDomain(userDO);
    }

    @Override
    public void update(AdminUser user) {
        AdminUserDO userDO = toDO(user);
        userDO.setId(user.getId());
        adminUserMapper.updateById(userDO);
    }

    @Override
    public void updateLastLoginTime(Long id) {
        adminUserMapper.updateLastLoginTime(id);
    }

    @Override
    public void deleteById(Long id) {
        adminUserMapper.deleteById(id);
    }

    private AdminUser toDomain(AdminUserDO userDO) {
        return AdminUser.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .password(userDO.getPassword())
                .salt(userDO.getSalt())
                .realName(userDO.getRealName())
                .phone(userDO.getPhone())
                .email(userDO.getEmail())
                .avatar(userDO.getAvatar())
                .status(userDO.getStatus())
                .lastLoginTime(userDO.getLastLoginTime())
                .createTime(userDO.getCreateTime())
                .updateTime(userDO.getUpdateTime())
                .build();
    }

    private AdminUserDO toDO(AdminUser user) {
        AdminUserDO userDO = new AdminUserDO();
        userDO.setUsername(user.getUsername());
        userDO.setPassword(user.getPassword());
        userDO.setSalt(user.getSalt());
        userDO.setRealName(user.getRealName());
        userDO.setPhone(user.getPhone());
        userDO.setEmail(user.getEmail());
        userDO.setAvatar(user.getAvatar());
        userDO.setStatus(user.getStatus());
        return userDO;
    }
}
