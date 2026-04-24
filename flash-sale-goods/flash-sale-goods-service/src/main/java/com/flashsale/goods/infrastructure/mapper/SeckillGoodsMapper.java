package com.flashsale.goods.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flashsale.goods.infrastructure.SeckillGoodsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoodsDO> {

    @Update("UPDATE t_seckill_goods SET stock_count = stock_count - 1 WHERE id = #{id} AND stock_count > 0")
    int decreaseStock(Long id);

    @Update("UPDATE t_seckill_goods SET stock_count = stock_count + 1 WHERE id = #{id}")
    int increaseStock(Long id);
}
