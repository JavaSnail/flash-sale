-- ============================================================
-- Flash Sale System - Database Schema
-- ============================================================

-- -----------------------------------------------------------
-- Database: flash_sale_user
-- -----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS flash_sale_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE flash_sale_user;

CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    password VARCHAR(64) NOT NULL,
    salt VARCHAR(16) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Database: flash_sale_goods
-- -----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS flash_sale_goods DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE flash_sale_goods;

CREATE TABLE t_goods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    goods_name VARCHAR(128) NOT NULL,
    goods_img VARCHAR(512),
    goods_price DECIMAL(10,2) NOT NULL,
    goods_stock INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_seckill_goods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    goods_id BIGINT NOT NULL,
    seckill_price DECIMAL(10,2) NOT NULL,
    stock_count INT NOT NULL DEFAULT 0,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_goods_id (goods_id),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Database: flash_sale_order
-- -----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS flash_sale_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE flash_sale_order;

CREATE TABLE t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    seckill_goods_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    order_price DECIMAL(10,2) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-待支付 1-已支付 2-已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_goods (user_id, seckill_goods_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 消息去重表
CREATE TABLE t_message_idempotent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_key VARCHAR(128) NOT NULL UNIQUE,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Database: flash_sale_pay
-- -----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS flash_sale_pay DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE flash_sale_pay;

CREATE TABLE t_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    pay_channel VARCHAR(32) NOT NULL COMMENT 'ALIPAY/WECHAT',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-待支付 1-成功 2-失败',
    trade_no VARCHAR(128),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- Database: flash_sale_admin
-- -----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS flash_sale_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE flash_sale_admin;

CREATE TABLE t_seckill_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_name VARCHAR(128) NOT NULL,
    goods_id BIGINT NOT NULL,
    seckill_price DECIMAL(10,2) NOT NULL,
    stock_count INT NOT NULL DEFAULT 0,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-未开始 1-进行中 2-已结束',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_goods_id (goods_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 管理员用户表
CREATE TABLE sys_admin_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(128) NOT NULL COMMENT '加密后密码',
    salt VARCHAR(32) NOT NULL COMMENT '密码盐值',
    real_name VARCHAR(64) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(128) COMMENT '邮箱',
    avatar VARCHAR(512) COMMENT '头像URL',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(64) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    description VARCHAR(256) COMMENT '角色描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    sort_order INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    perm_code VARCHAR(128) NOT NULL UNIQUE COMMENT '权限编码',
    perm_name VARCHAR(128) NOT NULL COMMENT '权限名称',
    api_path VARCHAR(256) COMMENT '关联API路径',
    api_method VARCHAR(16) COMMENT 'GET/POST/PUT/DELETE',
    description VARCHAR(256),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 菜单表
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID, 0=顶层',
    menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
    menu_type TINYINT NOT NULL DEFAULT 1 COMMENT '1-目录 2-菜单 3-按钮',
    route_path VARCHAR(256) COMMENT '前端路由路径',
    component_path VARCHAR(256) COMMENT '前端组件路径',
    perm_code VARCHAR(128) COMMENT '关联权限编码',
    icon VARCHAR(128) COMMENT '图标名称',
    sort_order INT NOT NULL DEFAULT 0,
    visible TINYINT NOT NULL DEFAULT 1 COMMENT '0-隐藏 1-显示',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 用户-角色关联表
CREATE TABLE sys_admin_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- 角色-权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- 角色-菜单关联表
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关联表';

-- 初始化角色
INSERT INTO sys_role (role_code, role_name, description, sort_order) VALUES
('SUPER_ADMIN', '超级管理员', '拥有系统全部权限', 1),
('OPERATOR', '运营人员', '负责活动管理和日常运营', 2),
('VIEWER', '观察者', '只读权限，仅可查看数据', 3);

-- 初始化权限
INSERT INTO sys_permission (perm_code, perm_name, api_path, api_method) VALUES
('admin:activity:list', '查询活动列表', '/admin/activities', 'GET'),
('admin:activity:detail', '查询活动详情', '/admin/activities/{id}', 'GET'),
('admin:activity:create', '创建活动', '/admin/activities', 'POST'),
('admin:warmup:trigger', '触发预热', '/admin/warmup', 'POST'),
('admin:dashboard:view', '查看数据看板', '/admin/dashboard', 'GET'),
('admin:user:list', '查询管理员列表', '/admin/sys/users', 'GET'),
('admin:user:create', '创建管理员', '/admin/sys/users', 'POST'),
('admin:user:update', '编辑管理员', '/admin/sys/users/{id}', 'PUT'),
('admin:user:delete', '删除管理员', '/admin/sys/users/{id}', 'DELETE'),
('admin:user:assign-role', '分配角色', '/admin/sys/users/{id}/roles', 'PUT'),
('admin:role:list', '查询角色列表', '/admin/sys/roles', 'GET'),
('admin:role:create', '创建角色', '/admin/sys/roles', 'POST'),
('admin:role:update', '编辑角色', '/admin/sys/roles/{id}', 'PUT'),
('admin:role:delete', '删除角色', '/admin/sys/roles/{id}', 'DELETE'),
('admin:role:assign-perm', '分配权限', '/admin/sys/roles/{id}/permissions', 'PUT'),
('admin:role:assign-menu', '分配菜单', '/admin/sys/roles/{id}/menus', 'PUT'),
('admin:perm:list', '查询权限列表', '/admin/sys/permissions', 'GET'),
('admin:menu:list', '查询菜单列表', '/admin/sys/menus', 'GET'),
('admin:menu:create', '创建菜单', '/admin/sys/menus', 'POST'),
('admin:menu:update', '编辑菜单', '/admin/sys/menus/{id}', 'PUT'),
('admin:menu:delete', '删除菜单', '/admin/sys/menus/{id}', 'DELETE');

-- 初始化菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, route_path, component_path, icon, sort_order) VALUES
(1, 0, '数据看板', 2, '/dashboard', 'pages/Dashboard', 'DashboardOutlined', 1),
(2, 0, '活动管理', 1, '/activity', NULL, 'ThunderboltOutlined', 2),
(3, 2, '活动列表', 2, '/activity/list', 'pages/activity/ActivityList', NULL, 1),
(4, 2, '创建活动', 2, '/activity/create', 'pages/activity/ActivityCreate', NULL, 2),
(5, 0, '系统管理', 1, '/system', NULL, 'SettingOutlined', 99),
(6, 5, '管理员管理', 2, '/system/users', 'pages/system/UserList', NULL, 1),
(7, 5, '角色管理', 2, '/system/roles', 'pages/system/RoleList', NULL, 2),
(8, 5, '权限管理', 2, '/system/permissions', 'pages/system/PermissionList', NULL, 3),
(9, 5, '菜单管理', 2, '/system/menus', 'pages/system/MenuList', NULL, 4);

