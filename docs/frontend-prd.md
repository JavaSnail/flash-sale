# Flash-Sale 前端产品需求文档 (PRD)

> 版本：v1.0 | 日期：2026-04-29 | 作者：AI 辅助生成

---

## 一、项目概述

### 1.1 背景

Flash-Sale 是一个高并发秒杀系统，后端基于 Spring Boot + Spring Cloud 微服务架构，包含 7 个服务：Gateway、User、Goods、Seckill、Order、Pay、Admin。前端需要提供 **C 端用户秒杀前台** 和 **B 端管理后台** 两套界面。

### 1.2 目标用户

| 角色 | 描述 |
|------|------|
| C 端用户 | 参与秒杀活动的普通消费者，核心诉求：浏览商品 → 参与秒杀 → 下单支付 |
| B 端管理员 | 运营/管理人员，核心诉求：创建活动 → 监控数据 → 缓存预热 |

### 1.3 技术栈选型

| 技术 | 版本 | 用途 |
|------|------|------|
| React | 18.x | UI 框架 |
| TypeScript | 5.x | 类型安全 |
| Ant Design | 5.x | 组件库 |
| React Router | 6.x | 路由管理 |
| Axios | 1.x | HTTP 请求 |
| Zustand | 4.x | 轻量状态管理 |
| Vite | 5.x | 构建工具 |

### 1.4 网关地址

```
开发环境：http://localhost:8080
生产环境：https://api.flash-sale.com
```

所有前端请求统一通过 Gateway 转发，不直连各微服务。

---

## 二、C 端页面

### 2.1 登录页

**路由**：`/login`

**线框图**：

```
┌──────────────────────────────────────┐
│           Flash-Sale 秒杀           │
│                                      │
│  ┌──────────────────────────────┐   │
│  │  📱 手机号                    │   │
│  └──────────────────────────────┘   │
│  ┌──────────────────────────────┐   │
│  │  🔒 密码                      │   │
│  └──────────────────────────────┘   │
│                                      │
│  ┌──────────────────────────────┐   │
│  │          登  录               │   │
│  └──────────────────────────────┘   │
│                                      │
│       没有账号？去注册 →            │
└──────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `LoginPage` | 页面容器，居中卡片布局 |
| `LoginForm` | Ant Design `Form`，包含手机号 + 密码输入、登录按钮 |

**API 映射**：

| 操作 | 方法 | 路径 | 请求体 | 响应 |
|------|------|------|--------|------|
| 登录 | POST | `/user/login` | `{ phone: string, password: string }` | `Result<string>`（token） |

**交互流程**：

```
用户输入手机号/密码 → 点击"登录"
  ├─ 前端校验：手机号 11 位、密码非空
  ├─ 成功(code=0)：存储 token → 跳转 /（秒杀列表）
  ├─ 失败(code≠0)：展示 msg（如"手机号或密码错误"）
  └─ 网络异常：展示"网络异常，请重试"
```

**异常处理**：
- 手机号格式不正确 → 前端表单实时校验提示
- 密码为空 → 前端表单实时校验提示
- 账号不存在 / 密码错误 → 后端返回错误，展示 `msg`
- 连续登录失败 → 按钮 loading 防重复提交

---

### 2.2 注册页

**路由**：`/register`

**线框图**：

```
┌──────────────────────────────────────┐
│           创建账号                   │
│                                      │
│  ┌──────────────────────────────┐   │
│  │  📱 手机号                    │   │
│  └──────────────────────────────┘   │
│  ┌──────────────────────────────┐   │
│  │  🔒 密码                      │   │
│  └──────────────────────────────┘   │
│  ┌──────────────────────────────┐   │
│  │  🔒 确认密码                  │   │
│  └──────────────────────────────┘   │
│  ┌──────────────────────────────┐   │
│  │  👤 昵称（选填）              │   │
│  └──────────────────────────────┘   │
│                                      │
│  ┌──────────────────────────────┐   │
│  │          注  册               │   │
│  └──────────────────────────────┘   │
│                                      │
│       已有账号？去登录 →            │
└──────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `RegisterPage` | 页面容器 |
| `RegisterForm` | 包含手机号、密码、确认密码、昵称输入 |

**API 映射**：

| 操作 | 方法 | 路径 | 请求体 | 响应 |
|------|------|------|--------|------|
| 注册 | POST | `/user/register` | `{ phone: string, password: string, nickname?: string }` | `Result<void>` |

**交互流程**：

```
用户填写表单 → 点击"注册"
  ├─ 前端校验：手机号格式、密码 ≥ 6 位、两次密码一致
  ├─ 成功(code=0)：提示"注册成功" → 自动跳转 /login
  ├─ 失败(code=400)：展示 msg（如"手机号已注册"）
  └─ 网络异常：展示"网络异常，请重试"
```

---

