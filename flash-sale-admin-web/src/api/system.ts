import request from '@/utils/request';
import type { AdminUserVO, RoleVO, PermissionVO, AdminMenuVO } from '@/types';

// ---- Admin User ----

export function getUserList() {
  return request.get<never, AdminUserVO[]>('/admin/sys/users');
}

export function getUser(id: number) {
  return request.get<never, AdminUserVO>(`/admin/sys/users/${id}`);
}

export function createUser(data: { username: string; password: string; realName?: string; phone?: string; email?: string }) {
  return request.post<never, void>('/admin/sys/users', data);
}

export function updateUser(id: number, data: { password?: string; realName?: string; phone?: string; email?: string; status?: number }) {
  return request.put<never, void>(`/admin/sys/users/${id}`, data);
}

export function deleteUser(id: number) {
  return request.delete<never, void>(`/admin/sys/users/${id}`);
}

export function assignUserRoles(userId: number, roleIds: number[]) {
  return request.put<never, void>(`/admin/sys/users/${userId}/roles`, roleIds);
}

// ---- Role ----

export function getRoleList() {
  return request.get<never, RoleVO[]>('/admin/sys/roles');
}

export function getRole(id: number) {
  return request.get<never, RoleVO>(`/admin/sys/roles/${id}`);
}

export function createRole(data: { roleCode: string; roleName: string; description?: string; sortOrder?: number }) {
  return request.post<never, void>('/admin/sys/roles', data);
}

export function updateRole(id: number, data: { roleCode?: string; roleName?: string; description?: string; status?: number; sortOrder?: number }) {
  return request.put<never, void>(`/admin/sys/roles/${id}`, data);
}

export function deleteRole(id: number) {
  return request.delete<never, void>(`/admin/sys/roles/${id}`);
}

export function assignRolePermissions(roleId: number, permissionIds: number[]) {
  return request.put<never, void>(`/admin/sys/roles/${roleId}/permissions`, permissionIds);
}

export function assignRoleMenus(roleId: number, menuIds: number[]) {
  return request.put<never, void>(`/admin/sys/roles/${roleId}/menus`, menuIds);
}

// ---- Permission ----

export function getPermissionList() {
  return request.get<never, PermissionVO[]>('/admin/sys/permissions');
}

// ---- Menu ----

export function getMenuTree() {
  return request.get<never, AdminMenuVO[]>('/admin/sys/menus');
}

export function getMenuFlat() {
  return request.get<never, AdminMenuVO[]>('/admin/sys/menus/flat');
}

export function createMenu(data: {
  parentId?: number; menuName: string; menuType: number;
  routePath?: string; componentPath?: string; permCode?: string;
  icon?: string; sortOrder?: number; visible?: number;
}) {
  return request.post<never, void>('/admin/sys/menus', data);
}

export function updateMenu(id: number, data: {
  parentId?: number; menuName?: string; menuType?: number;
  routePath?: string; componentPath?: string; permCode?: string;
  icon?: string; sortOrder?: number; visible?: number; status?: number;
}) {
  return request.put<never, void>(`/admin/sys/menus/${id}`, data);
}

export function deleteMenu(id: number) {
  return request.delete<never, void>(`/admin/sys/menus/${id}`);
}
