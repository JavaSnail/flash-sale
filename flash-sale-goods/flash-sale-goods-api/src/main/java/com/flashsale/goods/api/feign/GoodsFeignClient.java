package com.flashsale.goods.api.feign;

import com.flashsale.common.result.Result;
import com.flashsale.goods.api.dto.GoodsDTO;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "flash-sale-goods", path = "/goods")
public interface GoodsFeignClient {

    @GetMapping("/list")
    Result<List<GoodsDTO>> listGoods();

    @GetMapping("/seckill/list")
    Result<List<SeckillGoodsDTO>> listSeckillGoods();

    @GetMapping("/seckill/{id}")
    Result<SeckillGoodsDTO> getSeckillGoods(@PathVariable("id") Long id);

    @PostMapping("/seckill")
    Result<SeckillGoodsDTO> createSeckillGoods(@RequestBody SeckillGoodsDTO dto);

    @PutMapping("/seckill/{id}")
    Result<SeckillGoodsDTO> updateSeckillGoods(@PathVariable("id") Long id, @RequestBody SeckillGoodsDTO dto);

    @DeleteMapping("/seckill/{id}")
    Result<Void> deleteSeckillGoods(@PathVariable("id") Long id);

    @PostMapping("/seckill/warmup")
    Result<Void> warmUpStock();

    @GetMapping("/seckill/{id}/stock")
    Result<Integer> getStock(@PathVariable("id") Long id);
}
