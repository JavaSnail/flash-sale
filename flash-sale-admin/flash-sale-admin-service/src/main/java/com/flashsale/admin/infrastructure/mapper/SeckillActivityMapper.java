package com.flashsale.admin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flashsale.admin.infrastructure.SeckillActivityDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivityDO> {

    @Update("UPDATE t_seckill_activity SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(Long id, int status);
}
