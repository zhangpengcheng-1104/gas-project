# 智能燃气平台管理系统 - 项目概览

> 此文档供 Trae AI 助手了解项目结构，便于手机端与电脑端协作开发

---

## 一、项目基本信息

- **项目名称**: 智能燃气平台管理系统
- **项目类型**: 前后端分离的燃气行业管理系统
- **项目路径**: `e:\.行业实训`
- **创建日期**: 2026-05-07

---

## 二、项目架构

```
e:\.行业实训/
├── gas-api/              # 燃气管理系统主后端 API (Spring Boot)
├── gas-admin-api/        # 管理员后端 API
├── gas-vue/              # 管理后台前端 (Vue 3 + Element Plus)
├── patient-wx/           # 患者端微信小程序 (UniApp)
├── patient-wx-api/       # 患者端后端 API
├── .trae/documents/      # 项目开发文档
├── gas_database_init.sql # 数据库初始化脚本
└── PROJECT_OVERVIEW.md   # 本文档
```

---

## 三、技术栈详情

### 3.1 后端技术栈 (gas-api)

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.4 | 核心框架 |
| JDK | 15 | Java 版本 |
| MySQL | 8.0.32 | 关系型数据库 |
| Redis | - | 缓存数据库 |
| MyBatis | 2.2.2 | ORM 框架 |
| Sa-Token | 1.28.0 | 权限认证框架 |
| Druid | 1.2.20 | 数据库连接池 |
| Hutool | 5.8.5 | Java 工具库 |
| Quartz | - | 定时任务 |
| MinIO | 8.2.1 | 对象存储 |
| 腾讯云 SDK | 3.1.416 | 云服务（TRTC视频通话等） |
| 微信支付 SDK | 1.0.13 | 微信支付集成 |
| Apache POI | 5.2.3 | Excel/PDF 处理 |

### 3.2 前端技术栈 (gas-vue)

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.2.39 | 前端框架 |
| Element Plus | 2.2.16 | UI 组件库 |
| Vite | 3.1.0 | 构建工具 |
| Vue Router | 4.1.5 | 路由管理 |
| Axios | 1.6.0 | HTTP 请求 |
| ECharts | 5.4.0 | 图表库 |
| Day.js | 1.11.5 | 日期处理 |
| Less | 4.1.3 | CSS 预处理器 |

### 3.3 微信小程序技术栈 (patient-wx)

| 技术 | 版本 | 说明 |
|------|------|------|
| UniApp | - | 跨平台小程序框架 |
| uView UI | - | UI 组件库 |
| TRTC SDK | 4.14.4 | 腾讯实时音视频 |
| Day.js | - | 日期处理 |

---

## 四、核心功能模块

### 4.1 管理后台功能 (gas-vue)

| 模块文件 | 功能说明 |
|----------|----------|
| `gas_user.vue` | 燃气用户管理 - 用户信息增删改查 |
| `gas_worker_plan.vue` | 燃气工人工作计划管理 |
| `hazard_info.vue` | 隐患信息管理 - 安全隐患记录与整改 |
| `worker.vue` | 工人信息管理 |
| `doctor.vue` | 医生信息管理 |
| `doctor_schedule.vue` | 医生排班管理 |
| `medical_dept.vue` | 医疗科室管理 |
| `medical_dept_sub.vue` | 医疗子科室管理 |
| `role.vue` | 角色权限管理 |
| `user.vue` | 系统用户管理 |
| `video_diagnose.vue` | 视频诊疗管理 |
| `dept.vue` | 部门管理 |
| `home.vue` | 首页仪表盘 |
| `login.vue` | 用户登录 |

### 4.2 微信小程序功能 (patient-wx)

| 页面路径 | 功能说明 |
|----------|----------|
| `pages/index/` | 首页 |
| `pages/mine/` | 个人中心 |
| `registration/dept_sub_plan/` | 科室预约挂号 |
| `registration/worker_schedule/` | 工人排班查询 |
| `registration/prescription/` | 处方查看 |
| `video_diagnose/doctor_list/` | 视频诊疗医生列表 |
| `video_diagnose/order_list/` | 视频诊疗订单列表 |
| `pages/message_list/` | 消息列表 |
| `user/face_camera/` | 人脸识别认证 |
| `user/fill_user_info/` | 用户信息填写 |
| `user/gas_worker_info/` | 燃气工人信息 |

