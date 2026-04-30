import request from '@/utils/request';
import type { SeckillCommand, SeckillResultDTO } from '@/types';

export function getCaptchaUrl(userId: number, seckillGoodsId: number) {
  return `/seckill/captcha?userId=${userId}&seckillGoodsId=${seckillGoodsId}&t=${Date.now()}`;
}

export function getToken(userId: number, seckillGoodsId: number, captcha: string) {
  return request.post<never, string>(
    `/seckill/token?userId=${userId}&seckillGoodsId=${seckillGoodsId}&captcha=${captcha}`,
  );
}

export function execute(cmd: SeckillCommand) {
  return request.post<never, void>('/seckill/execute', cmd);
}

export function getResult(userId: number, seckillGoodsId: number) {
  return request.get<never, SeckillResultDTO>(
    `/seckill/result?userId=${userId}&seckillGoodsId=${seckillGoodsId}`,
  );
}
