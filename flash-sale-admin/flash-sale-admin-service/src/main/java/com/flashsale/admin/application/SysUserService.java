package com.flashsale.admin.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flashsale.admin.domain.sys.AdminUser;
import com.flashsale.admin.domain.sys.AdminUserRepository;
import com.flashsale.admin.domain.sys.Role;
import com.flashsale.admin.domain.sys.RoleRepository;
import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private static final String FIXED_SALT = "f1a5h$@le";

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;

    public List<AdminUserVO> listUsers() {
        List<AdminUser> users = adminUserRepository.findAll();
        return users.stream().map(this::toVO).toList();
    }

    public AdminUserVO getUser(Long id) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        AdminUserVO vo = toVO(user);
        List<Role> roles = roleRepository.findByUserId(id);
        vo.setRoleIds(roles.stream().map(Role::getId).toList());
        vo.setRoleCodes(roles.stream().map(Role::getRoleCode).toList());
        return vo;
    }

    public void createUser(CreateUserRequest request) {
        // Check uniqueness
        adminUserRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new BizException(ErrorCode.PARAM_ERROR, "用户名已存在");
        });

        String salt = RandomUtil.randomString(8);
        String firstMd5 = SecureUtil.md5(request.getPassword() + FIXED_SALT);
        String encoded = SecureUtil.md5(firstMd5 + salt);

        AdminUser user = AdminUser.builder()
                .username(request.getUsername())
                .password(encoded)
                .salt(salt)
                .realName(request.getRealName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(1)
                .build();
        adminUserRepository.save(user);
    }

    public void updateUser(Long id, UpdateUserRequest request) {
        AdminUser existing = adminUserRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));

        AdminUser.AdminUserBuilder builder = AdminUser.builder()
                .id(id)
                .username(existing.getUsername())
                .password(existing.getPassword())
                .salt(existing.getSalt())
                .realName(request.getRealName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(request.getStatus() != null ? request.getStatus() : existing.getStatus());

        // If password is provided, re-encode
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String salt = RandomUtil.randomString(8);
            String firstMd5 = SecureUtil.md5(request.getPassword() + FIXED_SALT);
            String encoded = SecureUtil.md5(firstMd5 + salt);
            builder.password(encoded).salt(salt);
        }

        adminUserRepository.update(builder.build());
    }

    public void deleteUser(Long id) {
        adminUserRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        adminUserRepository.deleteById(id);
    }

    public void assignRoles(Long userId, List<Long> roleIds) {
        adminUserRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        roleRepository.assignRolesToUser(userId, roleIds);
    }

    private AdminUserVO toVO(AdminUser user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
