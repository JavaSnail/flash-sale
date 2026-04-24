package com.flashsale.admin.api.feign;

import com.flashsale.admin.api.dto.DashboardDTO;
import com.flashsale.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "flash-sale-admin", path = "/admin")
public interface AdminFeignClient {

    @GetMapping("/dashboard")
    Result<DashboardDTO> dashboard();
}
