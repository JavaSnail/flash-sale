package com.flashsale.goods.domain;

import java.time.LocalDateTime;

/**
 * 商品聚合根。
 * <p>
 * 封装商品的核心属性与业务行为，所有状态变更通过业务方法完成。 价格使用 {@link Money} 值对象封装，保证非负约束。
 * </p>
 * <h3>业务行为</h3>
 * <ul>
 * <li>{@link #changePrice(Money)} — 调整商品价格</li>
 * <li>{@link #replenishStock(int)} — 补充库存</li>
 * <li>{@link #isAvailable()} — 判断是否有货</li>
 * </ul>
 *
 * @see Money
 */
public class Goods {

    // ==================== 字段 ====================

    private final Long id;

    private String goodsName;

    private String goodsImg;

    private Money goodsPrice;

    private int goodsStock;

    private final LocalDateTime createTime;

    private LocalDateTime updateTime;

    // ==================== 私有构造器 ====================

    private Goods(Long id, String goodsName, String goodsImg, Money goodsPrice, int goodsStock,
        LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.goodsName = goodsName;
        this.goodsImg = goodsImg;
        this.goodsPrice = goodsPrice;
        this.goodsStock = goodsStock;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ==================== 工厂方法 ====================

    /**
     * 创建新商品。
     *
     * @param goodsName 商品名称，不能为空
     * @param goodsImg 商品图片 URL
     * @param price 价格值对象
     * @param stock 初始库存，不能为负
     * @return 尚未持久化的商品（id = null）
     */
    public static Goods create(String goodsName, String goodsImg, Money price, int stock) {
        if (goodsName == null || goodsName.isBlank()) {
            throw new IllegalArgumentException("商品名不能为空");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("库存不能为负");
        }
        LocalDateTime now = LocalDateTime.now();
        return new Goods(null, goodsName, goodsImg, price, stock, now, now);
    }

    /**
     * 从持久化存储重建领域对象。
     * <p>
     * <b>仅限 infrastructure 层使用。</b>
     * </p>
     */
    public static Goods reconstitute(Long id, String goodsName, String goodsImg, Money goodsPrice, int goodsStock,
        LocalDateTime createTime, LocalDateTime updateTime) {
        return new Goods(id, goodsName, goodsImg, goodsPrice, goodsStock, createTime, updateTime);
    }

    // ==================== 业务行为 ====================

    /**
     * 调整商品价格。
     *
     * @param newPrice 新价格值对象
     * @throws IllegalArgumentException 当新价格为 null 时抛出
     */
    public void changePrice(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("价格不能为空");
        }
        this.goodsPrice = newPrice;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 补充库存。
     *
     * @param quantity 补货数量，必须为正
     * @throws IllegalArgumentException 当数量 <= 0 时抛出
     */
    public void replenishStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("补货数量必须大于 0");
        }
        this.goodsStock += quantity;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 判断商品是否有货（库存 > 0）。
     */
    public boolean isAvailable() {
        return goodsStock > 0;
    }

    // ==================== 持久化辅助 ====================

    /**
     * 创建携带数据库生成 ID 的新副本。<b>仅限 Repository 使用。</b>
     */
    public Goods withId(Long newId) {
        return new Goods(newId, goodsName, goodsImg, goodsPrice, goodsStock, createTime, updateTime);
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public Money getGoodsPrice() {
        return goodsPrice;
    }

    public int getGoodsStock() {
        return goodsStock;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
}
