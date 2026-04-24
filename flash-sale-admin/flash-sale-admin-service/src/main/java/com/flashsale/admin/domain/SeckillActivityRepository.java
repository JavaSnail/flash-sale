package com.flashsale.admin.domain;

import java.util.List;
import java.util.Optional;

public interface SeckillActivityRepository {
    void save(SeckillActivity activity);
    Optional<SeckillActivity> findById(Long id);
    List<SeckillActivity> findAll();
    void updateStatus(Long id, int status);
}
