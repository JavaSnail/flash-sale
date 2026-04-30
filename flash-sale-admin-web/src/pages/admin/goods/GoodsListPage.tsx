import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Popconfirm, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getGoodsList, deleteGoods } from '@/api/goods';
import type { GoodsDTO } from '@/types';

export default function GoodsListPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<GoodsDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      const list = await getGoodsList();
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

  const handleDelete = async (id: number) => {
    try {
      await deleteGoods(id);
      message.success('删除成功');
      load();
    } catch (err: unknown) {
      message.error((err as Error).message || '删除失败');
    }
  };

  const columns: ColumnsType<GoodsDTO> = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    {
      title: '商品名称',
      dataIndex: 'goodsName',
      render: (v: string) => <span style={{ fontWeight: 600 }}>{v}</span>,
    },
    {
      title: '图片',
      dataIndex: 'goodsImg',
      width: 80,
      render: (v: string) =>
        v ? (
          <img src={v} alt="商品图片" style={{ width: 48, height: 48, objectFit: 'cover', borderRadius: 6 }} />
        ) : (
          '-'
        ),
    },
    {
      title: '价格',
      dataIndex: 'goodsPrice',
      width: 120,
      render: (v: number) => (
        <span style={{ color: '#DC2626', fontWeight: 600 }}>¥{v.toFixed(2)}</span>
      ),
    },
    { title: '库存', dataIndex: 'goodsStock', width: 80 },
    {
      title: '操作',
      width: 140,
      render: (_, record) => (
        <>
          <Button
            type="link"
            size="small"
            onClick={() => navigate(`/goods/edit/${record.id}`)}
            style={{ fontWeight: 500 }}
          >
            编辑
          </Button>
          <Popconfirm
            title="确认删除该商品？"
            onConfirm={() => handleDelete(record.id!)}
            okText="确认"
            cancelText="取消"
          >
            <Button type="link" size="small" danger style={{ fontWeight: 500 }}>
              删除
            </Button>
          </Popconfirm>
        </>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0, letterSpacing: -0.3 }}>
          商品管理
        </h2>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate('/goods/create')}
          style={{ borderRadius: 10, fontWeight: 600 }}
        >
          创建商品
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
