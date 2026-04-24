package com.flashsale.common.constant;

/**
 * Redis Key 前缀常量。
 * <p>
 * 统一管理所有 Redis Key 的命名规范，避免魔法字符串散落在各服务中。 Key 命名规则：{@code 业务域:用途:具体标识}。
 * </p>
 */
public interface RedisConstants {

    /** 秒杀商品库存：seckill:stock:{seckillGoodsId} → 库存数量 */
    String SECKILL_STOCK_PREFIX = "seckill:stock:";

    /** 秒杀令牌：seckill:token:{userId}:{seckillGoodsId} → token 值 */
    String SECKILL_TOKEN_PREFIX = "seckill:token:";

    /** 秒杀结果（供前端轮询）：seckill:result:{userId}:{seckillGoodsId} → orderId 或 fail 信息 */
    String SECKILL_RESULT_PREFIX = "seckill:result:";

    /** 秒杀售罄标记：seckill:soldout:{seckillGoodsId} → "1"（内存级快速判断，避免打到 Redis 库存） */
    String SECKILL_SOLDOUT_PREFIX = "seckill:soldout:";

    /** 用户会话：user:session:{userId} */
    String USER_SESSION_PREFIX = "user:session:";

    /** 验证码：captcha:{uuid} → 验证码值 */
    String CAPTCHA_PREFIX = "captcha:";

    /** 接口限流计数器：access:{uri}:{ip} → 访问次数 */
    String ACCESS_LIMIT_PREFIX = "access:";

    /** 商品详情缓存：goods:detail:{goodsId} → JSON */
    String GOODS_DETAIL_PREFIX = "goods:detail:";

    /** 订单令牌（防重复提交）：order:token:{userId} → token 值 */
    String ORDER_TOKEN_PREFIX = "order:token:";
}
