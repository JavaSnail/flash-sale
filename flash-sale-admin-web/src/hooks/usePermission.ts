import useAuthStore from '@/store/useAuthStore';

export default function usePermission(perm: string): boolean {
  return useAuthStore((s) => s.permissions.includes(perm));
}
