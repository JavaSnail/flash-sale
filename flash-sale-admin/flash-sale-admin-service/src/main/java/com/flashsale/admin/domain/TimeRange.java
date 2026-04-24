package com.flashsale.admin.domain;

import java.time.LocalDateTime;

/**
 * 值对象：时间区间（半开区间 [start, end)）。
 *
 * <p>与 goods 模块的 TimeRange 值对象功能相同，
 * 因 admin 模块不依赖 goods-service 而独立定义。</p>
 *
 * @param start 起始时间（含）
 * @param end   结束时间（不含）
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
     * 判断给定时刻是否在区间内。
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
     * 判断给定时刻是否已达到起始时间。
     */
    public boolean hasStarted(LocalDateTime instant) {
        return !instant.isBefore(start);
    }
}
