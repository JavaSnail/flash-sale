package com.flashsale.goods.adapter.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.common.result.Result;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import com.flashsale.goods.application.GoodsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping("/seckill/list")
    public Result<List<SeckillGoodsDTO>> listSeckillGoods() {
        return Result.success(goodsService.listSeckillGoods());
    }

    @GetMapping("/seckill/{id}")
    public Result<SeckillGoodsDTO> getSeckillGoods(@PathVariable Long id) {
        return Result.success(goodsService.getSeckillGoods(id));
    }

    @GetMapping("/seckill/{id}/stock")
    public Result<Integer> getStock(@PathVariable Long id) {
        return Result.success(goodsService.getStock(id));
    }

    @PostMapping("/seckill/warmup")
    public Result<Void> warmUp() {
        goodsService.warmUpStock();
        return Result.success();
    }
}
