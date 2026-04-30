import { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Spin } from 'antd';
import AppHeader from '@/components/AppHeader';
import useAuthStore from '@/store/useAuthStore';
import { getMe } from '@/api/user';

export default function CLayout() {
  const token = useAuthStore((s) => s.token);
  const user = useAuthStore((s) => s.user);
  const setUser = useAuthStore((s) => s.setUser);
  const logout = useAuthStore((s) => s.logout);
  const [loading, setLoading] = useState(!user && !!token);

  useEffect(() => {
    if (token && !user) {
      setLoading(true);
      getMe()
        .then(setUser)
        .catch(() => logout())
        .finally(() => setLoading(false));
    }
  }, [token, user, setUser, logout]);

  if (loading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div className="c-layout">
      <AppHeader />
      <div className="c-layout-content">
        <Outlet />
      </div>
    </div>
  );
}
