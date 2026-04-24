package com.flashsale.admin.application;

import com.flashsale.admin.api.dto.DashboardDTO;
import com.flashsale.admin.api.dto.SeckillActivityDTO;
import com.flashsale.admin.domain.SeckillActivity;
import com.flashsale.admin.domain.SeckillActivityRepository;
import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.goods.api.feign.GoodsFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final SeckillActivityRepository activityRepository;
    private final GoodsFeignClient goodsFeignClient;
    private final StringRedisTemplate redisTemplate;

    public List<SeckillActivityDTO> listActivities() {
        return activityRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public SeckillActivityDTO getActivity(Long id) {
        SeckillActivity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        return toDTO(activity);
    }

    public void createActivity(SeckillActivityDTO dto) {
        SeckillActivity activity = new SeckillActivity();
        activity.setActivityName(dto.getActivityName());
        activity.setGoodsId(dto.getGoodsId());
        activity.setSeckillPrice(dto.getSeckillPrice());
        activity.setStockCount(dto.getStockCount());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setStatus(0);
        activityRepository.save(activity);
    }

    public void triggerWarmUp() {
        goodsFeignClient.listSeckillGoods(); // trigger cache loading
        log.info("Stock warm-up triggered");
    }

    public DashboardDTO dashboard() {
        DashboardDTO dto = new DashboardDTO();
        // Placeholder - would query from order/user services
        dto.setTotalOrders(0L);
        dto.setSuccessOrders(0L);
        dto.setTotalUsers(0L);
        dto.setActiveActivities((long) activityRepository.findAll().size());
        return dto;
    }

    private SeckillActivityDTO toDTO(SeckillActivity a) {
        SeckillActivityDTO dto = new SeckillActivityDTO();
        dto.setId(a.getId());
        dto.setActivityName(a.getActivityName());
        dto.setGoodsId(a.getGoodsId());
        dto.setSeckillPrice(a.getSeckillPrice());
        dto.setStockCount(a.getStockCount());
        dto.setStartTime(a.getStartTime());
        dto.setEndTime(a.getEndTime());
        dto.setStatus(a.getStatus());
        return dto;
    }
}
