import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Avatar, Typography, Button, Spin, message } from 'antd';
import { UserOutlined, LogoutOutlined, PhoneOutlined } from '@ant-design/icons';
import { getMe } from '@/api/user';
import useAuthStore from '@/store/useAuthStore';
import type { UserDTO } from '@/types';

export default function ProfilePage() {
  const navigate = useNavigate();
  const logout = useAuthStore((s) => s.logout);
  const setUser = useAuthStore((s) => s.setUser);
  const [userInfo, setUserInfo] = useState<UserDTO | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const data = await getMe();
        setUserInfo(data);
        setUser(data);
      } catch {
        message.error('获取用户信息失败');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [setUser]);

  if (loading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 440, margin: '0 auto' }} className="anim-fade-up">
      <div className="profile-card">
        <div className="profile-card-header">
          <Avatar
            size={72}
            icon={<UserOutlined />}
            style={{
              backgroundColor: 'rgba(255,255,255,0.1)',
              border: '3px solid rgba(255,255,255,0.15)',
              position: 'relative',
              zIndex: 1,
            }}
          />
        </div>
        <div className="profile-card-body">
          <Typography.Title level={4} style={{ marginBottom: 4 }}>
            {userInfo?.nickname || '用户'}
          </Typography.Title>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6, color: '#71717A', fontSize: 14 }}>
            <PhoneOutlined />
            <span>{userInfo?.phone}</span>
          </div>
        </div>
      </div>

      <Button
        type="primary"
        danger
        icon={<LogoutOutlined />}
        block
        size="large"
        style={{ marginTop: 20, height: 48, fontSize: 15, fontWeight: 600, borderRadius: 12 }}
        onClick={() => {
          logout();
          navigate('/login', { replace: true });
        }}
      >
        退出登录
      </Button>
    </div>
  );
}
