package com.flashsale.admin.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.flashsale.admin.api.dto.DashboardDTO;
import com.flashsale.common.result.Result;

@FeignClient(name = "flash-sale-admin", path = "/admin")
public interface AdminFeignClient {

    @GetMapping("/dashboard")
    Result<DashboardDTO> dashboard();
}
