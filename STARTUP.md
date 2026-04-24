# Flash Sale System - 启动运行文档

## 项目概述

高并发分布式秒杀系统，基于 Spring Boot 3.2 + Spring Cloud Alibaba 微服务架构，采用 DDD（领域驱动设计）分层。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 框架 | Spring Boot | 3.2.4 |
| 微服务 | Spring Cloud + Spring Cloud Alibaba | 2023.0.1 |
| 注册中心 | Nacos | 2.3.1 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| 消息队列 | RocketMQ | 5.1.4 |
| ORM | MyBatis-Plus | 3.5.5 |
| 认证 | Sa-Token | 1.37.0 |
| 限流熔断 | Sentinel | 1.8.7 |
| 构建工具 | Maven | 3.8+ |

## 项目结构

```
flash-sale/
├── flash-sale-common          # 公共模块（Result、ErrorCode、常量、注解）
├── flash-sale-gateway         # API 网关（认证过滤、IP 黑名单、限流）
├── flash-sale-user            # 用户服务（注册、登录、会话管理）
│   ├── flash-sale-user-api    #   Feign 接口 + DTO
│   └── flash-sale-user-service#   业务实现
├── flash-sale-goods           # 商品服务（商品管理、库存预热）
│   ├── flash-sale-goods-api
│   └── flash-sale-goods-service
├── flash-sale-seckill         # 秒杀服务（验证码、令牌、库存预扣、异步下单）
│   ├── flash-sale-seckill-api
│   └── flash-sale-seckill-service
├── flash-sale-order           # 订单服务（MQ 消费创建订单、超时取消）
│   ├── flash-sale-order-api
│   └── flash-sale-order-service
├── flash-sale-pay             # 支付服务（支付创建、回调通知）
│   ├── flash-sale-pay-api
│   └── flash-sale-pay-service
├── flash-sale-admin           # 管理后台（活动管理、Dashboard）
│   ├── flash-sale-admin-api
│   └── flash-sale-admin-service
├── sql/                       # 数据库初始化脚本
│   └── schema.sql
└── docker-compose.yml         # 中间件一键部署
```

## 环境要求

- **JDK 21**
- **Maven 3.8+**
- **Docker & Docker Compose**（用于启动中间件）

## 快速启动

### 第一步：启动中间件

使用 Docker Compose 一键拉起所有依赖的中间件：

```bash
docker-compose up -d
```

等待所有容器健康运行（约 30~60 秒）：

```bash
docker-compose ps
```

各中间件端口：

| 中间件 | 容器名 | 端口 | 说明 |
|--------|--------|------|------|
| MySQL | flash-sale-mysql | 3306 | 账号 root/root，自动初始化数据库 |
| Redis | flash-sale-redis | 6379 | 无密码 |
| Nacos | flash-sale-nacos | 8848 | 控制台 http://localhost:8848/nacos（nacos/nacos） |
| RocketMQ NameServer | flash-sale-namesrv | 9876 | |
| RocketMQ Broker | flash-sale-broker | 10911 | |

> Sentinel Dashboard 为可选组件，启动方式：`docker-compose --profile full up -d`，端口 8858。

### 第二步：编译项目

```bash
mvn clean install -DskipTests
```

### 第三步：启动微服务

按以下顺序启动（在 IntelliJ IDEA 中直接运行各 `*Application.java` 主类）：

| 顺序 | 服务 | 端口 | 启动类 |
|------|------|------|--------|
| 1 | 用户服务 | 8081 | `UserApplication` |
| 2 | 商品服务 | 8082 | `GoodsApplication` |
| 3 | 管理后台 | 8086 | `AdminApplication` |
| 4 | 秒杀服务 | 8083 | `SeckillApplication` |
| 5 | 订单服务 | 8084 | `OrderApplication` |
| 6 | 支付服务 | 8085 | `PayApplication` |
| 7 | API 网关 | 8080 | `GatewayApplication` |

