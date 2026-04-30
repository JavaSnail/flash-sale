import request from '@/utils/request';
import type { GoodsDTO, SeckillGoodsDTO } from '@/types';

export function getGoodsList() {
  return request.get<never, GoodsDTO[]>('/goods/list');
}

export function getGoods(id: number) {
  return request.get<never, GoodsDTO>(`/goods/${id}`);
}

export function createGoods(data: GoodsDTO) {
  return request.post<never, GoodsDTO>('/goods', data);
}

export function updateGoods(id: number, data: GoodsDTO) {
  return request.put<never, GoodsDTO>(`/goods/${id}`, data);
}

export function deleteGoods(id: number) {
  return request.delete<never, void>(`/goods/${id}`);
}

// ==================== 秒杀商品 ====================

export function getSeckillGoodsList() {
  return request.get<never, SeckillGoodsDTO[]>('/goods/seckill/list');
}

export function getSeckillGoods(id: number) {
  return request.get<never, SeckillGoodsDTO>(`/goods/seckill/${id}`);
}

export function createSeckillGoods(data: Partial<SeckillGoodsDTO>) {
  return request.post<never, SeckillGoodsDTO>('/goods/seckill', data);
}

export function updateSeckillGoods(id: number, data: Partial<SeckillGoodsDTO>) {
  return request.put<never, SeckillGoodsDTO>(`/goods/seckill/${id}`, data);
}

export function deleteSeckillGoods(id: number) {
  return request.delete<never, void>(`/goods/seckill/${id}`);
}
