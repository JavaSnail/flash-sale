import { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Radio, Button, Typography, Spin, Result, message } from 'antd';
import { ArrowLeftOutlined, AlipayCircleOutlined, WechatOutlined } from '@ant-design/icons';
import { getOrder } from '@/api/order';
import { createPay, getPayResult } from '@/api/pay';
import type { OrderDTO } from '@/types';
import useAuthStore from '@/store/useAuthStore';

export default function PayPage() {
  const { orderId } = useParams<{ orderId: string }>();
  const navigate = useNavigate();
  const user = useAuthStore((s) => s.user);

  const [order, setOrder] = useState<OrderDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [channel, setChannel] = useState<'ALIPAY' | 'WECHAT'>('ALIPAY');
  const [paying, setPaying] = useState(false);
  const [payStatus, setPayStatus] = useState<number | null>(null);

  const pollRef = useRef<ReturnType<typeof setInterval>>(undefined);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const data = await getOrder(Number(orderId));
        if (data.status === 1) {
          navigate(`/order/${data.id}`, { replace: true });
          return;
        }
        if (data.status === 2) {
          message.warning('订单已取消');
          navigate('/', { replace: true });
          return;
        }
        setOrder(data);
      } catch (err: unknown) {
        message.error((err as Error).message || '加载失败');
      } finally {
        setLoading(false);
      }
    };
    load();
    return () => {
      if (pollRef.current) clearInterval(pollRef.current);
    };
  }, [orderId, navigate]);

  const handlePay = async () => {
    if (!order || !user) return;
    setPaying(true);
    try {
      await createPay({
        orderId: order.id,
        userId: user.id,
        amount: order.orderPrice,
        payChannel: channel,
      });
      setPayStatus(0);

      let count = 0;
      pollRef.current = setInterval(async () => {
        count++;
        if (count > 20) {
          clearInterval(pollRef.current);
          setPayStatus(2);
          return;
        }
        try {
          const r = await getPayResult(order.id);
          if (r.status === 1) {
            setPayStatus(1);
            clearInterval(pollRef.current);
          } else if (r.status === 2) {
            setPayStatus(2);
            clearInterval(pollRef.current);
          }
        } catch {
          /* continue */
        }
      }, 3000);
    } catch (err: unknown) {
      message.error((err as Error).message || '支付创建失败');
    } finally {
      setPaying(false);
    }
  };

  if (loading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
        <span className="loading-text">加载支付信息...</span>
      </div>
    );
  }

  if (!order) return null;

  if (payStatus === 1) {
    return (
      <div className="anim-scale-in" style={{ paddingTop: 40 }}>
        <Result
          status="success"
          title="支付成功"
          extra={
            <Button type="primary" size="large" onClick={() => navigate(`/order/${order.id}`)}
              style={{ height: 44, borderRadius: 12, fontWeight: 600, paddingInline: 32 }}>
              查看订单
            </Button>
          }
        />
      </div>
    );
  }
  if (payStatus === 2) {
    return (
      <div className="anim-scale-in" style={{ paddingTop: 40 }}>
        <Result
          status="error"
          title="支付失败"
          subTitle="请重试"
          extra={
            <Button type="primary" size="large" onClick={() => setPayStatus(null)}
              style={{ height: 44, borderRadius: 12, fontWeight: 600 }}>
              重新支付
            </Button>
          }
        />
      </div>
    );
  }
  if (payStatus === 0) {
    return (
      <div className="page-loading">
        <Spin size="large" />
        <span className="loading-text">支付处理中...</span>
      </div>
    );
  }

  return (
    <div className="anim-fade-up">
      <div className="back-btn" onClick={() => navigate(-1)}>
        <ArrowLeftOutlined /> 返回
      </div>

      <div className="page-card anim-scale-in">
        <div className="page-card-body">
          <div style={{ textAlign: 'center', marginBottom: 32 }}>
            <div style={{ fontSize: 14, color: '#71717A', marginBottom: 8 }}>支付金额</div>
            <Typography.Text strong style={{ fontSize: 42, color: '#DC2626', fontFamily: 'var(--font-display)', fontWeight: 800, letterSpacing: -1.5 }}>
              <span style={{ fontSize: 24, fontWeight: 700 }}>¥</span>
              {order.orderPrice.toFixed(2)}
            </Typography.Text>
            <div style={{ fontSize: 13, color: '#A1A1AA', marginTop: 6 }}>订单编号：{order.id}</div>
          </div>

          <div style={{
            padding: 20,
            background: '#FAF9F6',
            borderRadius: 12,
            marginBottom: 28,
          }}>
            <div style={{ fontSize: 14, fontWeight: 600, marginBottom: 14, color: '#18181B' }}>选择支付方式</div>
            <Radio.Group
              value={channel}
              onChange={(e) => setChannel(e.target.value)}
              style={{ display: 'flex', flexDirection: 'column', gap: 10 }}
            >
              <Radio
                value="ALIPAY"
                style={{
                  padding: '12px 16px',
                  background: channel === 'ALIPAY' ? '#EFF6FF' : '#FFF',
                  borderRadius: 10,
                  border: channel === 'ALIPAY' ? '1.5px solid #3B82F6' : '1.5px solid #E5E5E5',
                  transition: 'all 0.2s ease',
                }}
              >
                <AlipayCircleOutlined style={{ color: '#1677ff', fontSize: 18, marginRight: 8 }} />
                <span style={{ fontWeight: 500 }}>支付宝</span>
              </Radio>
              <Radio
                value="WECHAT"
                style={{
                  padding: '12px 16px',
                  background: channel === 'WECHAT' ? '#F0FDF4' : '#FFF',
                  borderRadius: 10,
                  border: channel === 'WECHAT' ? '1.5px solid #16A34A' : '1.5px solid #E5E5E5',
                  transition: 'all 0.2s ease',
                }}
              >
                <WechatOutlined style={{ color: '#52c41a', fontSize: 18, marginRight: 8 }} />
                <span style={{ fontWeight: 500 }}>微信支付</span>
              </Radio>
            </Radio.Group>
          </div>

          <Button
            type="primary"
            size="large"
            block
            loading={paying}
            onClick={handlePay}
            style={{ height: 52, fontSize: 17, fontWeight: 700, borderRadius: 12 }}
          >
            确认支付 ¥{order.orderPrice.toFixed(2)}
          </Button>
        </div>
      </div>
    </div>
  );
}
