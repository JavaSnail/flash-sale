import { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, InputNumber, Select, Tag, TreeSelect, Popconfirm, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/system';
import type { AdminMenuVO } from '@/types';
import useAuthStore from '@/store/useAuthStore';

const menuTypeMap: Record<number, { label: string; color: string }> = {
  1: { label: '目录', color: 'blue' },
  2: { label: '菜单', color: 'green' },
  3: { label: '按钮', color: 'orange' },
};

function menuToTreeSelectData(menus: AdminMenuVO[]): { title: string; value: number; children?: ReturnType<typeof menuToTreeSelectData> }[] {
  return menus.map((m) => ({
    title: m.menuName,
    value: m.id,
    children: m.children?.length ? menuToTreeSelectData(m.children) : undefined,
  }));
}

// Flatten tree for table display
function flattenMenus(menus: AdminMenuVO[], depth = 0): (AdminMenuVO & { depth: number })[] {
  const result: (AdminMenuVO & { depth: number })[] = [];
  for (const m of menus) {
    result.push({ ...m, depth });
    if (m.children?.length) {
      result.push(...flattenMenus(m.children, depth + 1));
    }
  }
  return result;
}

export default function MenuListPage() {
  const [treeData, setTreeData] = useState<AdminMenuVO[]>([]);
  const [flatData, setFlatData] = useState<(AdminMenuVO & { depth: number })[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form] = Form.useForm();
  const hasPermission = useAuthStore((s) => s.hasPermission);

  const load = async () => {
    setLoading(true);
    try {
      const tree = await getMenuTree();
      setTreeData(tree);
      setFlatData(flattenMenus(tree));
    } catch (err: unknown) {
      message.error((err as Error).message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => {
    setEditingId(null);
    form.resetFields();
    form.setFieldsValue({ parentId: 0, menuType: 2, sortOrder: 0, visible: 1 });
    setModalOpen(true);
  };

  const openEdit = async (record: AdminMenuVO) => {
    setEditingId(record.id);
    form.setFieldsValue({
      parentId: record.parentId,
      menuName: record.menuName,
      menuType: record.menuType,
      routePath: record.routePath,
      componentPath: record.componentPath,
      permCode: record.permCode,
      icon: record.icon,
      sortOrder: record.sortOrder,
      visible: record.visible,
    });
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingId) {
        await updateMenu(editingId, values);
        message.success('更新成功');
      } else {
        await createMenu(values);
        message.success('创建成功');
      }
      setModalOpen(false);
      load();
    } catch (err: unknown) {
      if ((err as { errorFields?: unknown }).errorFields) return;
      message.error((err as Error).message || '操作失败');
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteMenu(id);
      message.success('删除成功');
      load();
    } catch (err: unknown) {
      message.error((err as Error).message || '删除失败');
    }
  };

  const columns: ColumnsType<AdminMenuVO & { depth: number }> = [
    {
      title: '菜单名称', dataIndex: 'menuName',
      render: (v: string, record) => (
        <span style={{ paddingLeft: record.depth * 24, fontWeight: 600 }}>{v}</span>
      ),
    },
    {
      title: '类型', dataIndex: 'menuType', width: 80,
      render: (v: number) => {
        const info = menuTypeMap[v];
        return info ? <Tag color={info.color}>{info.label}</Tag> : '-';
      },
    },
    { title: '路由', dataIndex: 'routePath', render: (v: string) => v || '-' },
    { title: '组件', dataIndex: 'componentPath', render: (v: string) => v || '-' },
    { title: '权限编码', dataIndex: 'permCode', render: (v: string) => v ? <Tag>{v}</Tag> : '-' },
    { title: '图标', dataIndex: 'icon', width: 100, render: (v: string) => v || '-' },
    { title: '排序', dataIndex: 'sortOrder', width: 70 },
    {
      title: '显示', dataIndex: 'visible', width: 70,
      render: (v: number) => v === 1 ? <Tag color="green">是</Tag> : <Tag color="red">否</Tag>,
    },
    {
      title: '操作', width: 140,
      render: (_, record) => (
        <>
          {hasPermission('admin:menu:update') && (
            <Button type="link" size="small" onClick={() => openEdit(record)} style={{ fontWeight: 500 }}>编辑</Button>
          )}
          {hasPermission('admin:menu:delete') && (
            <Popconfirm title="确认删除该菜单？" onConfirm={() => handleDelete(record.id)} okText="确认" cancelText="取消">
              <Button type="link" size="small" danger style={{ fontWeight: 500 }}>删除</Button>
            </Popconfirm>
          )}
        </>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0, letterSpacing: -0.3 }}>菜单管理</h2>
        {hasPermission('admin:menu:create') && (
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate} style={{ borderRadius: 10, fontWeight: 600 }}>
            新建菜单
          </Button>
        )}
      </div>
      <Table columns={columns} dataSource={flatData} rowKey="id" loading={loading} pagination={false} />

      {/* Create / Edit */}
      <Modal title={editingId ? '编辑菜单' : '新建菜单'} open={modalOpen} onOk={handleSubmit} onCancel={() => setModalOpen(false)} okText="确认" cancelText="取消" width={560}>
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item name="parentId" label="父级菜单">
            <TreeSelect
              treeData={[{ title: '顶层', value: 0 }, ...menuToTreeSelectData(treeData)]}
              placeholder="选择父级"
              allowClear
              treeDefaultExpandAll
            />
          </Form.Item>
          <Form.Item name="menuName" label="菜单名称" rules={[{ required: true, message: '请输入菜单名称' }]}>
            <Input placeholder="菜单名称" />
          </Form.Item>
          <Form.Item name="menuType" label="类型" rules={[{ required: true, message: '请选择类型' }]}>
            <Select options={[{ value: 1, label: '目录' }, { value: 2, label: '菜单' }, { value: 3, label: '按钮' }]} />
          </Form.Item>
          <Form.Item name="routePath" label="路由路径"><Input placeholder="/system/users" /></Form.Item>
          <Form.Item name="componentPath" label="组件路径"><Input placeholder="pages/system/UserList" /></Form.Item>
          <Form.Item name="permCode" label="权限编码"><Input placeholder="admin:user:list" /></Form.Item>
          <Form.Item name="icon" label="图标"><Input placeholder="SettingOutlined" /></Form.Item>
          <Form.Item name="sortOrder" label="排序"><InputNumber min={0} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="visible" label="是否显示">
            <Select options={[{ value: 1, label: '显示' }, { value: 0, label: '隐藏' }]} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