### 2.3 秒杀列表页（首页）

**路由**：`/`

**线框图**：

```
┌──────────────────────────────────────────────┐
│  [Logo] Flash-Sale            [用户头像 ▼]   │
├──────────────────────────────────────────────┤
│                                              │
│  🔥 限时秒杀                                │
│                                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │  商品图   │  │  商品图   │  │  商品图   │  │
│  │          │  │          │  │          │  │
│  │ iPhone   │  │ MacBook  │  │ AirPods  │  │
│  │ ¥4999    │  │ ¥6999    │  │ ¥899     │  │
│  │ 原价¥7999│  │ 原价¥9999│  │ 原价¥1299│  │
│  │          │  │          │  │          │  │
│  │ 距开始   │  │ 抢购中   │  │ 已结束   │  │
│  │ 02:30:15 │  │ 剩余50件 │  │          │  │
│  │          │  │          │  │          │  │
│  │[即将开始]│  │[立即抢购]│  │[已结束]  │  │
│  └──────────┘  └──────────┘  └──────────┘  │
│                                              │
└──────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `HomePage` | 页面容器 |
| `AppHeader` | 顶部导航栏，Logo + 用户下拉菜单 |
| `GoodsList` | 商品卡片网格列表 |
| `GoodsCard` | 单个商品卡片：图片、名称、秒杀价、原价、状态标签、倒计时 |
| `CountdownTimer` | 距离活动开始/结束的倒计时组件 |

**API 映射**：

| 操作 | 方法 | 路径 | 请求体 | 响应 |
|------|------|------|--------|------|
| 获取秒杀商品列表 | GET | `/goods/seckill/list` | — | `Result<SeckillGoodsDTO[]>` |

**`SeckillGoodsDTO` 字段**：

```typescript
interface SeckillGoodsDTO {
  id: number;           // 秒杀商品ID
  goodsId: number;      // 原始商品ID
  goodsName: string;    // 商品名称
  goodsImg: string;     // 商品图片URL
  goodsPrice: number;   // 原价
  seckillPrice: number; // 秒杀价
  stockCount: number;   // 库存
  startTime: string;    // 开始时间 ISO 格式
  endTime: string;      // 结束时间 ISO 格式
}
```

**交互流程**：

```
进入页面 → 请求商品列表
  ├─ 成功 → 渲染商品卡片，根据时间判断状态：
  │    ├─ 未开始（now < startTime）：显示倒计时 + "即将开始"灰色按钮
  │    ├─ 进行中（startTime ≤ now ≤ endTime）：显示"立即抢购"红色按钮 + 剩余库存
  │    └─ 已结束（now > endTime）：显示"已结束"灰色按钮
  ├─ 点击卡片 / "立即抢购" → 跳转 /goods/:id
  ├─ 列表为空 → 展示空状态"暂无秒杀活动"
  └─ 请求失败 → 展示错误提示 + 重试按钮
```

**补充说明**：
- 卡片状态根据前端本地时间 + `startTime`/`endTime` 判断
- 倒计时精确到秒，每秒更新
- 进行中的活动排在最前，即将开始的其次，已结束的最后

---

### 2.4 商品详情页

**路由**：`/goods/:id`

**线框图**：

```
┌──────────────────────────────────────────────┐
│  ← 返回               商品详情              │
├──────────────────────────────────────────────┤
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │                                      │   │
│  │           商品大图                    │   │
│  │                                      │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  iPhone 15 Pro                               │
│                                              │
│  ¥4999          原价 ¥7999                  │
│  (秒杀价)        ─────                       │
│                                              │
│  ┌─────────────────────────────────────┐    │
│  │ 活动状态：抢购中                      │    │
│  │ 剩余库存：50 件                       │    │
│  │ 活动时间：2026-05-01 10:00 ~          │    │
│  │           2026-05-01 12:00            │    │
│  │ 倒计时：  01:23:45                    │    │
│  └─────────────────────────────────────┘    │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  ┌─────────┐   ┌─────┐              │   │
│  │  │ 5 + 3 = │   │  ?  │  ← 验证码    │   │
│  │  └─────────┘   └─────┘              │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │            立即抢购                   │   │
│  └──────────────────────────────────────┘   │
│                                              │
└──────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `GoodsDetailPage` | 页面容器，负责数据加载编排 |
| `GoodsImage` | 商品大图展示 |
| `GoodsPriceInfo` | 秒杀价 + 原价展示 |
| `SeckillStatusPanel` | 活动状态、库存、时间、倒计时 |
| `CaptchaInput` | 验证码图片 + 输入框 |
| `SeckillButton` | "立即抢购"按钮，含状态管理 |
| `SeckillResultModal` | 秒杀结果弹窗（排队中/成功/失败） |

**API 映射**：

