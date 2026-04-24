package com.flashsale.goods.domain;

import java.time.LocalDateTime;

/**
 * 值对象：时间区间（半开区间 [start, end)）。
 *
 * <p>不可变对象，创建时保证 start &lt; end。
 * 封装秒杀活动/商品的"开始 ~ 结束"时间窗口，
 * 提供时间判断行为，替代散落在 Service 层的 if-else。</p>
 *
 * @param start 起始时间（含）
 * @param end   结束时间（不含）
 * @throws IllegalArgumentException 当起止时间为 null 或 start >= end 时抛出
 */
public record TimeRange(LocalDateTime start, LocalDateTime end) {

    public TimeRange {
        if (start == null || end == null) {
            throw new IllegalArgumentException("时间区间起止不能为空");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("起始时间必须早于结束时间");
        }
    }

    /**
     * 判断给定时刻是否在区间内（start <= instant < end）。
     */
    public boolean contains(LocalDateTime instant) {
        return !instant.isBefore(start) && instant.isBefore(end);
    }

    /**
     * 判断给定时刻是否已超过结束时间。
     */
    public boolean hasEnded(LocalDateTime instant) {
        return !instant.isBefore(end);
    }

    /**
     * 判断给定时刻是否已达到或超过起始时间。
     */
    public boolean hasStarted(LocalDateTime instant) {
        return !instant.isBefore(start);
    }
}
