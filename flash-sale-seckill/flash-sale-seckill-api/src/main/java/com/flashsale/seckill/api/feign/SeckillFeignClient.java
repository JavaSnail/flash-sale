package com.flashsale.seckill.api.feign;

import com.flashsale.common.result.Result;
import com.flashsale.seckill.api.dto.SeckillResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "flash-sale-seckill", path = "/seckill")
public interface SeckillFeignClient {

    @GetMapping("/result")
    Result<SeckillResultDTO> getResult(@RequestParam("userId") Long userId,
                                       @RequestParam("seckillGoodsId") Long seckillGoodsId);
}