| 操作 | 方法 | 路径 | 参数 | 响应 |
|------|------|------|------|------|
| 获取商品详情 | GET | `/goods/seckill/{id}` | path: id | `Result<SeckillGoodsDTO>` |
| 获取实时库存 | GET | `/goods/seckill/{id}/stock` | path: id | `Result<number>` |
| 获取验证码图片 | GET | `/seckill/captcha` | query: userId, seckillGoodsId | `image/jpeg` 二进制流 |
| 获取秒杀令牌 | POST | `/seckill/token` | query: userId, seckillGoodsId, captcha | `Result<string>` |
| 执行秒杀 | POST | `/seckill/execute` | body: `SeckillCommand` | `Result<void>` |
| 轮询秒杀结果 | GET | `/seckill/result` | query: userId, seckillGoodsId | `Result<SeckillResultDTO>` |

**`SeckillResultDTO` 字段**：

```typescript
interface SeckillResultDTO {
  orderId: number | null;  // 成功时返回订单ID
  status: number;          // 0=排队中, 1=成功, -1=失败
  message: string;         // 状态描述
}
```

**`SeckillCommand` 字段**：

```typescript
interface SeckillCommand {
  userId: number;
  seckillGoodsId: number;
  token: string;
}
```

**交互流程（秒杀核心流程）**：

```
1. 进入页面
   ├─ 并行请求：商品详情 + 实时库存
   ├─ 判断活动状态（同列表页逻辑）
   └─ 活动进行中 → 加载验证码图片

2. 秒杀操作流程（4 步串行）

   Step 1: 获取验证码
   GET /seckill/captcha?userId={uid}&seckillGoodsId={id}
   → 展示数学验证码图片（如 "5 + 3 = ?"）
   → 用户输入答案

   Step 2: 获取秒杀令牌
   POST /seckill/token?userId={uid}&seckillGoodsId={id}&captcha={answer}
     ├─ 成功 → 获得 token，进入 Step 3
     ├─ 验证码错误(code=5004) → 提示"验证码错误" + 刷新验证码
     └─ 令牌无效(code=5005) → 提示"令牌已失效，请重试"

   Step 3: 执行秒杀
   POST /seckill/execute { userId, seckillGoodsId, token }
     ├─ 成功 → 进入 Step 4 轮询
     ├─ 秒杀结束(code=5001) → 提示"秒杀已结束"
     ├─ 重复秒杀(code=5002) → 提示"您已参与过该活动"
     ├─ 访问限制(code=5006) → 提示"操作过于频繁，请稍后再试"
     └─ 其他失败 → 展示 msg

   Step 4: 轮询结果
   GET /seckill/result?userId={uid}&seckillGoodsId={id}
   → 弹出 SeckillResultModal，每 2 秒轮询一次
     ├─ status=0（排队中）→ 显示 loading + "排队中，请稍候..."
     ├─ status=1（成功）→ 显示"秒杀成功！" + "查看订单"按钮 → 跳转 /order/{orderId}
     ├─ status=-1（失败）→ 显示"秒杀失败" + message
     └─ 轮询上限 30 次（60 秒）→ 超时提示"请稍后在订单中查看"
```

**库存轮询**：
- 活动进行中时，每 5 秒轮询一次实时库存
- 库存为 0 时禁用抢购按钮，显示"已抢完"
- 页面离开时清理所有定时器

---

### 2.5 订单详情页

**路由**：`/order/:id`

**线框图**：

```
┌──────────────────────────────────────────────┐
│  ← 返回               订单详情              │
├──────────────────────────────────────────────┤
│                                              │
│  订单编号：1001                              │
│  下单时间：2026-05-01 10:00:30               │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │ 🖼️ iPhone 15 Pro                     │   │
│  │                                      │   │
│  │ 秒杀价：¥4999.00                     │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  订单状态：待支付                            │
│                                              │
│  ┌────────────────────┐                     │
│  │  ⏱️ 支付倒计时       │                     │
│  │   14:30            │                     │
│  └────────────────────┘                     │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │            立即支付                   │   │
│  └──────────────────────────────────────┘   │
│                                              │
└──────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `OrderDetailPage` | 页面容器 |
| `OrderInfo` | 订单号、下单时间 |
| `OrderGoodsCard` | 商品缩略信息 |
| `OrderStatusTag` | 订单状态标签（Ant Design Tag 组件） |
| `PayCountdown` | 待支付状态下的倒计时（15 分钟） |
| `PayButton` | 立即支付按钮 |

**API 映射**：

| 操作 | 方法 | 路径 | 参数 | 响应 |
|------|------|------|------|------|
| 获取订单详情 | GET | `/order/{id}` | path: id | `Result<OrderDTO>` |

**`OrderDTO` 字段**：

```typescript
interface OrderDTO {
  id: number;             // 订单ID
  userId: number;         // 用户ID
  seckillGoodsId: number; // 秒杀商品ID
  goodsId: number;        // 原始商品ID
  orderPrice: number;     // 订单金额（秒杀价）
  status: number;         // 0=待支付, 1=已支付, 2=已取消
  createTime: string;     // 下单时间
}
```

**交互流程**：

```
进入页面 → 请求订单详情
  ├─ 成功 → 渲染订单信息
  │    ├─ status=0（待支付）：
  │    │    ├─ 显示支付倒计时（15 分钟，基于 createTime 计算）
  │    │    ├─ 显示"立即支付"按钮 → 点击跳转 /pay/{orderId}
  │    │    └─ 倒计时结束 → 刷新页面，订单变为"已取消"
  │    ├─ status=1（已支付）：显示"已支付"标签，隐藏支付按钮
  │    └─ status=2（已取消）：显示"已取消"标签，提示"订单已超时取消"
  ├─ 订单不存在(code=6001) → 显示"订单不存在"空状态
  └─ 未登录(code=401) → 跳转 /login
