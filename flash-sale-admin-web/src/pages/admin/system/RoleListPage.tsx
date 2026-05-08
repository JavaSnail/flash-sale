import { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, InputNumber, Tag, Tree, Popconfirm, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import {
  getRoleList, getRole, createRole, updateRole, deleteRole,
  assignRolePermissions, assignRoleMenus,
  getPermissionList, getMenuTree,
} from '@/api/system';
import type { RoleVO, PermissionVO, AdminMenuVO } from '@/types';
import useAuthStore from '@/store/useAuthStore';

function menuToTreeData(menus: AdminMenuVO[]): { title: string; key: number; children?: { title: string; key: number }[] }[] {
  return menus.map((m) => ({
    title: m.menuName,
    key: m.id,
    children: m.children?.length ? menuToTreeData(m.children) : undefined,
  }));
}

export default function RoleListPage() {
  const [data, setData] = useState<RoleVO[]>([]);
  const [permissions, setPermissions] = useState<PermissionVO[]>([]);
  const [menuTree, setMenuTree] = useState<AdminMenuVO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [permModalOpen, setPermModalOpen] = useState(false);
  const [menuModalOpen, setMenuModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [assignRoleId, setAssignRoleId] = useState<number | null>(null);
  const [selectedPermIds, setSelectedPermIds] = useState<number[]>([]);
  const [checkedMenuKeys, setCheckedMenuKeys] = useState<number[]>([]);
  const [form] = Form.useForm();
  const hasPermission = useAuthStore((s) => s.hasPermission);

  const load = async () => {
    setLoading(true);
    try {
      const list = await getRoleList();
      setData(list);
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

  const openEdit = (record: RoleVO) => {
    setEditingId(record.id);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingId) {
        await updateRole(editingId, values);
        message.success('更新成功');
      } else {
        await createRole(values);
        message.success('创建成功');
      }
      setModalOpen(false);
      load();
    } catch (err: unknown) {
      if ((err as { errorFields?: unknown }).errorFields) return;
      message.error((err as Error).message || '操作失败');
    }
  };

  const openAssignPerms = async (roleId: number) => {
    try {
      const [perms, role] = await Promise.all([getPermissionList(), getRole(roleId)]);
      setPermissions(perms);
      setAssignRoleId(roleId);
      setSelectedPermIds(role.permissionIds || []);
      setPermModalOpen(true);
    } catch (err: unknown) {
      message.error((err as Error).message || '加载失败');
    }
  };

  const handleAssignPerms = async () => {
    if (!assignRoleId) return;
    try {
      await assignRolePermissions(assignRoleId, selectedPermIds);
      message.success('权限分配成功');
      setPermModalOpen(false);
    } catch (err: unknown) {
      message.error((err as Error).message || '操作失败');
    }
  };

  const openAssignMenus = async (roleId: number) => {
    try {
      const tree = await getMenuTree();
      setMenuTree(tree);
      setAssignRoleId(roleId);
      // Load role's current menu IDs - for now get from role detail
      const role = await getRole(roleId);
      // We don't have menuIds on RoleVO, so reset to empty. The backend will need to return this.
      // For now, use the permissionIds as a proxy or leave empty.
      setCheckedMenuKeys([]);
      void role;
      setMenuModalOpen(true);
    } catch (err: unknown) {
      message.error((err as Error).message || '加载失败');
    }
  };

  const handleAssignMenus = async () => {
    if (!assignRoleId) return;
    try {
      await assignRoleMenus(assignRoleId, checkedMenuKeys);
      message.success('菜单分配成功');
      setMenuModalOpen(false);
    } catch (err: unknown) {
      message.error((err as Error).message || '操作失败');
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteRole(id);
      message.success('删除成功');
      load();
    } catch (err: unknown) {
      message.error((err as Error).message || '删除失败');
    }
  };

  const columns: ColumnsType<RoleVO> = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    { title: '角色编码', dataIndex: 'roleCode', render: (v: string) => <Tag>{v}</Tag> },
    { title: '角色名称', dataIndex: 'roleName', render: (v: string) => <span style={{ fontWeight: 600 }}>{v}</span> },
    { title: '描述', dataIndex: 'description', render: (v: string) => v || '-' },
    {
      title: '状态', dataIndex: 'status', width: 80,
      render: (v: number) => v === 1 ? <Tag color="green">启用</Tag> : <Tag color="red">禁用</Tag>,
    },
    { title: '排序', dataIndex: 'sortOrder', width: 70 },
    {
      title: '操作', width: 320,
      render: (_, record) => (
        <>
          {hasPermission('admin:role:update') && (
            <Button type="link" size="small" onClick={() => openEdit(record)} style={{ fontWeight: 500 }}>编辑</Button>
          )}
          {hasPermission('admin:role:assign-perm') && (
            <Button type="link" size="small" onClick={() => openAssignPerms(record.id)} style={{ fontWeight: 500 }}>分配权限</Button>
          )}
          {hasPermission('admin:role:assign-menu') && (
            <Button type="link" size="small" onClick={() => openAssignMenus(record.id)} style={{ fontWeight: 500 }}>分配菜单</Button>
          )}
          {hasPermission('admin:role:delete') && (
            <Popconfirm title="确认删除该角色？" onConfirm={() => handleDelete(record.id)} okText="确认" cancelText="取消">
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
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0, letterSpacing: -0.3 }}>角色管理</h2>
        {hasPermission('admin:role:create') && (
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate} style={{ borderRadius: 10, fontWeight: 600 }}>
            新建角色
          </Button>
        )}
      </div>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />

      {/* Create / Edit */}
      <Modal title={editingId ? '编辑角色' : '新建角色'} open={modalOpen} onOk={handleSubmit} onCancel={() => setModalOpen(false)} okText="确认" cancelText="取消">
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item name="roleCode" label="角色编码" rules={[{ required: true, message: '请输入角色编码' }]}>
            <Input placeholder="如 OPERATOR" disabled={!!editingId} />
          </Form.Item>
          <Form.Item name="roleName" label="角色名称" rules={[{ required: true, message: '请输入角色名称' }]}>
            <Input placeholder="如 运营人员" />
          </Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea rows={2} placeholder="可选" /></Form.Item>
          <Form.Item name="sortOrder" label="排序"><InputNumber min={0} style={{ width: '100%' }} /></Form.Item>
        </Form>
      </Modal>

      {/* Assign Permissions */}
      <Modal title="分配权限" open={permModalOpen} onOk={handleAssignPerms} onCancel={() => setPermModalOpen(false)} okText="确认" cancelText="取消" width={600}>
        <div style={{ maxHeight: 400, overflow: 'auto', marginTop: 16 }}>
          {permissions.map((p) => (
            <label key={p.id} style={{ display: 'flex', alignItems: 'center', padding: '6px 0', gap: 8 }}>
              <input
                type="checkbox"
                checked={selectedPermIds.includes(p.id)}
                onChange={(e) => {
                  if (e.target.checked) {
                    setSelectedPermIds([...selectedPermIds, p.id]);
                  } else {
                    setSelectedPermIds(selectedPermIds.filter((id) => id !== p.id));
                  }
                }}
              />
              <span style={{ fontWeight: 500 }}>{p.permName}</span>
              <Tag style={{ marginLeft: 4 }}>{p.permCode}</Tag>
            </label>
          ))}
        </div>
      </Modal>

      {/* Assign Menus */}
      <Modal title="分配菜单" open={menuModalOpen} onOk={handleAssignMenus} onCancel={() => setMenuModalOpen(false)} okText="确认" cancelText="取消">
        <Tree
          checkable
          defaultExpandAll
          checkedKeys={checkedMenuKeys}
          onCheck={(keys) => setCheckedMenuKeys(keys as number[])}
          treeData={menuToTreeData(menuTree)}
          style={{ marginTop: 16 }}
        />
      </Modal>
    </div>
  );
}
