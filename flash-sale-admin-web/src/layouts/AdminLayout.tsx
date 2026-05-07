import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button } from 'antd';
import {
  DashboardOutlined,
  ShoppingOutlined,
  UnorderedListOutlined,
  FileTextOutlined,
  AccountBookOutlined,
  FireOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import useAuthStore from '@/store/useAuthStore';

const { Sider, Header, Content } = Layout;

const menuItems = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: '数据看板' },
  { key: '/goods', icon: <ShoppingOutlined />, label: '商品管理' },
  { key: '/activities', icon: <UnorderedListOutlined />, label: '秒杀商品管理' },
  { key: '/orders', icon: <FileTextOutlined />, label: '订单管理' },
  { key: '/payments', icon: <AccountBookOutlined />, label: '支付管理' },
  { key: '/warmup', icon: <FireOutlined />, label: '缓存预热' },
];

export default function AdminLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const logout = useAuthStore((s) => s.logout);
  const [collapsed, setCollapsed] = useState(false);

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        width={220}
        collapsedWidth={64}
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        trigger={null}
        className="admin-sider"
      >
        <div className={`admin-sider-logo ${collapsed ? 'collapsed' : ''}`}>
          <div className="logo-dot" />
          {!collapsed && <span>Flash-Sale Admin</span>}
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
          inlineCollapsed={collapsed}
        />
      </Sider>
      <Layout>
        <Header
          className="admin-header"
          style={{
            padding: '0 24px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{ fontSize: 16 }}
          />
          <Button
            type="text"
            icon={<LogoutOutlined />}
            onClick={() => {
              logout();
              navigate('/login');
            }}
          >
            退出
          </Button>
        </Header>
        <Content className="admin-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
