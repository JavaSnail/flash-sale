package com.flashsale.goods.domain;

import java.time.LocalDateTime;

/**
 * 秒杀商品聚合根。
 *
 * <p>封装秒杀商品的库存管理与时间窗口判断逻辑。
 * 库存扣减行为内聚在聚合根中，替代 Service 层的 if-else 散装逻辑。</p>
 *
 * <h3>时间判断</h3>
 * <p>提供两种 API 风格：</p>
 * <ul>
 *   <li>{@code isOngoingAt(now)} — 显式注入时钟，便于单元测试</li>
 *   <li>{@code isOngoing()} — 便捷方法，使用系统时钟（兼容旧 API）</li>
 * </ul>
 *
 * @see Money
 * @see TimeRange
 * @see StockInsufficientException
 */
public class SeckillGoods {

    // ==================== 字段 ====================

    private final Long id;
    private final Long goodsId;
    private final Money seckillPrice;
    private int stockCount;
    private final TimeRange timeRange;
    private final LocalDateTime createTime;

    // ==================== 私有构造器 ====================

    private SeckillGoods(Long id,
                         Long goodsId,
                         Money seckillPrice,
                         int stockCount,
                         TimeRange timeRange,
                         LocalDateTime createTime) {
        this.id = id;
        this.goodsId = goodsId;
        this.seckillPrice = seckillPrice;
        this.stockCount = stockCount;
        this.timeRange = timeRange;
        this.createTime = createTime;
    }

    // ==================== 工厂方法 ====================

    /**
     * 创建新秒杀商品。
     *
     * @param goodsId      关联的商品 ID
     * @param seckillPrice 秒杀价格
     * @param stockCount   秒杀库存
     * @param timeRange    秒杀时间窗口
     * @return 尚未持久化的秒杀商品（id = null）
     */
    public static SeckillGoods create(Long goodsId, Money seckillPrice, int stockCount, TimeRange timeRange) {
        if (goodsId == null) {
            throw new IllegalArgumentException("goodsId 不能为空");
        }
        if (stockCount < 0) {
            throw new IllegalArgumentException("stockCount 不能为负");
        }
        return new SeckillGoods(null, goodsId, seckillPrice, stockCount, timeRange, LocalDateTime.now());
    }

    /**
     * 从持久化存储重建领域对象。<b>仅限 infrastructure 层使用。</b>
     */
    public static SeckillGoods reconstitute(Long id,
                                            Long goodsId,
                                            Money seckillPrice,
                                            int stockCount,
                                            TimeRange timeRange,
                                            LocalDateTime createTime) {
        return new SeckillGoods(id, goodsId, seckillPrice, stockCount, timeRange, createTime);
    }

    // ==================== 库存行为 ====================

    /**
     * 内存中的库存扣减。
     *
     * <p>注意：这是聚合根内部的逻辑扣减，实际的持久化层原子扣减
     * 由 {@link SeckillGoodsRepository#decreaseStock(Long)} 负责。</p>
     *
     * @throws StockInsufficientException 库存不足时抛出
     */
    public void decrementStock() {
        if (!hasStock()) {
            throw new StockInsufficientException(id);
        }
        this.stockCount--;
    }

    /**
     * 判断是否有库存。
     */
    public boolean hasStock() {
        return stockCount > 0;
    }

    // ==================== 时间判断（可测试版本，显式注入时钟） ====================

    /**
     * 判断在给定时刻，秒杀是否正在进行中。
     *
     * @param now 当前时间（由调用方提供，便于测试）
     * @return 在时间窗口内返回 true
     */
    public boolean isOngoingAt(LocalDateTime now) {
        return timeRange.contains(now);
    }

    /**
     * 判断在给定时刻，秒杀是否尚未开始。
     */
    public boolean isNotStartedAt(LocalDateTime now) {
        return !timeRange.hasStarted(now);
    }

    /**
     * 判断在给定时刻，秒杀是否已结束。
     */
    public boolean hasEndedAt(LocalDateTime now) {
        return timeRange.hasEnded(now);
    }

    // ==================== 时间判断（便捷版本，使用系统时钟） ====================

    /** 判断秒杀是否正在进行中（使用系统时钟）。 */
    public boolean isOngoing() {
        return isOngoingAt(LocalDateTime.now());
    }

    /** 判断秒杀是否尚未开始（使用系统时钟）。 */
    public boolean isNotStarted() {
        return isNotStartedAt(LocalDateTime.now());
    }

    // ==================== 持久化辅助 ====================

    /**
     * 创建携带数据库生成 ID 的新副本。<b>仅限 Repository 使用。</b>
     */
    public SeckillGoods withId(Long newId) {
        return new SeckillGoods(newId, goodsId, seckillPrice, stockCount, timeRange, createTime);
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
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

    /** 便捷方法：获取秒杀开始时间。 */
    public LocalDateTime getStartTime() {
        return timeRange.start();
    }

    /** 便捷方法：获取秒杀结束时间。 */
    public LocalDateTime getEndTime() {
        return timeRange.end();
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
}
