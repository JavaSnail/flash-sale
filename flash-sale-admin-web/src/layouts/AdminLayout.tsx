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

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider width={220} className="admin-sider">
        <div className="admin-sider-logo">
          <div className="logo-dot" />
          <span>Flash-Sale Admin</span>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header
          className="admin-header"
          style={{
            padding: '0 24px',
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'center',
          }}
        >
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