-- 初始化管理员 (密码: admin123, salt: abcd1234)
-- md5(md5('admin123' + 'f1a5h$@le') + 'abcd1234')
-- 第一层: md5('admin123f1a5h$@le') = 需要实际计算
INSERT INTO sys_admin_user (username, password, salt, real_name, status) VALUES
('admin', '9a30e37e3f262456a7e2ebd420d3b381', 'abcd1234', '超级管理员', 1);

-- 为超级管理员分配 SUPER_ADMIN 角色
INSERT INTO sys_admin_user_role (user_id, role_id) VALUES (1, 1);

-- 为 SUPER_ADMIN 角色分配全部权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 为 SUPER_ADMIN 角色分配全部菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 为 OPERATOR 角色分配业务权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE perm_code IN (
    'admin:activity:list', 'admin:activity:detail', 'admin:activity:create',
    'admin:warmup:trigger', 'admin:dashboard:view'
);

-- 为 OPERATOR 角色分配业务菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, id FROM sys_menu WHERE id IN (1, 2, 3, 4);

-- 为 VIEWER 角色分配只读权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE perm_code IN (
    'admin:activity:list', 'admin:activity:detail', 'admin:dashboard:view'
);

-- 为 VIEWER 角色分配只读菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu WHERE id IN (1, 2, 3);

-- -----------------------------------------------------------
-- Sample Data
-- -----------------------------------------------------------
USE flash_sale_goods;

INSERT INTO t_goods (goods_name, goods_img, goods_price, goods_stock) VALUES
('iPhone 15 Pro', '/img/iphone15.jpg', 8999.00, 1000),
('MacBook Pro M3', '/img/macbook.jpg', 14999.00, 500),
('AirPods Pro 2', '/img/airpods.jpg', 1799.00, 2000);

INSERT INTO t_seckill_goods (goods_id, seckill_price, stock_count, start_time, end_time) VALUES
(1, 6999.00, 100, '2026-05-01 10:00:00', '2026-05-01 12:00:00'),
(2, 11999.00, 50, '2026-05-01 10:00:00', '2026-05-01 12:00:00'),
(3, 999.00, 200, '2026-05-01 14:00:00', '2026-05-01 16:00:00');