```

---

### 2.6 支付页

**路由**：`/pay/:orderId`

**线框图**：

```
┌──────────────────────────────────────────────┐
│  ← 返回               确认支付              │
├──────────────────────────────────────────────┤
│                                              │
│  订单编号：1001                              │
│  支付金额：¥4999.00                          │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │ 支付方式                              │   │
│  │                                      │   │
│  │  ○ 支付宝                            │   │
│  │  ○ 微信支付                          │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │         确认支付 ¥4999.00             │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │         支付状态                      │   │
│  │  ✅ 支付成功 / ⏳ 支付处理中          │   │
│  │                                      │   │
│  │  [查看订单]                          │   │
│  └──────────────────────────────────────┘   │
│                                              │
└──────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `PayPage` | 页面容器 |
| `PayInfo` | 订单号 + 金额展示 |
| `PayChannelSelector` | 支付方式选择（Radio Group） |
| `PayButton` | 确认支付按钮 |
| `PayResultStatus` | 支付结果展示（Ant Design Result 组件） |

**API 映射**：

| 操作 | 方法 | 路径 | 参数 | 响应 |
|------|------|------|------|------|
| 创建支付 | POST | `/pay/create` | body: `PayRequestDTO` | `Result<number>`（支付ID） |
| 查询支付结果 | GET | `/pay/{orderId}` | path: orderId | `Result<PayResultDTO>` |

**`PayRequestDTO` 字段**：

```typescript
interface PayRequestDTO {
  orderId: number;
  userId: number;
  amount: number;
  payChannel: 'ALIPAY' | 'WECHAT';
}
```

**`PayResultDTO` 字段**：

```typescript
interface PayResultDTO {
  payId: number;
  orderId: number;
  status: number;    // 0=待支付, 1=成功, 2=失败
  tradeNo: string;   // 第三方流水号
}
```

**交互流程**：

```
进入页面 → 先请求订单详情确认订单有效
  ├─ 订单已支付 → 直接跳转 /order/{id}
  ├─ 订单已取消 → 提示"订单已取消"→ 返回首页

选择支付方式 → 点击"确认支付"
  ├─ POST /pay/create 创建支付
  │    ├─ 成功 → 开始轮询支付结果（每 3 秒）
  │    │    ├─ status=1（成功）→ 展示"支付成功" + "查看订单"按钮
  │    │    ├─ status=2（失败）→ 展示"支付失败，请重试"
  │    │    └─ status=0（处理中）→ 继续轮询，最多 60 秒
  │    └─ 失败(code=7001) → 展示"支付创建失败" + msg
  └─ 未选择支付方式 → 前端提示"请选择支付方式"
```

---

### 2.7 个人中心

**路由**：`/me`

**线框图**：

```
┌──────────────────────────────────────────────┐
│  ← 返回               个人中心              │
├──────────────────────────────────────────────┤
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  👤                                   │   │
│  │  用户A                                │   │
│  │  13800138000                          │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  退出登录                             │   │
│  └──────────────────────────────────────┘   │
│                                              │
└──────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `ProfilePage` | 页面容器 |
| `UserInfoCard` | 用户头像、昵称、手机号 |
| `LogoutButton` | 退出登录按钮 |

**API 映射**：

| 操作 | 方法 | 路径 | 参数 | 响应 |
|------|------|------|------|------|
| 获取当前用户 | GET | `/user/me` | — | `Result<UserDTO>` |

**`UserDTO` 字段**：

```typescript
interface UserDTO {
  id: number;
  nickname: string;
  phone: string;
}
```

**交互流程**：

```
进入页面 → GET /user/me
  ├─ 成功 → 展示用户信息
  ├─ 未登录(code=401) → 跳转 /login
  └─ 失败 → 展示错误提示

点击"退出登录"
  → 清除本地 token
  → 跳转 /login
