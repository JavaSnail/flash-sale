package com.flashsale.admin.infrastructure.sys;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.admin.domain.sys.Menu;
import com.flashsale.admin.domain.sys.MenuRepository;
import com.flashsale.admin.infrastructure.sys.mapper.MenuMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisMenuRepository implements MenuRepository {

    private final MenuMapper menuMapper;

    @Override
    public List<Menu> findAll() {
        return menuMapper.selectList(new LambdaQueryWrapper<MenuDO>()
                .orderByAsc(MenuDO::getSortOrder))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Menu> findById(Long id) {
        MenuDO menuDO = menuMapper.selectById(id);
        return Optional.ofNullable(menuDO).map(this::toDomain);
    }

    @Override
    public Menu save(Menu menu) {
        MenuDO menuDO = toDO(menu);
        menuMapper.insert(menuDO);
        return toDomain(menuDO);
    }

    @Override
    public void update(Menu menu) {
        MenuDO menuDO = toDO(menu);
        menuDO.setId(menu.getId());
        menuMapper.updateById(menuDO);
    }

    @Override
    public void deleteById(Long id) {
        menuMapper.deleteById(id);
    }

    @Override
    public List<Menu> findByRoleIds(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return menuMapper.selectByRoleIds(roleIds).stream().map(this::toDomain).toList();
    }

    private Menu toDomain(MenuDO menuDO) {
        return Menu.builder()
                .id(menuDO.getId())
                .parentId(menuDO.getParentId())
                .menuName(menuDO.getMenuName())
                .menuType(menuDO.getMenuType())
                .routePath(menuDO.getRoutePath())
                .componentPath(menuDO.getComponentPath())
                .permCode(menuDO.getPermCode())
                .icon(menuDO.getIcon())
                .sortOrder(menuDO.getSortOrder())
                .visible(menuDO.getVisible())
                .status(menuDO.getStatus())
                .createTime(menuDO.getCreateTime())
                .updateTime(menuDO.getUpdateTime())
                .build();
    }

    private MenuDO toDO(Menu menu) {
        MenuDO menuDO = new MenuDO();
        menuDO.setParentId(menu.getParentId());
        menuDO.setMenuName(menu.getMenuName());
        menuDO.setMenuType(menu.getMenuType());
        menuDO.setRoutePath(menu.getRoutePath());
        menuDO.setComponentPath(menu.getComponentPath());
        menuDO.setPermCode(menu.getPermCode());
        menuDO.setIcon(menu.getIcon());
        menuDO.setSortOrder(menu.getSortOrder());
        menuDO.setVisible(menu.getVisible());
        menuDO.setStatus(menu.getStatus());
        return menuDO;
    }
}
