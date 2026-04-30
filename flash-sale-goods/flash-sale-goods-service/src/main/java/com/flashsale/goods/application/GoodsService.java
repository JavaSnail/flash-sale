package com.flashsale.goods.application;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.goods.api.dto.GoodsDTO;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import com.flashsale.goods.domain.Goods;
import com.flashsale.goods.domain.GoodsRepository;
import com.flashsale.goods.domain.Money;
import com.flashsale.goods.domain.SeckillGoods;
import com.flashsale.goods.domain.SeckillGoodsRepository;
import com.flashsale.goods.domain.TimeRange;

import lombok.RequiredArgsConstructor;

/**
 * 商品应用服务。
 * <p>
 * 编排秒杀商品查询、库存读取、库存预热等用例。 库存优先从 Redis 缓存读取，降级时回源数据库。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;

    private final SeckillGoodsRepository seckillGoodsRepository;

    private final StringRedisTemplate redisTemplate;

    // ==================== 商品 CRUD ====================

    /**
     * 查询全部商品。
     */
    public List<GoodsDTO> listGoods() {
        return goodsRepository.findAll().stream().map(this::toGoodsDTO).collect(Collectors.toList());
    }

    /**
     * 按 ID 查询商品。
     *
     * @param id 商品 ID
     * @return 商品 DTO
     * @throws BizException 商品不存在
     */
    public GoodsDTO getGoods(Long id) {
        Goods goods = goodsRepository.findById(id).orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        return toGoodsDTO(goods);
    }

    /**
     * 创建商品。
     *
     * @param dto 商品信息
     * @return 带 ID 的商品 DTO
     */
    public GoodsDTO createGoods(GoodsDTO dto) {
        Goods goods = Goods.create(dto.getGoodsName(), dto.getGoodsImg(),
            Money.of(dto.getGoodsPrice()), dto.getGoodsStock());
        goods = goodsRepository.save(goods);
        return toGoodsDTO(goods);
    }

    /**
     * 更新商品。
     *
     * @param id  商品 ID
     * @param dto 更新信息
     * @return 更新后的商品 DTO
     * @throws BizException 商品不存在
     */
    public GoodsDTO updateGoods(Long id, GoodsDTO dto) {
        Goods goods = goodsRepository.findById(id).orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        goods.rename(dto.getGoodsName());
        goods.changeImage(dto.getGoodsImg());
        goods.changePrice(Money.of(dto.getGoodsPrice()));
        goods.resetStock(dto.getGoodsStock());
        goodsRepository.save(goods);
        return toGoodsDTO(goods);
    }

    /**
     * 删除商品。
     *
     * @param id 商品 ID
     */
    public void deleteGoods(Long id) {
        goodsRepository.deleteById(id);
    }

    // ==================== 秒杀商品 ====================

    /**
     * 查询所有秒杀商品列表（含关联商品信息）。
     */
    public List<SeckillGoodsDTO> listSeckillGoods() {
        List<SeckillGoods> list = seckillGoodsRepository.findAll();
        return list.stream().map(sg -> {
            Goods goods = goodsRepository.findById(sg.getGoodsId()).orElse(null);
            return toSeckillDTO(sg, goods);
        }).collect(Collectors.toList());
    }

    /**
     * 查询单个秒杀商品详情。
     *
     * @param id 秒杀商品 ID
     * @return 秒杀商品 DTO（含原始商品信息）
     * @throws BizException 商品不存在
     */
    public SeckillGoodsDTO getSeckillGoods(Long id) {
        SeckillGoods sg = seckillGoodsRepository.findById(id).orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        Goods goods = goodsRepository.findById(sg.getGoodsId()).orElse(null);
        return toSeckillDTO(sg, goods);
    }

    /**
     * 创建秒杀商品。
     */
    public SeckillGoodsDTO createSeckillGoods(SeckillGoodsDTO dto) {
        goodsRepository.findById(dto.getGoodsId())
            .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "关联商品不存在"));
        SeckillGoods sg = SeckillGoods.create(
            dto.getGoodsId(),
            Money.of(dto.getSeckillPrice()),
            dto.getStockCount(),
            new TimeRange(dto.getStartTime(), dto.getEndTime()));
        sg = seckillGoodsRepository.save(sg);
        Goods goods = goodsRepository.findById(sg.getGoodsId()).orElse(null);
        return toSeckillDTO(sg, goods);
    }

    /**
     * 更新秒杀商品（goodsId 不可改）。
     */
    public SeckillGoodsDTO updateSeckillGoods(Long id, SeckillGoodsDTO dto) {
        SeckillGoods sg = seckillGoodsRepository.findById(id)
            .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        sg.changeSeckillPrice(Money.of(dto.getSeckillPrice()));
        sg.resetStock(dto.getStockCount());
        sg.changeTimeRange(new TimeRange(dto.getStartTime(), dto.getEndTime()));
        seckillGoodsRepository.save(sg);
        Goods goods = goodsRepository.findById(sg.getGoodsId()).orElse(null);
        return toSeckillDTO(sg, goods);
    }

    /**
     * 删除秒杀商品。
     */
    public void deleteSeckillGoods(Long id) {
        seckillGoodsRepository.deleteById(id);
    }

    /**
     * 获取秒杀商品实时库存。
     * <p>
     * 优先从 Redis 缓存读取（高并发场景）， 缓存 miss 时回源数据库查询。
     * </p>
     *
     * @param seckillGoodsId 秒杀商品 ID
     * @return 当前库存数量
     */
    public Integer getStock(Long seckillGoodsId) {
        String key = "seckill:stock:" + seckillGoodsId;
        String val = redisTemplate.opsForValue().get(key);
        if (val != null) {
            return Integer.parseInt(val);
        }
        // Redis miss，回源数据库
        SeckillGoods sg = seckillGoodsRepository.findById(seckillGoodsId)
            .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        return sg.getStockCount();
    }

    /**
     * 库存预热：将所有秒杀商品库存加载到 Redis。
     * <p>
     * 在秒杀活动开始前由管理后台触发， 将数据库库存写入 Redis 并设置 2 小时过期， 避免秒杀高峰时大量请求穿透到数据库。
     * </p>
     */
    public void warmUpStock() {
        List<SeckillGoods> list = seckillGoodsRepository.findAll();
        for (SeckillGoods sg : list) {
            String stockKey = "seckill:stock:" + sg.getId();
            redisTemplate.opsForValue().set(stockKey, String.valueOf(sg.getStockCount()), 2, TimeUnit.HOURS);
            // 清除 soldout 标记（可能由之前未预热的请求误设）
            redisTemplate.delete("seckill:soldout:" + sg.getId());
            // 通知 seckill 服务清除内存中的 soldout 标记
            redisTemplate.convertAndSend("seckill:soldout:channel", "reset:" + sg.getId());
        }
    }

    // ==================== DTO 转换 ====================

    private GoodsDTO toGoodsDTO(Goods goods) {
        GoodsDTO dto = new GoodsDTO();
        dto.setId(goods.getId());
        dto.setGoodsName(goods.getGoodsName());
        dto.setGoodsImg(goods.getGoodsImg());
        dto.setGoodsPrice(goods.getGoodsPrice().amount());
        dto.setGoodsStock(goods.getGoodsStock());
        return dto;
    }

    /**
     * 组合秒杀商品与原始商品信息，构建 DTO。 价格从 {@link Money} 值对象中提取 BigDecimal。
     */
    private SeckillGoodsDTO toSeckillDTO(SeckillGoods sg, Goods goods) {
        SeckillGoodsDTO dto = new SeckillGoodsDTO();
        dto.setId(sg.getId());
        dto.setGoodsId(sg.getGoodsId());
        dto.setSeckillPrice(sg.getSeckillPrice().amount());
        dto.setStockCount(sg.getStockCount());
        dto.setStartTime(sg.getStartTime());
        dto.setEndTime(sg.getEndTime());
        if (goods != null) {
            dto.setGoodsName(goods.getGoodsName());
            dto.setGoodsImg(goods.getGoodsImg());
            dto.setGoodsPrice(goods.getGoodsPrice().amount());
        }
        return dto;
    }
}
