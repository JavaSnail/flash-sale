import { create } from 'zustand';
import type { UserDTO } from '@/types';

interface AuthState {
  token: string | null;
  user: UserDTO | null;
  setToken: (token: string) => void;
  setUser: (user: UserDTO) => void;
  logout: () => void;
}

const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('token'),
  user: null,
  setToken: (token: string) => {
    localStorage.setItem('token', token);
    set({ token });
  },
  setUser: (user: UserDTO) => set({ user }),
  logout: () => {
    localStorage.removeItem('token');
    set({ token: null, user: null });
  },
}));

export default useAuthStore;
