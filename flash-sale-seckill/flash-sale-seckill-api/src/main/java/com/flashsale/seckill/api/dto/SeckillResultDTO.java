package com.flashsale.seckill.api.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SeckillResultDTO implements Serializable {
    private Long orderId;
    private int status; // 0-排队中 1-成功 -1-失败
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
