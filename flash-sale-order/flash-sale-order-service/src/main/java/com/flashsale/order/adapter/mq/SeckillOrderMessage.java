package com.flashsale.order.adapter.mq;

import lombok.Data;

@Data
public class SeckillOrderMessage {
    private Long userId;
    private Long seckillGoodsId;
}
