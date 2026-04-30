import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Descriptions, Tag, Button, Spin, Empty, Typography, message } from 'antd';
import { ArrowLeftOutlined, ClockCircleOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { getOrder } from '@/api/order';
import type { OrderDTO } from '@/types';
import CountdownTimer from '@/components/CountdownTimer';

const statusMap: Record<number, { text: string; color: string }> = {
  0: { text: '待支付', color: 'orange' },
  1: { text: '已支付', color: 'green' },
  2: { text: '已取消', color: 'default' },
};

export default function OrderDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [order, setOrder] = useState<OrderDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const data = await getOrder(Number(id));
        setOrder(data);
      } catch (err: unknown) {
        const error = err as Error & { code?: number };
        if (error.code === 6001) {
          setNotFound(true);
        } else {
          message.error(error.message || '加载失败');
        }
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  if (loading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
        <span className="loading-text">加载订单详情...</span>
      </div>
    );
  }

  if (notFound || !order) {
    return (
      <div style={{ paddingTop: 80 }}>
        <Empty description="订单不存在" />
      </div>
    );
  }

  const statusInfo = statusMap[order.status] || statusMap[0];
  const payDeadline = dayjs(order.createTime).add(15, 'minute').toISOString();

  return (
    <div className="anim-fade-up">
      <div className="back-btn" onClick={() => navigate(-1)}>
        <ArrowLeftOutlined /> 返回
      </div>

      <div className="page-card anim-scale-in">
        <div className="page-card-body">
          <Descriptions
            title={<span style={{ fontSize: 20, fontWeight: 700, letterSpacing: -0.3 }}>订单详情</span>}
            column={1}
            bordered
            style={{ marginBottom: 0 }}
          >
            <Descriptions.Item label="订单编号">
              <span style={{ fontWeight: 600 }}>{order.id}</span>
            </Descriptions.Item>
            <Descriptions.Item label="下单时间">
              {dayjs(order.createTime).format('YYYY-MM-DD HH:mm:ss')}
            </Descriptions.Item>
            <Descriptions.Item label="订单金额">
              <Typography.Text strong style={{ fontSize: 20, color: '#DC2626' }}>
                ¥{order.orderPrice.toFixed(2)}
              </Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="订单状态">
              <Tag color={statusInfo.color}>{statusInfo.text}</Tag>
            </Descriptions.Item>
          </Descriptions>

          {order.status === 0 && (
            <div className="pay-countdown-box">
              <div style={{ marginBottom: 12, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8 }}>
                <ClockCircleOutlined style={{ color: '#D97706' }} />
                <Typography.Text type="secondary">支付倒计时</Typography.Text>
              </div>
              <div style={{ marginBottom: 20 }}>
                <CountdownTimer
                  targetTime={payDeadline}
                  onEnd={() => window.location.reload()}
                />
              </div>
              <Button
                type="primary"
                size="large"
                onClick={() => navigate(`/pay/${order.id}`)}
                style={{ height: 48, fontSize: 16, fontWeight: 700, borderRadius: 12, paddingInline: 48 }}
              >
                立即支付
              </Button>
            </div>
          )}

          {order.status === 2 && (
            <div style={{ marginTop: 24, textAlign: 'center', padding: 20, background: '#FAFAF8', borderRadius: 12 }}>
              <Typography.Text type="secondary">订单已超时取消</Typography.Text>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
