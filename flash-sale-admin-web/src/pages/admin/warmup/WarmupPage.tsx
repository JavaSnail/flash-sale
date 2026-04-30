import { useState } from 'react';
import { Button, Typography, Popconfirm, Tag, message } from 'antd';
import { FireFilled, CheckCircleOutlined, CloseCircleOutlined, ThunderboltOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { warmup } from '@/api/admin';

export default function WarmupPage() {
  const [loading, setLoading] = useState(false);
  const [lastTime, setLastTime] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean | null>(null);

  const handleWarmup = async () => {
    setLoading(true);
    try {
      await warmup();
      setLastTime(dayjs().format('YYYY-MM-DD HH:mm:ss'));
      setSuccess(true);
      message.success('预热成功');
    } catch (err: unknown) {
      setSuccess(false);
      message.error((err as Error).message || '预热失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 24, letterSpacing: -0.3 }}>
        缓存预热
      </h2>

      <div style={{
        padding: 28,
        background: 'linear-gradient(135deg, #FFF7ED 0%, #FEF3C7 100%)',
        borderRadius: 16,
        border: '1px solid rgba(245,158,11,0.12)',
        marginBottom: 28,
      }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: 16 }}>
          <div style={{
            width: 44,
            height: 44,
            borderRadius: 12,
            background: 'linear-gradient(135deg, #F59E0B 0%, #EA580C 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontSize: 20,
            flexShrink: 0,
          }}>
            <ThunderboltOutlined />
          </div>
          <div>
            <div style={{ fontWeight: 600, fontSize: 15, marginBottom: 6, color: '#92400E' }}>关于缓存预热</div>
            <Typography.Text style={{ color: '#A16207', fontSize: 14, lineHeight: 1.7 }}>
              将秒杀商品库存数据预热到 Redis 缓存中，提升秒杀开始时的并发处理能力。建议在活动开始前 30 分钟执行。
            </Typography.Text>
          </div>
        </div>
      </div>

      <Popconfirm title="确定执行缓存预热？" okText="确定" cancelText="取消" onConfirm={handleWarmup}>
        <Button
          type="primary"
          icon={<FireFilled />}
          loading={loading}
          size="large"
          style={{
            height: 48,
            fontSize: 15,
            fontWeight: 700,
            borderRadius: 12,
            paddingInline: 32,
            background: 'linear-gradient(135deg, #DC2626 0%, #EA580C 100%)',
            border: 'none',
            boxShadow: '0 4px 16px rgba(220,38,38,0.25)',
          }}
        >
          执行预热
        </Button>
      </Popconfirm>

      {lastTime && (
        <div style={{
          marginTop: 24,
          padding: 20,
          background: '#FAFAF8',
          borderRadius: 12,
          display: 'flex',
          alignItems: 'center',
          gap: 16,
        }}>
          <div>
            <div style={{ fontSize: 13, color: '#71717A', marginBottom: 4 }}>上次预热时间</div>
            <div style={{ fontWeight: 600 }}>{lastTime}</div>
          </div>
          <div style={{ marginLeft: 'auto' }}>
            {success ? (
              <Tag icon={<CheckCircleOutlined />} color="success">预热成功</Tag>
            ) : (
              <Tag icon={<CloseCircleOutlined />} color="error">预热失败</Tag>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
