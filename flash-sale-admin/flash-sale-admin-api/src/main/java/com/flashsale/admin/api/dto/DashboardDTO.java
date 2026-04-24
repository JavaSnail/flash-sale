package com.flashsale.admin.api.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DashboardDTO implements Serializable {
    private Long totalOrders;
    private Long successOrders;
    private Long totalUsers;
    private Long activeActivities;
}
