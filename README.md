# Order Manager

Spring Boot 电商后端系统，采用多模块架构，涵盖用户、商品、订单、优惠券、秒杀核心业务。

## 技术栈

- **框架**: Spring Boot 2.7.18 + MyBatis-Plus 3.5.5
- **数据库**: MySQL + Druid 连接池
- **缓存**: Redis（库存、购物车、秒杀扣减）
- **安全**: Spring Security BCrypt + JWT 无状态认证
- **工具**: Lombok、Fastjson2、OkHttp、Apache POI
- **分布式**: Guava RateLimiter 限流、Lua 脚本原子操作

## 模块结构

| 模块 | 功能说明 |
|------|----------|
| **User** | 用户注册/登录、JWT 认证拦截、个人信息管理、全局异常处理、AOP 日志 |
| **Product** | 商品 CRUD、分类管理、多条件筛选分页、Redis 购物车（Hash 结构） |
| **Order** | 下单（购物车结算 + 库存扣减 + 优惠券计算）、订单列表/详情、取消、支付 |
| **Coupon** | 优惠券模板管理、用户领券（Redis 库存 + 限领）、满减/折扣策略计算 |
| **Seckill** | 秒杀活动、Lua 原子扣库存 + 防重复、Guava RateLimiter 限流、秒杀订单生成 |

## API 概览

### User /user
- POST /user/login — 登录返回 JWT
- POST /user/register — 注册
- GET /user/me — 当前用户信息
- PUT /user/update — 更新个人信息

### Product /product
- GET /product/list — 分页列表（支持关键词、分类、价格区间筛选）
- GET /product/detail/{id} — 商品详情
- POST /product — 新增商品
- PUT /product/{id} — 更新商品
- PUT /product/{id}/status — 上下架

### Cart /cart
- GET /cart — 查看购物车
- POST /cart — 添加商品
- PUT /cart — 修改数量
- DELETE /cart/{productId} — 移除单品
- DELETE /cart — 清空

### Order /order
- POST /order — 创建订单（含优惠券抵扣）
- GET /order/list — 订单列表
- GET /order/detail/{orderId} — 订单详情
- PUT /order/{orderId}/cancel — 取消订单（恢复库存）
- PUT /order/{orderId}/pay — 模拟支付

### Coupon /coupon
- POST /coupon/template — 创建优惠券模板（满减/折扣）
- GET /coupon/template/list — 模板列表
- POST /coupon/receive/{templateId} — 领取优惠券
- GET /coupon/my — 我的优惠券

### Seckill /seckill
- POST /seckill/{activityId} — 参与秒杀（限流 + 原子扣库存）

## 启动方式

1. 配置 MySQL 和 Redis 连接信息（pplication.yml）
2. 创建对应数据库表
3. 启动 UserApplication.java（主模块会自动加载其他模块）

## 数据库表

| 表名 | 所属模块 | 说明 |
|------|----------|------|
| 	_user | User | 用户表 |
| 	_product | Product | 商品表 |
| 	_category | Product | 商品分类 |
| 	_order | Order | 订单主表 |
| 	_order_item | Order | 订单明细 |
| 	_coupon_template | Coupon | 优惠券模板 |
| 	_user_coupon | Coupon | 用户持有优惠券 |
| 	_seckill_activity | Seckill | 秒杀活动 |
