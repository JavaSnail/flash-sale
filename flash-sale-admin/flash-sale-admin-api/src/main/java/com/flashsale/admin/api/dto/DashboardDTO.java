package com.flashsale.admin.api.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据看板")
@Data
public class DashboardDTO implements Serializable {
    @Schema(description = "总订单数", example = "10000")
    private Long totalOrders;

    @Schema(description = "成功订单数", example = "8000")
    private Long successOrders;

    @Schema(description = "总用户数", example = "50000")
    private Long totalUsers;

    @Schema(description = "进行中的活动数", example = "3")
    private Long activeActivities;
}
