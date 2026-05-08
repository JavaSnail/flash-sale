package com.flashsale.admin.domain.sys;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Permission {
    private Long id;
    private String permCode;
    private String permName;
    private String apiPath;
    private String apiMethod;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
