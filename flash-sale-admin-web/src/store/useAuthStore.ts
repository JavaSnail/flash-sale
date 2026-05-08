import { create } from 'zustand';
import type { AdminLoginVO, AdminMenuVO } from '@/types';

interface AuthState {
  token: string | null;
  userId: number | null;
  username: string | null;
  realName: string | null;
  avatar: string | null;
  roles: string[];
  permissions: string[];
  menus: AdminMenuVO[];
  setLoginData: (data: AdminLoginVO) => void;
  hasPermission: (perm: string) => boolean;
  logout: () => void;
}

const useAuthStore = create<AuthState>((set, get) => ({
  token: localStorage.getItem('admin_token'),
  userId: null,
  username: null,
  realName: null,
  avatar: null,
  roles: [],
  permissions: [],
  menus: [],

  setLoginData: (data: AdminLoginVO) => {
    localStorage.setItem('admin_token', data.token);
    set({
      token: data.token,
      userId: data.userId,
      username: data.username,
      realName: data.realName,
      avatar: data.avatar,
      roles: data.roles,
      permissions: data.permissions,
      menus: data.menus,
    });
  },

  hasPermission: (perm: string) => {
    return get().permissions.includes(perm);
  },

  logout: () => {
    localStorage.removeItem('admin_token');
    set({
      token: null,
      userId: null,
      username: null,
      realName: null,
      avatar: null,
      roles: [],
      permissions: [],
      menus: [],
    });
  },
}));

export default useAuthStore;
