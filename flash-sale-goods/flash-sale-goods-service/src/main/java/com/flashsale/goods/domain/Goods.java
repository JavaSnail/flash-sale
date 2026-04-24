package com.flashsale.goods.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Goods {
    private Long id;
    private String goodsName;
    private String goodsImg;
    private BigDecimal goodsPrice;
    private Integer goodsStock;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Goods() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public String getGoodsImg() { return goodsImg; }
    public void setGoodsImg(String goodsImg) { this.goodsImg = goodsImg; }
    public BigDecimal getGoodsPrice() { return goodsPrice; }
    public void setGoodsPrice(BigDecimal goodsPrice) { this.goodsPrice = goodsPrice; }
    public Integer getGoodsStock() { return goodsStock; }
    public void setGoodsStock(Integer goodsStock) { this.goodsStock = goodsStock; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
