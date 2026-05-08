import { useEffect, useState } from 'react';
import { Table, Tag, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { getPermissionList } from '@/api/system';
import type { PermissionVO } from '@/types';

const methodColors: Record<string, string> = {
  GET: 'blue',
  POST: 'green',
  PUT: 'orange',
  DELETE: 'red',
};

export default function PermissionListPage() {
  const [data, setData] = useState<PermissionVO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getPermissionList()
      .then(setData)
      .catch((err: unknown) => message.error((err as Error).message || '加载失败'))
      .finally(() => setLoading(false));
  }, []);

  const columns: ColumnsType<PermissionVO> = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    {
      title: '权限编码', dataIndex: 'permCode',
      render: (v: string) => <Tag>{v}</Tag>,
    },
    {
      title: '权限名称', dataIndex: 'permName',
      render: (v: string) => <span style={{ fontWeight: 600 }}>{v}</span>,
    },
    {
      title: '请求方法', dataIndex: 'apiMethod', width: 100,
      render: (v: string) => v ? <Tag color={methodColors[v] || 'default'}>{v}</Tag> : '-',
    },
    { title: 'API路径', dataIndex: 'apiPath', render: (v: string) => v || '-' },
    { title: '描述', dataIndex: 'description', render: (v: string) => v || '-' },
  ];

  return (
    <div>
      <div style={{ marginBottom: 20 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0, letterSpacing: -0.3 }}>权限管理</h2>
      </div>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />
    </div>
  );
}
