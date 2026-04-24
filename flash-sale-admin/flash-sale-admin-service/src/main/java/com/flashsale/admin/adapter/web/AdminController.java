package com.flashsale.admin.adapter.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.admin.api.dto.DashboardDTO;
import com.flashsale.admin.api.dto.SeckillActivityDTO;
import com.flashsale.admin.application.AdminService;
import com.flashsale.common.result.Result;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/activities")
    public Result<List<SeckillActivityDTO>> listActivities() {
        return Result.success(adminService.listActivities());
    }

    @GetMapping("/activities/{id}")
    public Result<SeckillActivityDTO> getActivity(@PathVariable Long id) {
        return Result.success(adminService.getActivity(id));
    }

    @PostMapping("/activities")
    public Result<Void> createActivity(@RequestBody SeckillActivityDTO dto) {
        adminService.createActivity(dto);
        return Result.success();
    }

    @PostMapping("/warmup")
    public Result<Void> warmUp() {
        adminService.triggerWarmUp();
        return Result.success();
    }

    @GetMapping("/dashboard")
    public Result<DashboardDTO> dashboard() {
        return Result.success(adminService.dashboard());
    }
}