```

---

## 三、B 端页面

> B 端采用 Ant Design Pro Layout 侧边栏 + 内容区布局。

### 3.1 数据看板

**路由**：`/admin/dashboard`

**线框图**：

```
┌──────────────────────────────────────────────────────────┐
│  Flash-Sale 管理后台                    [管理员] [退出]  │
├────────────┬─────────────────────────────────────────────┤
│            │                                             │
│  📊 数据看板│   ┌──────────┐ ┌──────────┐               │
│            │   │ 总订单数  │ │ 成功订单  │               │
│  📋 活动管理│   │  10,000  │ │  8,000   │               │
│            │   └──────────┘ └──────────┘               │
│  🔥 缓存预热│   ┌──────────┐ ┌──────────┐               │
│            │   │ 总用户数  │ │ 活跃活动  │               │
│            │   │  50,000  │ │    3     │               │
│            │   └──────────┘ └──────────┘               │
│            │                                             │
│            │                                             │
└────────────┴─────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `AdminLayout` | B 端全局布局：侧边栏 + 内容区 |
| `DashboardPage` | 看板页面容器 |
| `StatisticCard` | 单个统计卡片（Ant Design Statistic 组件） |

**API 映射**：

| 操作 | 方法 | 路径 | 参数 | 响应 |
|------|------|------|------|------|
| 获取看板数据 | GET | `/admin/dashboard` | — | `Result<DashboardDTO>` |

**`DashboardDTO` 字段**：

```typescript
interface DashboardDTO {
  totalOrders: number;      // 总订单数
  successOrders: number;    // 成功订单数
  totalUsers: number;       // 总用户数
  activeActivities: number; // 活跃活动数
}
```

**交互流程**：

```
进入页面 → GET /admin/dashboard
  ├─ 成功 → 渲染 4 个统计卡片
  ├─ 未授权(code=403) → 跳转 /login + 提示"无管理员权限"
  └─ 失败 → 展示错误提示
```

---

### 3.2 活动列表

**路由**：`/admin/activities`

**线框图**：

```
┌────────────┬─────────────────────────────────────────────┐
│            │                                             │
│  📊 数据看板│  秒杀活动管理          [+ 创建活动]          │
│            │                                             │
│  📋 活动管理│  ┌─────┬──────┬──────┬──────┬──────┬─────┐ │
│            │  │ ID  │ 名称  │ 秒杀价│ 库存  │ 状态  │ 操作│ │
│  🔥 缓存预热│  ├─────┼──────┼──────┼──────┼──────┼─────┤ │
│            │  │ 1   │618大促│¥4999 │ 100  │进行中 │编辑 │ │
│            │  │ 2   │双11..│¥6999 │ 50   │未开始 │编辑 │ │
│            │  │ 3   │年末..│¥899  │ 200  │已结束 │编辑 │ │
│            │  └─────┴──────┴──────┴──────┴──────┴─────┘ │
│            │                                             │
└────────────┴─────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `ActivityListPage` | 页面容器 |
| `ActivityTable` | Ant Design Table 活动列表 |
| `StatusTag` | 状态标签（未开始/进行中/已结束，不同颜色） |

**API 映射**：

| 操作 | 方法 | 路径 | 参数 | 响应 |
|------|------|------|------|------|
| 获取活动列表 | GET | `/admin/activities` | — | `Result<SeckillActivityDTO[]>` |

**`SeckillActivityDTO` 字段**：

```typescript
interface SeckillActivityDTO {
  id: number;
  activityName: string;   // 活动名称
  goodsId: number;        // 关联商品ID
  seckillPrice: number;   // 秒杀价格
  stockCount: number;     // 库存数量
  startTime: string;      // 开始时间
  endTime: string;        // 结束时间
  status: number;         // 0=未开始, 1=进行中, 2=已结束
}
```

**表格列定义**：

| 列 | 字段 | 渲染 |
|----|------|------|
| ID | id | 文本 |
| 活动名称 | activityName | 文本 |
| 秒杀价格 | seckillPrice | 金额格式 ¥x.xx |
| 库存 | stockCount | 数字 |
| 开始时间 | startTime | 日期格式化 |
| 结束时间 | endTime | 日期格式化 |
| 状态 | status | Tag 组件：0→蓝色"未开始" / 1→绿色"进行中" / 2→灰色"已结束" |
| 操作 | — | "编辑"链接 → 跳转 /admin/activities/edit/:id |

**交互流程**：

```
进入页面 → GET /admin/activities
  ├─ 成功 → 渲染表格
  ├─ 列表为空 → 展示空状态"暂无活动，请创建"
  └─ 失败 → 展示错误提示

