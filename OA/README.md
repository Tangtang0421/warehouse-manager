# WMS 智能仓库管理系统

> 基于 Spring Boot + Redis + Redisson 构建的高并发、企业级仓库管理系统。

## 📖 项目简介
本项目是一个完整的前后端分离仓库管理系统。涵盖了用户 RBAC 权限体系、物料/仓库/分类字典管理、高并发出入库流水等核心业务。系统通过引入多级缓存与分布式锁架构，重点解决了传统 WMS 系统在海量数据查询与瞬时高并发出库场景下的性能瓶颈与数据一致性问题。

## 🛠️ 核心技术栈
* **后端框架：** Spring Boot 2.x / 3.x
* **持久层：** MyBatis-Plus + MySQL 8.0
* **缓存与分布式：** Redis + Spring Cache + Redisson
* **安全鉴权：** JWT + ThreadLocal
* **接口文档：** Apifox / Swagger
* **项目管理：** Maven + Git

## 💡 核心架构亮点

### 1. 高并发防超卖架构 (Redisson + MySQL 双重防御)
针对极高并发下的出库场景，废弃了低效的全局锁。
* **顶层削峰：** 引入 `Redisson` 分布式锁，精确锁定单件 `GoodsId`（细粒度锁），并配合 `tryLock` 实现快速失败（Fail-Fast）与 WatchDog 自动续期，有效防止并发洪峰打垮数据库。
* **底层兜底：** 利用 MySQL InnoDB 行级锁与乐观锁思想 (`ge` 校验)，确保即使在极端情况下库存也绝对不会出现负数。

### 2. 复杂流水查询调优
针对出入库记录（Record）的分页关联查询进行了深度优化。
* 摒弃了传统的数据库联表（JOIN）查询，采用**单表极速分页 + 内存应用层聚合**。
* 批量提取关联的物品 ID 和用户 ID，利用 Java `Map` 在内存中进行 O(1) 复杂度的关联组装，将原本可能引发的 N+1 次 SQL 查询强行压缩至 3 次，查询吞吐量提升显著。

### 3. 动静分离的缓存架构 (Spring Cache + SpEL)
* **字典数据：** 针对仓库列表、物料分类等“读多写少”的全量数据，通过 `@Cacheable` 结合自定义 `RedisCacheManager` (JSON序列化 + 24h TTL) 实现旁路缓存。
* **权限数据：** 针对系统菜单，利用 SpEL 表达式 (`key="#roleId"`) 实现基于 RBAC 角色的动态缓存隔离，杜绝越权访问漏洞。
* **缓存一致性：** 在增删改接口严格配合 `@CacheEvict` 进行缓存及时爆破。

### 4. 无状态安全鉴权
* 采用 **Redis + JWT** 结合的无状态认证模式。
* 拦截器拦截请求后，通过 Redis 校验 Token 状态并实现无感知续期，随后将用户信息存入 `ThreadLocal`，实现请求链路上的安全传递与层层解耦，并在拦截器结束时强制清理防止内存泄漏。

## 快速启动

1. 克隆项目：`git clone https://github.com/Tangtang0421/warehouse-management.git`
2. 初始化数据库：执行 `sql/` 目录下的初始化脚本。
3. 修改配置：在 `application.yml` 中修改 MySQL 和 Redis 的连接信息。
4. 启动 Redis 服务 (默认 `127.0.0.1:6379`)。
5. 运行 `WmsApplication.java` 启动项目。

## 接口规范
本项目严格遵循 RESTful API 设计规范，统一使用 `@Validated` 进行入参校验，并封装了统一的 `Result<T>` 泛型响应体与全局异常拦截器（GlobalExceptionHandler）。