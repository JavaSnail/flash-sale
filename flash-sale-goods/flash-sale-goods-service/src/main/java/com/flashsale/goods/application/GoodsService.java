package com.flashsale.goods.application;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.goods.api.dto.GoodsDTO;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import com.flashsale.goods.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final SeckillGoodsRepository seckillGoodsRepository;
    private final StringRedisTemplate redisTemplate;

    public List<SeckillGoodsDTO> listSeckillGoods() {
        List<SeckillGoods> list = seckillGoodsRepository.findAll();
        return list.stream().map(sg -> {
            Goods goods = goodsRepository.findById(sg.getGoodsId()).orElse(null);
            return toSeckillDTO(sg, goods);
        }).collect(Collectors.toList());
    }

    public SeckillGoodsDTO getSeckillGoods(Long id) {
        SeckillGoods sg = seckillGoodsRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        Goods goods = goodsRepository.findById(sg.getGoodsId()).orElse(null);
        return toSeckillDTO(sg, goods);
    }

    public Integer getStock(Long seckillGoodsId) {
        String key = "seckill:stock:" + seckillGoodsId;
        String val = redisTemplate.opsForValue().get(key);
        if (val != null) {
            return Integer.parseInt(val);
        }
        SeckillGoods sg = seckillGoodsRepository.findById(seckillGoodsId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        return sg.getStockCount();
    }

    /**
     * 库存预热：将秒杀商品库存加载到Redis
     */
    public void warmUpStock() {
        List<SeckillGoods> list = seckillGoodsRepository.findAll();
        for (SeckillGoods sg : list) {
            String key = "seckill:stock:" + sg.getId();
            redisTemplate.opsForValue().set(key, String.valueOf(sg.getStockCount()), 2, TimeUnit.HOURS);
        }
    }

    private SeckillGoodsDTO toSeckillDTO(SeckillGoods sg, Goods goods) {
        SeckillGoodsDTO dto = new SeckillGoodsDTO();
        dto.setId(sg.getId());
        dto.setGoodsId(sg.getGoodsId());
        dto.setSeckillPrice(sg.getSeckillPrice());
        dto.setStockCount(sg.getStockCount());
        dto.setStartTime(sg.getStartTime());
        dto.setEndTime(sg.getEndTime());
        if (goods != null) {
            dto.setGoodsName(goods.getGoodsName());
            dto.setGoodsImg(goods.getGoodsImg());
            dto.setGoodsPrice(goods.getGoodsPrice());
        }
        return dto;
    }
}
