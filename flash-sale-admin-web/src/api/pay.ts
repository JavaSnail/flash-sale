import request from '@/utils/request';
import type { PayResultDTO } from '@/types';

export function getPaymentList() {
  return request.get<never, PayResultDTO[]>('/pay/list');
}

export function getPayment(orderId: number) {
  return request.get<never, PayResultDTO>(`/pay/${orderId}`);
}
