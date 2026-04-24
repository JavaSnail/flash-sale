package com.flashsale.goods.infrastructure;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_goods")
public class GoodsDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String goodsName;
    private String goodsImg;
    private BigDecimal goodsPrice;
    private Integer goodsStock;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
