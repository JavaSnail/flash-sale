import { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, Select, Tag, Popconfirm, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getUserList, createUser, updateUser, deleteUser, getUser, assignUserRoles } from '@/api/system';
import { getRoleList } from '@/api/system';
import type { AdminUserVO, RoleVO } from '@/types';
import useAuthStore from '@/store/useAuthStore';

export default function UserListPage() {
  const [data, setData] = useState<AdminUserVO[]>([]);
  const [roles, setRoles] = useState<RoleVO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [roleModalOpen, setRoleModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [assignUserId, setAssignUserId] = useState<number | null>(null);
  const [selectedRoleIds, setSelectedRoleIds] = useState<number[]>([]);
  const [form] = Form.useForm();
  const hasPermission = useAuthStore((s) => s.hasPermission);

  const load = async () => {
    setLoading(true);
    try {
      const [users, roleList] = await Promise.all([getUserList(), getRoleList()]);
      setData(users);
      setRoles(roleList);
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
    setModalOpen(true);
  };

  const openEdit = async (id: number) => {
    try {
      const user = await getUser(id);
      setEditingId(id);
      form.setFieldsValue({ realName: user.realName, phone: user.phone, email: user.email, status: user.status });
      setModalOpen(true);
    } catch (err: unknown) {
      message.error((err as Error).message || '加载失败');
    }
  };

  const openAssignRoles = async (id: number) => {
    try {
      const user = await getUser(id);
      setAssignUserId(id);
      setSelectedRoleIds(user.roleIds || []);
      setRoleModalOpen(true);
    } catch (err: unknown) {
      message.error((err as Error).message || '加载失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingId) {
        await updateUser(editingId, values);
        message.success('更新成功');
      } else {
        await createUser(values);
        message.success('创建成功');
      }
      setModalOpen(false);
      load();
    } catch (err: unknown) {
      if ((err as { errorFields?: unknown }).errorFields) return;
      message.error((err as Error).message || '操作失败');
    }
  };

  const handleAssignRoles = async () => {
    if (!assignUserId) return;
    try {
      await assignUserRoles(assignUserId, selectedRoleIds);
      message.success('角色分配成功');
      setRoleModalOpen(false);
      load();
    } catch (err: unknown) {
      message.error((err as Error).message || '操作失败');
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteUser(id);
      message.success('删除成功');
      load();
    } catch (err: unknown) {
      message.error((err as Error).message || '删除失败');
    }
  };

  const columns: ColumnsType<AdminUserVO> = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    { title: '用户名', dataIndex: 'username', render: (v: string) => <span style={{ fontWeight: 600 }}>{v}</span> },
    { title: '姓名', dataIndex: 'realName', render: (v: string) => v || '-' },
    { title: '手机号', dataIndex: 'phone', render: (v: string) => v || '-' },
    { title: '邮箱', dataIndex: 'email', render: (v: string) => v || '-' },
    {
      title: '状态', dataIndex: 'status', width: 80,
      render: (v: number) => v === 1 ? <Tag color="green">启用</Tag> : <Tag color="red">禁用</Tag>,
    },
    { title: '最后登录', dataIndex: 'lastLoginTime', width: 170, render: (v: string) => v || '-' },
    {
      title: '操作', width: 240,
      render: (_, record) => (
        <>
          {hasPermission('admin:user:update') && (
            <Button type="link" size="small" onClick={() => openEdit(record.id)} style={{ fontWeight: 500 }}>编辑</Button>
          )}
          {hasPermission('admin:user:assign-role') && (
            <Button type="link" size="small" onClick={() => openAssignRoles(record.id)} style={{ fontWeight: 500 }}>分配角色</Button>
          )}
          {hasPermission('admin:user:delete') && (
            <Popconfirm title="确认删除该管理员？" onConfirm={() => handleDelete(record.id)} okText="确认" cancelText="取消">
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
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0, letterSpacing: -0.3 }}>管理员管理</h2>
        {hasPermission('admin:user:create') && (
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate} style={{ borderRadius: 10, fontWeight: 600 }}>
            新建管理员
          </Button>
        )}
      </div>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />

      {/* Create / Edit Modal */}
      <Modal title={editingId ? '编辑管理员' : '新建管理员'} open={modalOpen} onOk={handleSubmit} onCancel={() => setModalOpen(false)} okText="确认" cancelText="取消">
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          {!editingId && (
            <>
              <Form.Item name="username" label="用户名" rules={[{ required: true, message: '请输入用户名' }]}>
                <Input placeholder="登录用户名" />
              </Form.Item>
              <Form.Item name="password" label="密码" rules={[{ required: true, message: '请输入密码' }]}>
                <Input.Password placeholder="登录密码" />
              </Form.Item>
            </>
          )}
          {editingId && (
            <Form.Item name="password" label="新密码" extra="留空则不修改密码">
              <Input.Password placeholder="不修改请留空" />
            </Form.Item>
          )}
          <Form.Item name="realName" label="真实姓名"><Input placeholder="可选" /></Form.Item>
          <Form.Item name="phone" label="手机号"><Input placeholder="可选" /></Form.Item>
          <Form.Item name="email" label="邮箱"><Input placeholder="可选" /></Form.Item>
          {editingId && (
            <Form.Item name="status" label="状态">
              <Select options={[{ value: 1, label: '启用' }, { value: 0, label: '禁用' }]} />
            </Form.Item>
          )}
        </Form>
      </Modal>

      {/* Assign Roles Modal */}
      <Modal title="分配角色" open={roleModalOpen} onOk={handleAssignRoles} onCancel={() => setRoleModalOpen(false)} okText="确认" cancelText="取消">
        <Select
          mode="multiple"
          style={{ width: '100%', marginTop: 16 }}
          placeholder="选择角色"
          value={selectedRoleIds}
          onChange={setSelectedRoleIds}
          options={roles.map((r) => ({ value: r.id, label: `${r.roleName} (${r.roleCode})` }))}
        />
      </Modal>
    </div>
  );
}
