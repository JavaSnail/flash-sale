import { useEffect, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { Spin } from 'antd';
import useAuthStore from '@/store/useAuthStore';
import { getMe } from '@/api/user';

export default function AuthGuard() {
  const token = useAuthStore((s) => s.token);
  const user = useAuthStore((s) => s.user);
  const setUser = useAuthStore((s) => s.setUser);
  const logout = useAuthStore((s) => s.logout);
  const [loading, setLoading] = useState(!user && !!token);

  useEffect(() => {
    if (token && !user) {
      getMe()
        .then((u) => setUser(u))
        .catch(() => logout())
        .finally(() => setLoading(false));
    }
  }, [token, user, setUser, logout]);

  if (!token) {
    return <Navigate to="/login" replace />;
  }
  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    );
  }
  return <Outlet />;
}