点击"+ 创建活动" → 跳转 /admin/activities/create
点击"编辑" → 跳转 /admin/activities/edit/{id}
```

---

### 3.3 活动创建 / 编辑

**路由**：
- 创建：`/admin/activities/create`
- 编辑：`/admin/activities/edit/:id`

**线框图**：

```
┌────────────┬─────────────────────────────────────────────┐
│            │                                             │
│  📊 数据看板│  创建秒杀活动 / 编辑秒杀活动                 │
│            │                                             │
│  📋 活动管理│  ┌──────────────────────────────────────┐  │
│            │  │                                      │  │
│  🔥 缓存预热│  │  活动名称  ┌────────────────────┐    │  │
│            │  │           │ 618大促秒杀          │    │  │
│            │  │           └────────────────────┘    │  │
│            │  │                                      │  │
│            │  │  关联商品ID ┌────────────────────┐   │  │
│            │  │           │ 100                 │    │  │
│            │  │           └────────────────────┘    │  │
│            │  │                                      │  │
│            │  │  秒杀价格  ┌────────────────────┐    │  │
│            │  │           │ 4999.00             │    │  │
│            │  │           └────────────────────┘    │  │
│            │  │                                      │  │
│            │  │  库存数量  ┌────────────────────┐    │  │
│            │  │           │ 100                 │    │  │
│            │  │           └────────────────────┘    │  │
│            │  │                                      │  │
│            │  │  活动时间  ┌───────────┬──────────┐  │  │
│            │  │           │ 开始时间   │ 结束时间  │  │  │
│            │  │           └───────────┴──────────┘  │  │
│            │  │                                      │  │
│            │  │  ┌────────┐  ┌────────┐             │  │
│            │  │  │  提交   │  │  取消   │             │  │
│            │  │  └────────┘  └────────┘             │  │
│            │  │                                      │  │
│            │  └──────────────────────────────────────┘  │
│            │                                             │
└────────────┴─────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `ActivityFormPage` | 页面容器，区分创建/编辑模式 |
| `ActivityForm` | Ant Design Form 表单 |

**表单字段**：

| 字段 | 组件 | 校验规则 |
|------|------|----------|
| 活动名称 | Input | 必填，最大 50 字符 |
| 关联商品ID | InputNumber | 必填，正整数 |
| 秒杀价格 | InputNumber | 必填，> 0，精度 2 位小数 |
| 库存数量 | InputNumber | 必填，正整数 |
| 活动时间 | RangePicker (DatePicker) | 必填，结束时间 > 开始时间 |

**API 映射**：

| 操作 | 方法 | 路径 | 参数 | 响应 |
|------|------|------|------|------|
| 获取活动详情（编辑时） | GET | `/admin/activities/{id}` | path: id | `Result<SeckillActivityDTO>` |
| 创建活动 | POST | `/admin/activities` | body: `SeckillActivityDTO` | `Result<void>` |

**交互流程**：

```
创建模式：
  → 展示空表单
  → 填写表单 → 点击"提交"
    ├─ 前端校验通过 → POST /admin/activities
    │    ├─ 成功 → 提示"创建成功" → 跳转 /admin/activities
    │    └─ 失败 → 展示错误 msg
    └─ 前端校验不通过 → 表单字段标红提示

编辑模式：
  → GET /admin/activities/{id} 加载数据 → 回填表单
  → 修改表单 → 点击"提交"
    ├─ POST /admin/activities（带 id 字段）
    │    ├─ 成功 → 提示"更新成功" → 跳转 /admin/activities
    │    └─ 失败 → 展示错误 msg
    └─ 前端校验不通过 → 表单字段标红提示

点击"取消" → 返回 /admin/activities
```

---

### 3.4 缓存预热

**路由**：`/admin/warmup`

**线框图**：

```
┌────────────┬─────────────────────────────────────────────┐
│            │                                             │
│  📊 数据看板│  缓存预热                                   │
│            │                                             │
│  📋 活动管理│  ┌──────────────────────────────────────┐  │
│            │  │                                      │  │
│  🔥 缓存预热│  │  将秒杀商品库存数据预热到 Redis 缓存中，│  │
│            │  │  提升秒杀开始时的并发处理能力。          │  │
│            │  │                                      │  │
│            │  │  ┌──────────────────────────────┐    │  │
│            │  │  │        执行预热               │    │  │
│            │  │  └──────────────────────────────┘    │  │
│            │  │                                      │  │
│            │  │  上次预热时间：2026-05-01 09:30:00    │  │
│            │  │  状态：✅ 预热成功                    │  │
│            │  │                                      │  │
│            │  └──────────────────────────────────────┘  │
│            │                                             │
└────────────┴─────────────────────────────────────────────┘
```

**组件拆解**：

| 组件 | 说明 |
|------|------|
| `WarmupPage` | 页面容器 |
| `WarmupDescription` | 功能说明文字 |
| `WarmupButton` | 执行预热按钮 |
| `WarmupStatus` | 预热结果状态展示 |

**API 映射**：

