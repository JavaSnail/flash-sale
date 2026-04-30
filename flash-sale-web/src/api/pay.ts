import request from '@/utils/request';
import type { PayRequestDTO, PayResultDTO } from '@/types';

export function createPay(data: PayRequestDTO) {
  return request.post<never, number>('/pay/create', data);
}

export function getPayResult(orderId: number) {
  return request.get<never, PayResultDTO>(`/pay/${orderId}`);
}
