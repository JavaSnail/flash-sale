package com.flashsale.seckill.api.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "秒杀结果")
@Data
public class SeckillResultDTO implements Serializable {

    @Schema(description = "订单ID（秒杀成功时返回）", example = "1001")
    private Long orderId;

    @Schema(description = "状态：0-排队中 1-成功 -1-失败", example = "0")
    private int status;

    @Schema(description = "结果描述", example = "排队中")
    private String message;

    public static SeckillResultDTO queuing() {
        SeckillResultDTO r = new SeckillResultDTO();
        r.setStatus(0);
        r.setMessage("排队中");
        return r;
    }

    public static SeckillResultDTO success(Long orderId) {
        SeckillResultDTO r = new SeckillResultDTO();
        r.setStatus(1);
        r.setOrderId(orderId);
        r.setMessage("秒杀成功");
        return r;
    }

    public static SeckillResultDTO fail(String msg) {
        SeckillResultDTO r = new SeckillResultDTO();
        r.setStatus(-1);
        r.setMessage(msg);
        return r;
    }
}
