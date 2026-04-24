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
