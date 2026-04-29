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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "管理后台", description = "秒杀活动管理、数据看板接口")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "查询活动列表", description = "获取所有秒杀活动")
    @GetMapping("/activities")
    public Result<List<SeckillActivityDTO>> listActivities() {
        return Result.success(adminService.listActivities());
    }

    @Operation(summary = "查询活动详情", description = "根据ID获取秒杀活动详情")
    @GetMapping("/activities/{id}")
    public Result<SeckillActivityDTO> getActivity(@Parameter(description = "活动ID") @PathVariable Long id) {
        return Result.success(adminService.getActivity(id));
    }

    @Operation(summary = "创建秒杀活动", description = "新建一个秒杀活动")
    @PostMapping("/activities")
    public Result<Void> createActivity(@RequestBody SeckillActivityDTO dto) {
        adminService.createActivity(dto);
        return Result.success();
    }

    @Operation(summary = "触发预热", description = "触发库存缓存预热")
    @PostMapping("/warmup")
    public Result<Void> warmUp() {
        adminService.triggerWarmUp();
        return Result.success();
    }

    @Operation(summary = "数据看板", description = "获取系统运营数据概览")
    @GetMapping("/dashboard")
    public Result<DashboardDTO> dashboard() {
        return Result.success(adminService.dashboard());
    }
}
