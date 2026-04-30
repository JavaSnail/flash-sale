import { useEffect, useState } from 'react';
import { Row, Col, Spin, message } from 'antd';
import {
  ShoppingCartOutlined,
  CheckCircleOutlined,
  UserOutlined,
  FireOutlined,
} from '@ant-design/icons';
import { getDashboard } from '@/api/admin';
import type { DashboardDTO } from '@/types';

export default function DashboardPage() {
  const [data, setData] = useState<DashboardDTO | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const d = await getDashboard();
        setData(d);
      } catch (err: unknown) {
        message.error((err as Error).message || '加载失败');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
      </div>
    );
  }

  if (!data) return null;

  const items = [
    { title: '总订单数', value: data.totalOrders, icon: <ShoppingCartOutlined />, cls: 'stat-blue' },
    { title: '成功订单', value: data.successOrders, icon: <CheckCircleOutlined />, cls: 'stat-green' },
    { title: '总用户数', value: data.totalUsers, icon: <UserOutlined />, cls: 'stat-purple' },
    { title: '活跃活动', value: data.activeActivities, icon: <FireOutlined />, cls: 'stat-red' },
  ];

  return (
    <div>
      <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 24, letterSpacing: -0.3 }}>
        数据概览
      </h2>
      <Row gutter={[20, 20]}>
        {items.map((item, idx) => (
          <Col xs={24} sm={12} lg={6} key={item.title}>
            <div className={`stat-card ${item.cls} anim-fade-up anim-delay-${idx + 1}`}>
              <div className="stat-card-icon">{item.icon}</div>
              <div className="stat-card-value">
                {item.value.toLocaleString()}
              </div>
              <div className="stat-card-title">{item.title}</div>
            </div>
          </Col>
        ))}
      </Row>
    </div>
  );
}
