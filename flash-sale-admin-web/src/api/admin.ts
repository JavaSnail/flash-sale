import request from '@/utils/request';
import type { DashboardDTO, SeckillActivityDTO } from '@/types';

export function getDashboard() {
  return request.get<never, DashboardDTO>('/admin/dashboard');
}

export function getActivities() {
  return request.get<never, SeckillActivityDTO[]>('/admin/activities');
}

export function getActivity(id: number) {
  return request.get<never, SeckillActivityDTO>(`/admin/activities/${id}`);
}

export function createActivity(data: SeckillActivityDTO) {
  return request.post<never, void>('/admin/activities', data);
}

export function warmup() {
  return request.post<never, void>('/admin/warmup');
}
