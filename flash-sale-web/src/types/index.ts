export interface UserDTO {
  id: number;
  nickname: string;
  phone: string;
}

export interface SeckillGoodsDTO {
  id: number;
  goodsId: number;
  goodsName: string;
  goodsImg: string;
  goodsPrice: number;
  seckillPrice: number;
  stockCount: number;
  startTime: string;
  endTime: string;
}

export interface SeckillResultDTO {
  orderId: number | null;
  status: number; // 0=排队中, 1=成功, -1=失败
  message: string;
}

export interface SeckillCommand {
  userId: number;
  seckillGoodsId: number;
  token: string;
}

export interface OrderDTO {
  id: number;
  userId: number;
  seckillGoodsId: number;
  goodsId: number;
  orderPrice: number;
  status: number; // 0=待支付, 1=已支付, 2=已取消
  createTime: string;
}

export interface PayRequestDTO {
  orderId: number;
  userId: number;
  amount: number;
  payChannel: 'ALIPAY' | 'WECHAT';
}

export interface PayResultDTO {
  payId: number;
  orderId: number;
  status: number; // 0=待支付, 1=成功, 2=失败
  tradeNo: string;
}

export interface DashboardDTO {
  totalOrders: number;
  successOrders: number;
  totalUsers: number;
  activeActivities: number;
}

export interface SeckillActivityDTO {
  id?: number;
  activityName: string;
  goodsId: number;
  seckillPrice: number;
  stockCount: number;
  startTime: string;
  endTime: string;
  status?: number; // 0=未开始, 1=进行中, 2=已结束
}

export const ErrorCode = {
  SUCCESS: 0,
  PARAM_ERROR: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500,
  SECKILL_OVER: 5001,
  REPEAT_SECKILL: 5002,
  SECKILL_FAIL: 5003,
  CAPTCHA_ERROR: 5004,
  TOKEN_INVALID: 5005,
  ACCESS_LIMIT: 5006,
  ORDER_NOT_EXIST: 6001,
  ORDER_TIMEOUT: 6002,
  PAY_FAIL: 7001,
} as const;

export interface Result<T> {
  code: number;
  msg: string;
  data: T;
}
