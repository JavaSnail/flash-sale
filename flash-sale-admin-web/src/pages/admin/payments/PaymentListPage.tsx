import { useEffect, useState } from 'react';
import { Table, Tag, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { getPaymentList } from '@/api/pay';
import type { PayResultDTO } from '@/types';
import dayjs from 'dayjs';

const statusMap: Record<number, { text: string; color: string }> = {
  0: { text: '待支付', color: 'orange' },
  1: { text: '成功', color: 'green' },
  2: { text: '失败', color: 'red' },
};

const channelMap: Record<string, string> = {
  ALIPAY: '支付宝',
  WECHAT: '微信',
};

export default function PaymentListPage() {
  const [data, setData] = useState<PayResultDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      const list = await getPaymentList();
      setData(list);
    } catch (err: unknown) {
      message.error((err as Error).message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const columns: ColumnsType<PayResultDTO> = [
    { title: '支付ID', dataIndex: 'payId', width: 80 },
    { title: '订单ID', dataIndex: 'orderId', width: 80 },
    { title: '用户ID', dataIndex: 'userId', width: 80 },
    {
      title: '金额',
      dataIndex: 'amount',
      width: 120,
      render: (v: number) => (
        <span style={{ color: '#DC2626', fontWeight: 600 }}>¥{v.toFixed(2)}</span>
      ),
    },
    {
      title: '支付渠道',
      dataIndex: 'payChannel',
      width: 100,
      render: (v: string) => (
        <Tag color={v === 'ALIPAY' ? 'blue' : 'green'}>{channelMap[v] || v}</Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (v: number) => {
        const s = statusMap[v] || { text: '未知', color: 'default' };
        return <Tag color={s.color}>{s.text}</Tag>;
      },
    },
    { title: '交易号', dataIndex: 'tradeNo', width: 200, render: (v: string) => v || '-' },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      render: (v: string) => (v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0, letterSpacing: -0.3 }}>
          支付管理
        </h2>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="payId"
        loading={loading}
        pagination={false}
      />
    </div>
  );
}