| 操作 | 方法 | 路径 | 参数 | 响应 |
|------|------|------|------|------|
| 执行缓存预热 | POST | `/admin/warmup` | — | `Result<void>` |

**交互流程**：

```
点击"执行预热" → 按钮 loading
  ├─ POST /admin/warmup
  │    ├─ 成功(code=0) → 展示"预热成功" + 记录时间（前端本地）
  │    └─ 失败 → 展示"预热失败" + msg
  └─ 二次确认 Popconfirm："确定执行缓存预热？"
```

---

## 四、全局设计

### 4.1 路由表 & 权限控制

```typescript
// C 端路由
const publicRoutes = [
  { path: '/login',       element: <LoginPage /> },
  { path: '/register',    element: <RegisterPage /> },
];

const protectedRoutes = [
  { path: '/',            element: <HomePage /> },
  { path: '/goods/:id',   element: <GoodsDetailPage /> },
  { path: '/order/:id',   element: <OrderDetailPage /> },
  { path: '/pay/:orderId', element: <PayPage /> },
  { path: '/me',          element: <ProfilePage /> },
];

// B 端路由（嵌套在 AdminLayout 内）
const adminRoutes = [
  { path: '/admin/dashboard',          element: <DashboardPage /> },
  { path: '/admin/activities',         element: <ActivityListPage /> },
  { path: '/admin/activities/create',  element: <ActivityFormPage /> },
  { path: '/admin/activities/edit/:id', element: <ActivityFormPage /> },
  { path: '/admin/warmup',            element: <WarmupPage /> },
];
```

**权限控制规则**：

| 路由 | 权限要求 |
|------|----------|
| `/login`, `/register` | 无需登录，已登录则重定向到 `/` |
| C 端 protectedRoutes | 需要登录（有 token），未登录重定向到 `/login` |
| B 端 `/admin/*` | 需要管理员权限，普通用户返回 403 |

**实现方式**：
- `AuthGuard` 高阶组件：检查 token 是否存在
- `AdminGuard` 高阶组件：检查用户角色是否为管理员

### 4.2 HTTP 请求封装

```typescript
// src/utils/request.ts
import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';

// 统一响应类型
interface Result<T> {
  code: number;
  msg: string;
  data: T;
}

// 错误码枚举
enum ErrorCode {
  SUCCESS        = 0,
  PARAM_ERROR    = 400,
  UNAUTHORIZED   = 401,
  FORBIDDEN      = 403,
  NOT_FOUND      = 404,
  SERVER_ERROR   = 500,
  SECKILL_OVER   = 5001,
  REPEAT_SECKILL = 5002,
  SECKILL_FAIL   = 5003,
  CAPTCHA_ERROR  = 5004,
  TOKEN_INVALID  = 5005,
  ACCESS_LIMIT   = 5006,
  ORDER_NOT_EXIST = 6001,
  ORDER_TIMEOUT  = 6002,
  PAY_FAIL       = 7001,
}

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
});

// 请求拦截器：注入 token
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = token;
  }
  return config;
});

// 响应拦截器：统一处理 Result<T>
request.interceptors.response.use(
  (response: AxiosResponse<Result<any>>) => {
    const result = response.data;
    if (result.code === ErrorCode.SUCCESS) {
      return result.data;
    }
    // 401 未登录 → 跳转登录页
    if (result.code === ErrorCode.UNAUTHORIZED) {
      localStorage.removeItem('token');
      window.location.href = '/login';
      return Promise.reject(new Error(result.msg));
    }
    // 其他业务错误 → 抛出带 code 的错误
    const error = new Error(result.msg) as Error & { code: number };
    error.code = result.code;
    return Promise.reject(error);
  },
  (error) => {
    // 网络错误
    return Promise.reject(new Error('网络异常，请检查网络连接'));
  }
);

export default request;
export { ErrorCode };
export type { Result };
```

### 4.3 鉴权方案

```
┌──────────┐     POST /user/login      ┌──────────┐
│  前端     │ ────────────────────────→ │  Gateway  │
│          │ ←──────────────────────── │          │
│          │     Result<token>         │          │
│          │                           │          │
│  存储:    │     GET /goods/...        │  校验:    │
│  local-  │ ────────────────────────→ │  Auth-   │
│  Storage │  Authorization: {token}   │  Global- │
│          │                           │  Filter  │
└──────────┘                           └──────────┘
```

**流程说明**：
1. 用户登录成功后，后端返回 token 字符串
2. 前端将 token 存储在 `localStorage`
3. 每次请求通过 axios 拦截器在 `Authorization` header 中携带 token
4. Gateway 的 `AuthGlobalFilter` 校验 token 有效性
5. token 无效或过期 → 返回 `code=401` → 前端清除 token → 跳转登录页

**白名单路径**（无需 token）：
- `POST /user/login`
- `POST /user/register`

