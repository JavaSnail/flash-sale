package com.flashsale.goods.api.feign;

import com.flashsale.common.result.Result;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "flash-sale-goods", path = "/goods")
public interface GoodsFeignClient {

    @GetMapping("/seckill/list")
    Result<List<SeckillGoodsDTO>> listSeckillGoods();

    @GetMapping("/seckill/{id}")
    Result<SeckillGoodsDTO> getSeckillGoods(@PathVariable("id") Long id);

    @GetMapping("/seckill/{id}/stock")
    Result<Integer> getStock(@PathVariable("id") Long id);
}
