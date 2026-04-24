package com.flashsale.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