### 4.4 推荐目录结构

```
flash-sale-web/
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                    # API 请求函数
│   │   ├── user.ts             # 用户相关 API
│   │   ├── goods.ts            # 商品相关 API
│   │   ├── seckill.ts          # 秒杀相关 API
│   │   ├── order.ts            # 订单相关 API
│   │   ├── pay.ts              # 支付相关 API
│   │   └── admin.ts            # 管理后台 API
│   ├── components/             # 通用组件
│   │   ├── AuthGuard.tsx       # 登录态路由守卫
│   │   ├── AdminGuard.tsx      # 管理员路由守卫
│   │   ├── CountdownTimer.tsx  # 倒计时组件
│   │   └── AppHeader.tsx       # C 端顶部导航
│   ├── layouts/
│   │   ├── CLayout.tsx         # C 端布局
│   │   └── AdminLayout.tsx     # B 端侧边栏布局
│   ├── pages/
│   │   ├── login/
│   │   │   └── LoginPage.tsx
│   │   ├── register/
│   │   │   └── RegisterPage.tsx
│   │   ├── home/
│   │   │   ├── HomePage.tsx
│   │   │   └── GoodsCard.tsx
│   │   ├── goods/
│   │   │   ├── GoodsDetailPage.tsx
│   │   │   ├── CaptchaInput.tsx
│   │   │   ├── SeckillButton.tsx
│   │   │   └── SeckillResultModal.tsx
│   │   ├── order/
│   │   │   └── OrderDetailPage.tsx
│   │   ├── pay/
│   │   │   └── PayPage.tsx
│   │   ├── profile/
│   │   │   └── ProfilePage.tsx
│   │   └── admin/
│   │       ├── dashboard/
│   │       │   └── DashboardPage.tsx
│   │       ├── activities/
│   │       │   ├── ActivityListPage.tsx
│   │       │   └── ActivityFormPage.tsx
│   │       └── warmup/
│   │           └── WarmupPage.tsx
│   ├── store/                  # Zustand 状态管理
│   │   └── useAuthStore.ts     # 用户登录态
│   ├── types/                  # TypeScript 类型定义
│   │   └── index.ts            # 所有 DTO 接口定义
│   ├── utils/
│   │   └── request.ts          # Axios 封装
│   ├── App.tsx                 # 根组件 + 路由配置
│   ├── main.tsx                # 入口
│   └── vite-env.d.ts
├── .env.development            # VITE_API_BASE_URL=http://localhost:8080
├── .env.production             # VITE_API_BASE_URL=https://api.flash-sale.com
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

---

## 附录：API 汇总表

| 序号 | 服务 | 方法 | 路径 | 说明 | 使用页面 |
|------|------|------|------|------|----------|
| 1 | User | POST | `/user/login` | 用户登录 | 登录页 |
| 2 | User | POST | `/user/register` | 用户注册 | 注册页 |
| 3 | User | GET | `/user/me` | 获取当前用户 | 个人中心 |
| 4 | User | GET | `/user/{id}` | 根据 ID 获取用户 | （内部调用） |
| 5 | Goods | GET | `/goods/seckill/list` | 秒杀商品列表 | 秒杀列表页 |
| 6 | Goods | GET | `/goods/seckill/{id}` | 秒杀商品详情 | 商品详情页 |
| 7 | Goods | GET | `/goods/seckill/{id}/stock` | 实时库存查询 | 商品详情页 |
| 8 | Goods | POST | `/goods/seckill/warmup` | 库存预热 | （Admin 内部） |
| 9 | Seckill | GET | `/seckill/captcha` | 获取验证码图片 | 商品详情页 |
| 10 | Seckill | POST | `/seckill/token` | 获取秒杀令牌 | 商品详情页 |
| 11 | Seckill | POST | `/seckill/execute` | 执行秒杀 | 商品详情页 |
| 12 | Seckill | GET | `/seckill/result` | 查询秒杀结果 | 商品详情页 |
| 13 | Order | GET | `/order/{id}` | 订单详情 | 订单详情页 |
| 14 | Pay | POST | `/pay/create` | 创建支付 | 支付页 |
| 15 | Pay | GET | `/pay/{orderId}` | 查询支付结果 | 支付页 |
| 16 | Pay | POST | `/pay/callback` | 支付回调 | （后端内部） |
| 17 | Admin | GET | `/admin/dashboard` | 看板数据 | 数据看板 |
| 18 | Admin | GET | `/admin/activities` | 活动列表 | 活动列表 |
| 19 | Admin | GET | `/admin/activities/{id}` | 活动详情 | 活动编辑 |
| 20 | Admin | POST | `/admin/activities` | 创建/更新活动 | 活动创建/编辑 |
| 21 | Admin | POST | `/admin/warmup` | 缓存预热 | 缓存预热 |
