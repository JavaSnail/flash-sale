package com.flashsale.admin.api.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class DashboardDTO implements Serializable {
    private Long totalOrders;

    private Long successOrders;

    private Long totalUsers;

    private Long activeActivities;
}
