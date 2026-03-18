# AgriBridge - 助农生产销售服务平台

AgriBridge 是一个专为农产品生产、展示、销售及求购撮合设计的综合性后端系统。该项目旨在通过数字化手段连接农户与消费者，提供从田间到餐桌的全链路服务，包括商品溯源、物流跟踪及实时在线沟通。

## 项目核心要点

- **全流程电商交易**：涵盖商品分类、SKU 管理、购物车、订单生成及支付结算（集成支付网关）。
- **C2B/B2B 撮合机制**：独特的“求购与报价”系统，支持买家发布大宗求购需求，卖家在线提交报价单。
- **质量溯源体系**：支持农产品溯源信息维护，为消费者提供透明的生产记录查询。
- **商家服务生态**：提供完善的商家入驻审核流程，以及独立的商家后台管理功能。
- **实时沟通系统**：基于 WebSocket 技术实现买卖双方的即时在线沟通与客服支持。
- **物流监控集成**：对接高德地图 API，实现物流路径规划与实时位置展示。
- **运营数据看板**：内置数据统计模块，为管理员提供销量趋势、用户增长等可视化数据支持。

## 技术栈

### 核心架构
- **后端框架**：Spring Boot 2.7.18
- **权限认证**：Sa-Token (支持 JWT、Redis 缓存)
- **数据库**：MySQL 8.0
- **持久层框架**：MyBatis-Plus
- **缓存中间件**：Redis
- **实时通讯**：WebSocket

### 辅助工具与集成
- **工具库**：Hutool (加密、工具类)、Lombok (简化代码)
- **文件存储**：阿里云 OSS
- **地图服务**：高德地图 (AMap) API
- **邮件服务**：Spring Mail (集成 QQ 邮箱 SMTP)
- **任务调度**：Spring Task (订单超时处理、求购状态更新)
- **JSON处理**：Jackson

## ?? 项目结构概览

```text
com.reyn
├── common          # 核心基础类（BaseController, BaseEntity）
├── config          # 系统配置（Sa-Token, OSS, WebSocket, MyBatis-Plus）
├── controller      # RESTful API 接口层
├── mapper          # 数据库映射接口层
├── objects         # 数据对象（Entity, DTO, VO）
├── service         # 业务逻辑层（接口与实现）
├── task            # 定时任务
├── utils           # 工具类（支付、验证码、登录辅助、IP处理）
└── Application.java # 项目启动入口
```

## 快速开始

### 1. 环境准备
- **JDK**: 17
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 5.0+

### 2. 数据库配置
1. 创建名为 `agri_bridge` 的数据库。
2. 执行项目中的 SQL 初始化脚本（若提供）。
3. 修改 `src/main/resources/application-local.yaml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       username: your_username
       password: your_password
   ```

### 3. 中间件配置
在 `application-local.yaml` 中配置 Redis、阿里云 OSS、高德地图及邮箱密钥：
- **Redis**: 配置 `host`, `port` 及 `password`。
- **OSS/Map/Mail**: 填入对应的 `access-key` 和 `secret`。

### 4. 运行项目
在项目根目录下执行以下 Maven 命令，或直接在 IDE 中运行 `Application.java`：
```bash
mvn clean install
mvn spring-boot:run
```
默认启动端口为：`8081`

## 安全说明
本项目已通过 `application-local.yaml` 分离敏感密钥，并配置了 `.gitignore`。在上传代码至公开仓库前，请务必确保不要提交包含真实密钥的本地配置文件。