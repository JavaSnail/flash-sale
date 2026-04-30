package com.flashsale.goods.adapter.web;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.common.result.Result;
import com.flashsale.goods.api.dto.GoodsDTO;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import com.flashsale.goods.application.GoodsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "商品管理", description = "秒杀商品查询、库存预热接口")
@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    // ==================== 商品 CRUD ====================

    @Operation(summary = "查询商品列表", description = "获取所有商品信息")
    @GetMapping("/list")
    public Result<List<GoodsDTO>> listGoods() {
        return Result.success(goodsService.listGoods());
    }

    @Operation(summary = "查询商品详情", description = "根据ID获取商品详细信息")
    @GetMapping("/{id}")
    public Result<GoodsDTO> getGoods(@Parameter(description = "商品ID") @PathVariable("id") Long id) {
        return Result.success(goodsService.getGoods(id));
    }

    @Operation(summary = "创建商品", description = "新增一个商品")
    @PostMapping
    public Result<GoodsDTO> createGoods(@RequestBody GoodsDTO dto) {
        return Result.success(goodsService.createGoods(dto));
    }

    @Operation(summary = "更新商品", description = "根据ID更新商品信息")
    @PutMapping("/{id}")
    public Result<GoodsDTO> updateGoods(@Parameter(description = "商品ID") @PathVariable("id") Long id,
        @RequestBody GoodsDTO dto) {
        return Result.success(goodsService.updateGoods(id, dto));
    }

    @Operation(summary = "删除商品", description = "根据ID删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGoods(@Parameter(description = "商品ID") @PathVariable("id") Long id) {
        goodsService.deleteGoods(id);
        return Result.success();
    }

    // ==================== 秒杀商品 ====================

    @Operation(summary = "查询秒杀商品列表", description = "获取所有秒杀商品信息")
    @GetMapping("/seckill/list")
    public Result<List<SeckillGoodsDTO>> listSeckillGoods() {
        return Result.success(goodsService.listSeckillGoods());
    }

    @Operation(summary = "查询秒杀商品详情", description = "根据ID获取秒杀商品详细信息")
    @GetMapping("/seckill/{id}")
    public Result<SeckillGoodsDTO> getSeckillGoods(@Parameter(description = "秒杀商品ID") @PathVariable("id") Long id) {
        return Result.success(goodsService.getSeckillGoods(id));
    }

    @Operation(summary = "创建秒杀商品", description = "新增一个秒杀商品")
    @PostMapping("/seckill")
    public Result<SeckillGoodsDTO> createSeckillGoods(@RequestBody SeckillGoodsDTO dto) {
        return Result.success(goodsService.createSeckillGoods(dto));
    }

    @Operation(summary = "更新秒杀商品", description = "根据ID更新秒杀商品信息")
    @PutMapping("/seckill/{id}")
    public Result<SeckillGoodsDTO> updateSeckillGoods(@Parameter(description = "秒杀商品ID") @PathVariable("id") Long id,
        @RequestBody SeckillGoodsDTO dto) {
        return Result.success(goodsService.updateSeckillGoods(id, dto));
    }

    @Operation(summary = "删除秒杀商品", description = "根据ID删除秒杀商品")
    @DeleteMapping("/seckill/{id}")
    public Result<Void> deleteSeckillGoods(@Parameter(description = "秒杀商品ID") @PathVariable("id") Long id) {
        goodsService.deleteSeckillGoods(id);
        return Result.success();
    }

    @Operation(summary = "查询秒杀商品库存", description = "根据ID获取秒杀商品剩余库存")
    @GetMapping("/seckill/{id}/stock")
    public Result<Integer> getStock(@Parameter(description = "秒杀商品ID") @PathVariable("id") Long id) {
        return Result.success(goodsService.getStock(id));
    }

    @Operation(summary = "库存预热", description = "将秒杀商品库存预热到Redis缓存")
    @PostMapping("/seckill/warmup")
    public Result<Void> warmUp() {
        goodsService.warmUpStock();
        return Result.success();
    }
}
