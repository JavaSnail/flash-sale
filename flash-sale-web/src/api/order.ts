import request from '@/utils/request';
import type { OrderDTO } from '@/types';

export function getOrder(id: number) {
  return request.get<never, OrderDTO>(`/order/${id}`);
}
