package com.flashsale.seckill.result.application;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flashsale.seckill.api.dto.SeckillResultDTO;
import com.flashsale.seckill.result.domain.SeckillResultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeckillResultService {

    private final SeckillResultRepository resultRepository;

    public SeckillResultDTO getResult(Long userId, Long seckillGoodsId) {
        Optional<String> result = resultRepository.find(userId, seckillGoodsId);
        if (result.isEmpty()) {
            return SeckillResultDTO.queuing();
        }
        String val = result.get();
        if (val.startsWith("fail:")) {
            return SeckillResultDTO.fail(val.substring(5));
        }
        return SeckillResultDTO.success(Long.parseLong(val));
    }
}
