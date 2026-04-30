import request from '@/utils/request';
import type { UserDTO } from '@/types';

export function login(phone: string, password: string) {
  return request.post<never, string>('/user/login', { phone, password });
}

export function register(phone: string, password: string, nickname?: string) {
  return request.post<never, void>('/user/register', { phone, password, nickname });
}

export function getMe() {
  return request.get<never, UserDTO>('/user/me');
}

export function getUserById(id: number) {
  return request.get<never, UserDTO>(`/user/${id}`);
}
