package com.flashsale.seckill.result.adapter.web;

import com.flashsale.common.result.Result;
import com.flashsale.seckill.api.dto.SeckillResultDTO;
import com.flashsale.seckill.result.application.SeckillResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill/result")
@RequiredArgsConstructor
public class SeckillResultController {

    private final SeckillResultService resultService;

    @GetMapping
    public Result<SeckillResultDTO> getResult(@RequestParam Long userId,
                                               @RequestParam Long seckillGoodsId) {
        return Result.success(resultService.getResult(userId, seckillGoodsId));
    }
}
