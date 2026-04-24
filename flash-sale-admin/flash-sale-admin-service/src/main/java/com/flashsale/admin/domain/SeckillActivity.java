package com.flashsale.admin.domain;

import java.time.LocalDateTime;

/**
 * 秒杀活动聚合根。
 * <p>
 * 管理秒杀活动的全生命周期，包含状态机转换逻辑。 状态机：{@code PENDING → ONGOING → ENDED}。
 * </p>
 * <h3>业务行为</h3>
 * <ul>
 * <li>{@link #start()} — 手动启动活动（PENDING → ONGOING）</li>
 * <li>{@link #end()} — 手动结束活动（PENDING/ONGOING → ENDED）</li>
 * <li>{@link #refreshStatusBy(LocalDateTime)} — 根据时间自动推进状态</li>
 * </ul>
 * <h3>不变量</h3>
 * <ul>
 * <li>活动名称、商品ID、价格、库存、时间区间创建后不可修改</li>
 * <li>状态只能通过业务方法推进，不可回退</li>
 * </ul>
 *
 * @see ActivityStatus
 * @see TimeRange
 * @see Money
 */
public class SeckillActivity {

    // ==================== 字段 ====================

    private final Long id;

    private final String activityName;

    private final Long goodsId;

    private final Money seckillPrice;

    private final int stockCount;

    private final TimeRange timeRange;

    private ActivityStatus status;

    private final LocalDateTime createTime;

    private LocalDateTime updateTime;

    // ==================== 私有构造器 ====================

    private SeckillActivity(Long id, String activityName, Long goodsId, Money seckillPrice, int stockCount,
        TimeRange timeRange, ActivityStatus status, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.activityName = activityName;
        this.goodsId = goodsId;
        this.seckillPrice = seckillPrice;
        this.stockCount = stockCount;
        this.timeRange = timeRange;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ==================== 工厂方法 ====================

    /**
     * 创建新秒杀活动。
     * <p>
     * 初始状态为 {@link ActivityStatus#PENDING}。
     * </p>
     *
     * @param activityName 活动名称，不能为空
     * @param goodsId 关联商品 ID
     * @param seckillPrice 秒杀价格
     * @param stockCount 秒杀库存
     * @param timeRange 活动时间窗口
     * @return 尚未持久化的活动（id = null）
     */
    public static SeckillActivity create(String activityName, Long goodsId, Money seckillPrice, int stockCount,
        TimeRange timeRange) {
        if (activityName == null || activityName.isBlank()) {
            throw new IllegalArgumentException("活动名不能为空");
        }
        if (goodsId == null) {
            throw new IllegalArgumentException("goodsId 不能为空");
        }
        if (stockCount < 0) {
            throw new IllegalArgumentException("库存不能为负");
        }
        LocalDateTime now = LocalDateTime.now();
        return new SeckillActivity(null, activityName, goodsId, seckillPrice, stockCount, timeRange,
            ActivityStatus.PENDING, now, now);
    }

    /**
     * 从持久化存储重建领域对象。<b>仅限 infrastructure 层使用。</b>
     */
    public static SeckillActivity reconstitute(Long id, String activityName, Long goodsId, Money seckillPrice,
        int stockCount, TimeRange timeRange, ActivityStatus status, LocalDateTime createTime,
        LocalDateTime updateTime) {
        return new SeckillActivity(id, activityName, goodsId, seckillPrice, stockCount, timeRange, status, createTime,
            updateTime);
    }

    // ==================== 状态机行为 ====================

    /**
     * 手动启动活动（PENDING → ONGOING）。
     *
     * @throws IllegalStateException 当前状态非 PENDING
     */
    public void start() {
        if (status != ActivityStatus.PENDING) {
            throw new IllegalStateException("仅 PENDING 状态可以 start, 当前: " + status);
        }
        this.status = ActivityStatus.ONGOING;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 手动结束活动（任何非 ENDED 状态 → ENDED）。
     *
     * @throws IllegalStateException 活动已结束
     */
    public void end() {
        if (status == ActivityStatus.ENDED) {
            throw new IllegalStateException("活动已结束");
        }
        this.status = ActivityStatus.ENDED;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 根据当前时间自动推进活动状态。
     * <p>
     * 用于定时任务或查询时的懒惰状态刷新：
     * </p>
     * <ul>
     * <li>已过结束时间 → 自动推进至 ENDED</li>
     * <li>在进行中时间窗口且当前为 PENDING → 推进至 ONGOING</li>
     * </ul>
     *
     * @param now 当前时间（由调用方提供，便于测试）
     */
    public void refreshStatusBy(LocalDateTime now) {
        if (timeRange.hasEnded(now)) {
            if (status != ActivityStatus.ENDED) {
                this.status = ActivityStatus.ENDED;
                this.updateTime = LocalDateTime.now();
            }
        }
        else if (timeRange.contains(now)) {
            if (status == ActivityStatus.PENDING) {
                this.status = ActivityStatus.ONGOING;
                this.updateTime = LocalDateTime.now();
            }
        }
    }

    // ==================== 持久化辅助 ====================

    /**
     * 创建携带数据库生成 ID 的新副本。<b>仅限 Repository 使用。</b>
     */
    public SeckillActivity withId(Long newId) {
        return new SeckillActivity(newId, activityName, goodsId, seckillPrice, stockCount, timeRange, status,
            createTime, updateTime);
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public String getActivityName() {
        return activityName;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public Money getSeckillPrice() {
        return seckillPrice;
    }

    public int getStockCount() {
        return stockCount;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    /** 便捷方法：获取活动开始时间。 */
    public LocalDateTime getStartTime() {
        return timeRange.start();
    }

    /** 便捷方法：获取活动结束时间。 */
    public LocalDateTime getEndTime() {
        return timeRange.end();
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
}
