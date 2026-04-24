package com.flashsale.common.constant;

/**
 * RocketMQ Topic 与 Tag 常量。
 * <p>
 * 统一管理消息队列的 Topic/Tag 命名，确保生产者与消费者使用一致的常量。
 * </p>
 */
public interface MQConstants {

    /** 秒杀主题：承载秒杀下单、库存回滚等消息 */
    String SECKILL_TOPIC = "seckill-topic";

    /** 秒杀下单 Tag：秒杀服务发送，订单服务消费 */
    String SECKILL_ORDER_TAG = "seckill-order";

    /** 订单超时主题：延时消息，到期后触发订单自动取消 */
    String ORDER_TIMEOUT_TOPIC = "order-timeout-topic";

    /** 支付回调通知主题：支付服务发送，订单服务消费以更新订单状态 */
    String PAY_NOTIFY_TOPIC = "pay-notify-topic";

    /** 库存回滚 Tag：订单取消/超时后回滚秒杀库存 */
    String STOCK_ROLLBACK_TAG = "stock-rollback";
}