命令行启动方式（以用户服务为例）：

```bash
java -jar flash-sale-user/flash-sale-user-service/target/flash-sale-user-service-1.0.0-SNAPSHOT.jar
```

### 第四步：验证启动

1. 确认所有服务已注册到 Nacos：http://localhost:8848/nacos → 服务管理 → 服务列表

2. 通过网关调用接口：

```bash
# 用户注册
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"123456","nickname":"testuser"}'

# 用户登录（返回 Token）
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"123456"}'

# 查询秒杀商品列表（需携带 Token）
curl http://localhost:8080/goods/seckill/list \
  -H "Authorization: <上一步返回的Token>"
```

## 数据库说明

MySQL 首次启动时自动执行 `sql/schema.sql`，创建以下数据库和表：

| 数据库 | 表 | 说明 |
|--------|----|------|
| flash_sale_user | t_user | 用户表 |
| flash_sale_goods | t_goods | 商品表 |
| flash_sale_goods | t_seckill_goods | 秒杀商品表 |
| flash_sale_order | t_order | 订单表 |
| flash_sale_order | t_message_idempotent | 消息去重表 |
| flash_sale_pay | t_payment | 支付记录表 |
| flash_sale_admin | t_seckill_activity | 秒杀活动表 |

初始化脚本还包含 3 条示例商品数据和对应的秒杀商品配置。

## 环境变量配置

所有中间件地址均支持通过环境变量覆盖，方便部署到不同环境：

| 环境变量 | 默认值 | 说明 |
|----------|--------|------|
| `MYSQL_HOST` | 127.0.0.1 | MySQL 地址 |
| `MYSQL_USER` | root | MySQL 用户名 |
| `MYSQL_PASS` | root | MySQL 密码 |
| `REDIS_HOST` | 127.0.0.1 | Redis 地址 |
| `REDIS_PORT` | 6379 | Redis 端口 |
| `NACOS_ADDR` | 127.0.0.1:8848 | Nacos 地址 |
| `ROCKETMQ_NAMESRV` | 127.0.0.1:9876 | RocketMQ NameServer 地址 |
| `SENTINEL_DASHBOARD` | 127.0.0.1:8080 | Sentinel 控制台地址 |

## 秒杀核心流程

```
用户请求 → 网关(认证+限流) → 秒杀服务
                                ├── 验证码校验
                                ├── 令牌验证
                                ├── Redis 库存预扣（原子操作）
                                └── 发送 MQ 消息 ──→ 订单服务
                                                      ├── 幂等检查
                                                      ├── 创建订单
                                                      └── 写入 Redis 结果
用户轮询结果 ← 网关 ← 秒杀结果服务 ← Redis
```

## 常用运维命令

```bash
# 启动中间件
docker-compose up -d

# 停止中间件
docker-compose down

# 停止并清除数据卷（重新初始化数据库）
docker-compose down -v

# 查看中间件日志
docker-compose logs -f mysql
docker-compose logs -f nacos

# 查看 Nacos 服务列表
curl http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=10
```

## 常见问题

### 1. MySQL 容器启动后数据库未初始化

`schema.sql` 仅在 MySQL 数据卷**首次创建**时执行。若需重新初始化：

```bash
docker-compose down -v
docker-compose up -d
```

### 2. RocketMQ Broker 启动失败

检查 Broker 日志，确认 NameServer 已就绪：

```bash
docker-compose logs rocketmq-broker
```

如果报连接失败，等待 NameServer 完全启动后重启 Broker：

```bash
docker-compose restart rocketmq-broker
```

### 3. 服务注册不到 Nacos

- 确认 Nacos 已启动：http://localhost:8848/nacos
- 检查应用配置中 `NACOS_ADDR` 是否正确
- 确认本机防火墙未拦截 8848 和 9848 端口

### 4. 端口冲突

如本地端口被占用，可修改 `docker-compose.yml` 中的端口映射，并通过环境变量覆盖应用配置。
