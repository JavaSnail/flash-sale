import request from '@/utils/request';
import type { SeckillGoodsDTO } from '@/types';

export function getSeckillList() {
  return request.get<never, SeckillGoodsDTO[]>('/goods/seckill/list');
}

export function getSeckillDetail(id: number) {
  return request.get<never, SeckillGoodsDTO>(`/goods/seckill/${id}`);
}

export function getSeckillStock(id: number) {
  return request.get<never, number>(`/goods/seckill/${id}/stock`);
}
