package com.flashsale.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public abstract class BaseEntity implements Serializable {
    
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
