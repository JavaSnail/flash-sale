package com.flashsale.admin.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.flashsale.admin.domain.sys.Menu;
import com.flashsale.admin.domain.sys.MenuRepository;
import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SysMenuService {

    private final MenuRepository menuRepository;

    public List<MenuVO> listMenuTree() {
        List<Menu> all = menuRepository.findAll();
        return buildTree(all);
    }

    public List<MenuVO> listMenuFlat() {
        return menuRepository.findAll().stream().map(this::toVO).toList();
    }

    public void createMenu(CreateMenuRequest request) {
        Menu menu = Menu.builder()
                .parentId(request.getParentId() != null ? request.getParentId() : 0L)
                .menuName(request.getMenuName())
                .menuType(request.getMenuType())
                .routePath(request.getRoutePath())
                .componentPath(request.getComponentPath())
                .permCode(request.getPermCode())
                .icon(request.getIcon())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .visible(request.getVisible() != null ? request.getVisible() : 1)
                .status(1)
                .build();
        menuRepository.save(menu);
    }

    public void updateMenu(Long id, UpdateMenuRequest request) {
        menuRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "菜单不存在"));
        Menu menu = Menu.builder()
                .id(id)
                .parentId(request.getParentId() != null ? request.getParentId() : 0L)
                .menuName(request.getMenuName())
                .menuType(request.getMenuType())
                .routePath(request.getRoutePath())
                .componentPath(request.getComponentPath())
                .permCode(request.getPermCode())
                .icon(request.getIcon())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .visible(request.getVisible() != null ? request.getVisible() : 1)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .build();
        menuRepository.update(menu);
    }

    public void deleteMenu(Long id) {
        menuRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "菜单不存在"));
        menuRepository.deleteById(id);
    }

    private List<MenuVO> buildTree(List<Menu> menus) {
        Map<Long, List<Menu>> childrenMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        List<MenuVO> roots = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getParentId() == 0) {
                MenuVO vo = toVO(menu);
                vo.setChildren(buildChildren(menu.getId(), childrenMap));
                roots.add(vo);
            }
        }
        return roots;
    }

    private List<MenuVO> buildChildren(Long parentId, Map<Long, List<Menu>> childrenMap) {
        List<Menu> children = childrenMap.getOrDefault(parentId, List.of());
        return children.stream().map(m -> {
            MenuVO vo = toVO(m);
            vo.setChildren(buildChildren(m.getId(), childrenMap));
            return vo;
        }).toList();
    }

    private MenuVO toVO(Menu menu) {
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
                .build();
    }
}
