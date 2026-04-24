package com.flashsale.admin.domain;

/**
 * 秒杀活动状态枚举。
 *
 * <p>状态机转换规则：{@code PENDING → ONGOING → ENDED}。
 * 使用枚举替代原来的 {@code Integer status}，
 * 消除魔法数字，状态转换逻辑内聚在 {@link SeckillActivity} 聚合根中。</p>
 *
 * <p>每个枚举值持有一个整数 {@code code}，用于数据库持久化时的映射。</p>
 */
public enum ActivityStatus {

    /** 待开始（初始状态） */
    PENDING(0),

    /** 进行中（活动已开始） */
    ONGOING(1),

    /** 已结束（活动已结束） */
    ENDED(2);

    private final int code;

    ActivityStatus(int code) {
        this.code = code;
    }

    /**
     * 获取数据库持久化用的整数编码。
     */
    public int code() {
        return code;
    }

    /**
     * 从整数编码反查枚举值。
     *
     * @param code 数据库中存储的状态码
     * @return 对应的枚举值
     * @throws IllegalArgumentException 未知状态码
     */
    public static ActivityStatus fromCode(int code) {
        for (ActivityStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知活动状态码: " + code);
    }
}
