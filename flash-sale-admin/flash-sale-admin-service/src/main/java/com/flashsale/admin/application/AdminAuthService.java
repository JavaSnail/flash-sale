package com.flashsale.admin.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.flashsale.admin.domain.sys.AdminUser;
import com.flashsale.admin.domain.sys.AdminUserRepository;
import com.flashsale.admin.domain.sys.Menu;
import com.flashsale.admin.domain.sys.MenuRepository;
import com.flashsale.admin.domain.sys.PermissionRepository;
import com.flashsale.admin.domain.sys.Role;
import com.flashsale.admin.domain.sys.RoleRepository;
import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private static final String FIXED_SALT = "f1a5h$@le";

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;

    public AdminLoginVO login(String username, String password) {
        AdminUser user = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(ErrorCode.PARAM_ERROR, "用户名或密码错误"));

        if (!user.isEnabled()) {
            throw new BizException(ErrorCode.FORBIDDEN, "账号已被禁用");
        }

        // Verify password: md5(md5(password + fixedSalt) + randomSalt)
        String firstMd5 = SecureUtil.md5(password + FIXED_SALT);
        String encoded = SecureUtil.md5(firstMd5 + user.getSalt());
        if (!encoded.equals(user.getPassword())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "用户名或密码错误");
        }

        // Login via Sa-Token
        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // Update last login time
        adminUserRepository.updateLastLoginTime(user.getId());

        // Build response
        return buildLoginVO(user, tokenInfo.getTokenValue());
    }

    public void logout() {
        StpUtil.logout();
    }

    public AdminLoginVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        AdminUser user = adminUserRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.UNAUTHORIZED));
        return buildLoginVO(user, StpUtil.getTokenValue());
    }

    private AdminLoginVO buildLoginVO(AdminUser user, String token) {
        List<Role> roles = roleRepository.findByUserId(user.getId());
        List<Long> roleIds = roles.stream().map(Role::getId).toList();
        List<String> permissions = permissionRepository.findPermCodesByUserId(user.getId());
        List<Menu> menus = menuRepository.findByRoleIds(roleIds);
        List<Menu> menuTree = buildMenuTree(menus);

        return AdminLoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .roles(roles.stream().map(Role::getRoleCode).toList())
                .permissions(permissions)
                .menus(toMenuVOList(menuTree))
                .build();
    }

    private List<Menu> buildMenuTree(List<Menu> menus) {
        Map<Long, List<Menu>> childrenMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        List<Menu> roots = new ArrayList<>();
        for (Menu menu : menus) {
            menu.setChildren(childrenMap.getOrDefault(menu.getId(), List.of()));
            if (menu.getParentId() == 0) {
                roots.add(menu);
            }
        }
        return roots;
    }

    private List<MenuVO> toMenuVOList(List<Menu> menus) {
        return menus.stream().map(this::toMenuVO).toList();
    }

    private MenuVO toMenuVO(Menu menu) {
        return MenuVO.builder()
                .id(menu.getId())
                .parentId(menu.getParentId())
                .menuName(menu.getMenuName())
                .menuType(menu.getMenuType())
                .routePath(menu.getRoutePath())
                .componentPath(menu.getComponentPath())
                .permCode(menu.getPermCode())
                .icon(menu.getIcon())
                .sortOrder(menu.getSortOrder())
                .visible(menu.getVisible())
                .children(menu.getChildren() != null ? toMenuVOList(menu.getChildren()) : List.of())
                .build();
    }
}
