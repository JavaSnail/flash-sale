package com.flashsale.goods.application;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import com.flashsale.goods.domain.Goods;
import com.flashsale.goods.domain.GoodsRepository;
import com.flashsale.goods.domain.Money;
import com.flashsale.goods.domain.SeckillGoods;
import com.flashsale.goods.domain.SeckillGoodsRepository;

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
            String key = "seckill:stock:" + sg.getId();
            redisTemplate.opsForValue().set(key, String.valueOf(sg.getStockCount()), 2, TimeUnit.HOURS);
        }
    }

    // ==================== DTO 转换 ====================

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
