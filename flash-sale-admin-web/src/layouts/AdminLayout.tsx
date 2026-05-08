import { useMemo, useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button } from 'antd';
import type { ItemType } from 'antd/es/menu/interface';
import {
  DashboardOutlined,
  ShoppingOutlined,
  UnorderedListOutlined,
  FileTextOutlined,
  AccountBookOutlined,
  FireOutlined,
  SettingOutlined,
  UserOutlined,
  TeamOutlined,
  SafetyOutlined,
  MenuOutlined,
  ThunderboltOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import useAuthStore from '@/store/useAuthStore';
import { adminLogout } from '@/api/auth';
import type { AdminMenuVO } from '@/types';

const { Sider, Header, Content } = Layout;

// Map icon name strings to Ant Design icon components
const iconMap: Record<string, React.ReactNode> = {
  DashboardOutlined: <DashboardOutlined />,
  ShoppingOutlined: <ShoppingOutlined />,
  UnorderedListOutlined: <UnorderedListOutlined />,
  FileTextOutlined: <FileTextOutlined />,
  AccountBookOutlined: <AccountBookOutlined />,
  FireOutlined: <FireOutlined />,
  SettingOutlined: <SettingOutlined />,
  UserOutlined: <UserOutlined />,
  TeamOutlined: <TeamOutlined />,
  SafetyOutlined: <SafetyOutlined />,
  MenuOutlined: <MenuOutlined />,
  ThunderboltOutlined: <ThunderboltOutlined />,
};

function buildMenuItems(menus: AdminMenuVO[]): ItemType[] {
  return menus
    .filter((m) => m.visible === 1 && m.menuType !== 3) // exclude hidden & button types
    .map((menu) => {
      const icon = menu.icon ? iconMap[menu.icon] : undefined;
      if (menu.children && menu.children.length > 0) {
        const childItems = buildMenuItems(menu.children);
        if (childItems.length > 0) {
          return {
            key: menu.routePath || String(menu.id),
            icon,
            label: menu.menuName,
            children: childItems,
          };
        }
      }
      return {
        key: menu.routePath || String(menu.id),
        icon,
        label: menu.menuName,
      };
    });
}

export default function AdminLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const logout = useAuthStore((s) => s.logout);
  const menus = useAuthStore((s) => s.menus);
  const realName = useAuthStore((s) => s.realName);
  const username = useAuthStore((s) => s.username);
  const [collapsed, setCollapsed] = useState(false);

  const menuItems = useMemo(() => buildMenuItems(menus), [menus]);

  // Find current open submenu key
  const openKeys = useMemo(() => {
    for (const menu of menus) {
      if (menu.children?.some((c) => c.routePath === location.pathname)) {
        return [menu.routePath || String(menu.id)];
      }
    }
    return [];
  }, [menus, location.pathname]);

  const handleLogout = async () => {
    try {
      await adminLogout();
    } catch {
      // ignore
    }
    logout();
    navigate('/login');
  };

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
          defaultOpenKeys={openKeys}
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
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <span style={{ color: 'rgba(255,255,255,0.65)', fontSize: 14 }}>
              {realName || username}
            </span>
            <Button type="text" icon={<LogoutOutlined />} onClick={handleLogout}>
              退出
            </Button>
          </div>
        </Header>
        <Content className="admin-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
