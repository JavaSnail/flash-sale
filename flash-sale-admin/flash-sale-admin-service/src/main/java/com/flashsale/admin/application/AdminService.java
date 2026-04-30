package com.flashsale.admin.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.flashsale.admin.api.dto.DashboardDTO;
import com.flashsale.admin.api.dto.SeckillActivityDTO;
import com.flashsale.admin.domain.Money;
import com.flashsale.admin.domain.SeckillActivity;
import com.flashsale.admin.domain.SeckillActivityRepository;
import com.flashsale.admin.domain.TimeRange;
import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.goods.api.feign.GoodsFeignClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理后台应用服务。
 * <p>
 * 编排秒杀活动管理用例，包含活动 CRUD、库存预热触发和仪表盘统计。 活动创建委托给 {@link SeckillActivity#create} 领域工厂， 本服务不包含业务规则。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final SeckillActivityRepository activityRepository;

    private final GoodsFeignClient goodsFeignClient;

    private final StringRedisTemplate redisTemplate;

    /**
     * 查询所有秒杀活动。
     */
    public List<SeckillActivityDTO> listActivities() {
        return activityRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 查询单个秒杀活动详情。
     *
     * @param id 活动 ID
     * @throws BizException 活动不存在
     */
    public SeckillActivityDTO getActivity(Long id) {
        SeckillActivity activity = activityRepository.findById(id)
            .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND));
        return toDTO(activity);
    }

    /**
     * 创建新秒杀活动。
     * <p>
     * 通过 {@link SeckillActivity#create} 领域工厂构建聚合根， 初始状态为 PENDING，参数校验由工厂和值对象内部完成。
     * </p>
     *
     * @param dto 活动信息
     */
    public void createActivity(SeckillActivityDTO dto) {
        SeckillActivity activity = SeckillActivity.create(dto.getActivityName(), dto.getGoodsId(),
            Money.of(dto.getSeckillPrice()), dto.getStockCount(), new TimeRange(dto.getStartTime(), dto.getEndTime()));
        activityRepository.save(activity);
    }

    /**
     * 触发库存预热（通过 Feign 调用商品服务加载库存到 Redis）。
     */
    public void triggerWarmUp() {
        goodsFeignClient.warmUpStock();
        log.info("Stock warm-up triggered");
    }

    /**
     * 获取仪表盘统计数据。
     */
    public DashboardDTO dashboard() {
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalOrders(0L);
        dto.setSuccessOrders(0L);
        dto.setTotalUsers(0L);
        dto.setActiveActivities((long) activityRepository.findAll().size());
        return dto;
    }

    // ==================== DTO 转换 ====================

    private SeckillActivityDTO toDTO(SeckillActivity a) {
        SeckillActivityDTO dto = new SeckillActivityDTO();
        dto.setId(a.getId());
        dto.setActivityName(a.getActivityName());
        dto.setGoodsId(a.getGoodsId());
        dto.setSeckillPrice(a.getSeckillPrice().amount());
        dto.setStockCount(a.getStockCount());
        dto.setStartTime(a.getStartTime());
        dto.setEndTime(a.getEndTime());
        dto.setStatus(a.getStatus().code());
        return dto;
    }
}
