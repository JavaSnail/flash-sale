import { useNavigate } from 'react-router-dom';
import { Dropdown } from 'antd';
import { UserOutlined, LogoutOutlined, ThunderboltFilled } from '@ant-design/icons';
import useAuthStore from '@/store/useAuthStore';

export default function AppHeader() {
  const navigate = useNavigate();
  const logout = useAuthStore((s) => s.logout);

  return (
    <div className="app-header">
      <div className="app-header-logo" onClick={() => navigate('/')}>
        <div className="logo-icon">
          <ThunderboltFilled />
        </div>
        <span className="logo-text">Flash-Sale</span>
      </div>

      <Dropdown
        menu={{
          items: [
            {
              key: 'profile',
              icon: <UserOutlined />,
              label: '个人中心',
              onClick: () => navigate('/me'),
            },
            { type: 'divider' },
            {
              key: 'logout',
              icon: <LogoutOutlined />,
              label: '退出登录',
              danger: true,
              onClick: () => {
                logout();
                navigate('/login');
              },
            },
          ],
        }}
        trigger={['click']}
        placement="bottomRight"
      >
        <div className="app-header-user">
          <UserOutlined />
          <span>我的</span>
        </div>
      </Dropdown>
    </div>
  );
}