---

## 五、数据库表结构

### 5.1 核心数据表

| 表名 | 说明 |
|------|------|
| `gas_user` | 燃气用户表 |
| `gas_worker` | 燃气工人表 |
| `gas_worker_plan` | 工人工作计划表 |
| `gas_hazard_info` | 隐患信息表 |
| `mis_user` | 系统用户表 |
| `mis_role` | 角色表 |
| `mis_dept` | 部门表 |
| `medical_dept` | 医疗科室表 |
| `medical_dept_sub` | 医疗子科室表 |
| `doctor` | 医生表 |
| `doctor_work_plan` | 医生排班表 |
| `video_diagnose` | 视频诊疗表 |
| `face_auth` | 人脸认证表 |

### 5.2 Mapper 文件位置

- gas-api: `gas-api/src/main/resources/mapper/`
- patient-wx-api: `patient-wx-api/src/main/resources/mapper/`

---

## 六、配置文件说明

### 6.1 后端配置文件

| 文件 | 位置 | 说明 |
|------|------|------|
| `application.yml` | `gas-api/src/main/resources/` | 主配置文件 |
| `pom.xml` | `gas-api/` | Maven 依赖配置 |

### 6.2 前端配置文件

| 文件 | 位置 | 说明 |
|------|------|------|
| `package.json` | `gas-vue/` | NPM 依赖配置 |
| `vite.config.js` | `gas-vue/` | Vite 构建配置 |
| `src/router/index.js` | `gas-vue/` | 路由配置 |

### 6.3 小程序配置文件

| 文件 | 位置 | 说明 |
|------|------|------|
| `pages.json` | `patient-wx/` | 页面配置 |
| `manifest.json` | `patient-wx/` | 应用配置 |
| `project.config.json` | `patient-wx/` | 微信开发者工具配置 |

---

## 七、启动命令

### 7.1 后端启动

```bash
# 进入后端目录
cd gas-api

# 安装依赖并启动
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/gas-api-0.0.1-SNAPSHOT.jar
```

### 7.2 前端启动

```bash
# 进入前端目录
cd gas-vue

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 生产构建
npm run build
```

### 7.3 小程序启动

1. 使用 HBuilderX 打开 `patient-wx` 目录
2. 运行到微信开发者工具进行调试

---

## 八、API 接口说明

### 8.1 后端端口配置

- gas-api: 默认端口需查看 `application.yml`
- patient-wx-api: 默认端口需查看 `application.yml`

### 8.2 认证方式

- 使用 Sa-Token 进行 JWT 认证
- 请求头需携带 Token

---

## 九、开发规范

### 9.1 代码风格

- 后端使用 Lombok 简化代码
- 前端使用 Composition API (Vue 3)
- 样式使用 Less/SCSS 预处理器

### 9.2 目录规范

```
gas-api/src/main/java/com/example/gas/api/
├── common/          # 公共类（统一响应等）
├── controller/      # 控制器层
├── service/         # 服务层
├── dao/             # 数据访问层
├── entity/          # 实体类
└── config/          # 配置类

gas-vue/src/
├── assets/          # 静态资源
├── components/      # 公共组件
├── icons/           # SVG 图标
├── router/          # 路由配置
├── utils/           # 工具函数
└── views/           # 页面组件
```

---

## 十、注意事项

1. **数据库连接**: 需要在 `application.yml` 中配置正确的 MySQL 连接信息
2. **Redis 配置**: 需要启动 Redis 服务
3. **云服务配置**: 腾讯云 TRTC、微信支付等需要在配置文件中填写正确的密钥
4. **MinIO 配置**: 对象存储服务需要正确配置

---

## 十一、待开发/优化功能

- [ ] 燃气巡查计划功能完善
- [ ] 燃气管理系统功能展示优化
- [ ] 视频诊疗稳定性优化
- [ ] 小程序用户体验优化

---

## 十二、联系方式与协作

**协作模式**: 手机端 Trae 提问 → 电脑端修改项目

手机端 Trae 可以：
1. 读取此文档了解项目结构
2. 回答关于项目的问题
3. 提供开发建议
4. 指导电脑端进行代码修改

---

*文档最后更新: 2026-05-07*
