package com.flashsale.admin.domain.sys;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    List<Menu> findAll();
    Optional<Menu> findById(Long id);
    Menu save(Menu menu);
    void update(Menu menu);
    void deleteById(Long id);
    List<Menu> findByRoleIds(List<Long> roleIds);
}
