package com.flashsale.admin.domain;

import java.util.List;
import java.util.Optional;

/**
 * 秒杀活动仓储接口（领域层定义）。
 */
public interface SeckillActivityRepository {

    /**
     * 持久化秒杀活动。新建时返回带 ID 的新实例。
     */
    SeckillActivity save(SeckillActivity activity);

    /**
     * 根据 ID 查找活动。
     */
    Optional<SeckillActivity> findById(Long id);

    /**
     * 查询全部活动。
     */
    List<SeckillActivity> findAll();

    /**
     * 直接更新活动状态（数据库层操作，用于状态持久化）。
     *
     * @param id     活动 ID
     * @param status 目标状态码
     */
    void updateStatus(Long id, int status);
}
