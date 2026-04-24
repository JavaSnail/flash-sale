package com.flashsale.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flashsale.order.infrastructure.OrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper extends BaseMapper<OrderDO> {

    @Update("UPDATE t_order SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(Long id, int status);
}
