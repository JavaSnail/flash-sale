package com.flashsale.order.infrastructure;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class OrderDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long seckillGoodsId;
    private Long goodsId;
    private BigDecimal orderPrice;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
