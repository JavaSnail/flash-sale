import request from '@/utils/request';
import type { AdminLoginVO } from '@/types';

export function adminLogin(username: string, password: string) {
  return request.post<never, AdminLoginVO>('/admin/auth/login', { username, password });
}

export function adminLogout() {
  return request.post<never, void>('/admin/auth/logout');
}

export function getAdminMe() {
  return request.get<never, AdminLoginVO>('/admin/auth/me');
}
