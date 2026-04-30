import request from '@/utils/request';
import type { OrderDTO } from '@/types';

export function getOrderList() {
  return request.get<never, OrderDTO[]>('/order/list');
}

export function getOrder(id: number) {
  return request.get<never, OrderDTO>(`/order/${id}`);
}
