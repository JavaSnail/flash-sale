import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Tag, Button, Popconfirm, Space, message } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { getSeckillGoodsList, deleteSeckillGoods } from '@/api/goods';
import type { SeckillGoodsDTO } from '@/types';

function deriveStatus(startTime: string, endTime: string): { text: string; color: string } {
  const now = dayjs();
  if (now.isBefore(dayjs(startTime))) return { text: '未开始', color: 'blue' };
  if (now.isAfter(dayjs(endTime))) return { text: '已结束', color: 'default' };
  return { text: '进行中', color: 'green' };
}

export default function ActivityListPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<SeckillGoodsDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      const list = await getSeckillGoodsList();
      setData(list);
    } catch (err: unknown) {
      message.error((err as Error).message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleDelete = async (id: number) => {
    try {
      await deleteSeckillGoods(id);
      message.success('删除成功');
      load();
    } catch (err: unknown) {
      message.error((err as Error).message || '删除失败');
    }
  };

  const columns: ColumnsType<SeckillGoodsDTO> = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    {
      title: '商品名称',
      dataIndex: 'goodsName',
      render: (v: string) => <span style={{ fontWeight: 600 }}>{v}</span>,
    },
    {
      title: '原价',
      dataIndex: 'goodsPrice',
      render: (v: number) => `¥${v.toFixed(2)}`,
    },
    {
      title: '秒杀价格',
      dataIndex: 'seckillPrice',
      render: (v: number) => (
        <span style={{ color: '#DC2626', fontWeight: 600 }}>¥{v.toFixed(2)}</span>
      ),
    },
    { title: '库存', dataIndex: 'stockCount', width: 80 },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      render: (v: string) => dayjs(v).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      render: (v: string) => dayjs(v).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: '状态',
      width: 100,
      render: (_, record) => {
        const s = deriveStatus(record.startTime, record.endTime);
        return <Tag color={s.color}>{s.text}</Tag>;
      },
    },
    {
      title: '操作',
      width: 140,
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            onClick={() => navigate(`/activities/edit/${record.id}`)}
            style={{ fontWeight: 500 }}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除该秒杀商品？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0, letterSpacing: -0.3 }}>
          秒杀商品管理
        </h2>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate('/activities/create')}
          style={{ borderRadius: 10, fontWeight: 600 }}
        >
          创建秒杀商品
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={false}
      />
    </div>
  );
}
