import { useEffect, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { Spin } from 'antd';
import useAuthStore from '@/store/useAuthStore';
import { getAdminMe } from '@/api/auth';

export default function AdminGuard() {
  const token = useAuthStore((s) => s.token);
  const userId = useAuthStore((s) => s.userId);
  const setLoginData = useAuthStore((s) => s.setLoginData);
  const logout = useAuthStore((s) => s.logout);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // If we have a token but no user data (page refresh), fetch user info
    if (token && !userId) {
      setLoading(true);
      getAdminMe()
        .then((data) => {
          setLoginData({ ...data, token });
        })
        .catch(() => {
          logout();
        })
        .finally(() => {
          setLoading(false);
        });
    }
  }, [token, userId, setLoginData, logout]);

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (loading || (!userId && token)) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <Spin size="large" />
      </div>
    );
  }

  return <Outlet />;
}
