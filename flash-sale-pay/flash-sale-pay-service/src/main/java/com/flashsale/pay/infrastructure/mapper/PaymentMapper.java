package com.flashsale.pay.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flashsale.pay.infrastructure.PaymentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PaymentMapper extends BaseMapper<PaymentDO> {

    @Update("UPDATE t_payment SET status = #{status}, trade_no = #{tradeNo}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") int status, @Param("tradeNo") String tradeNo);
}
