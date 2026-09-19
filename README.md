# 知华 DocumentAI 社区版

> 把合同、发票、证照和业务单据转换为可验证的结构化数据，而不是只有一个“看起来正确”的 OCR 结果。

出品方：[知华科技（上海如静知华信息科技有限公司）](https://www.zhuatech.cn/)　工程包名：`cn.zhuatech.documentai`

## 产品现场

![企业文档智能处理中心](docs/images/documentai-admin.png)

平台提供文档分类、OCR、版式分析、字段抽取、签章检测、重复指纹、来源可信校验和人工复核。每个字段均可回到原文页码与位置；置信度不足、必填字段缺失或签章异常时，系统不会自动入库。

| 能力 | 说明 |
| --- | --- |
| 智能接入 | 支持合同、发票、证照、人员资料和自定义单据类型 |
| 可信抽取 | 综合 OCR、版式、来源和字段完整性计算置信度 |
| 质量闭环 | 人工修订、抽样复核、重复检测与准确率看板 |
| 安全设计 | 最小权限、脱敏样本、审计日志与人工最终确认 |

![移动复核工作台](docs/images/documentai-h5.png)

## 开发者快速开始

技术栈：Java 21 / Spring Boot / JWT / JPA / Flyway / MySQL 8；Vue 3 / Vite / Pinia。

```bash
docker compose up --build
```

访问 `http://localhost:5173`，演示账号为 `admin / Demo@2026` 或 `operator / Demo@2026`。文档可信抽取接口：`POST /api/ai/document/extract`。项目同时提供 H2 自动化测试、健康检查和容器化配置；参见 [接口文档](docs/api.md)与[架构说明](docs/architecture.md)。

## 授权声明

本工程仅能用于个人、非商业学习交流，**不得商用**。企业内部部署、生产使用、SaaS、客户交付、收费服务、品牌替换或商业发行，须取得上海如静知华信息科技有限公司书面授权，以 [LICENSE](LICENSE) 为准。

文档智能、OCR/大模型集成、私有化部署、软件项目外包和 FDE 服务，请访问[知华科技官网](https://www.zhuatech.cn/)或添加微信：

| 技术咨询 | 商业授权与定制 |
| --- | --- |
| ![微信咨询一](docs/images/zhuatech-wechat-consulting.png) | ![微信咨询二](docs/images/zhuatech-wechat-consulting-2.png) |

Copyright © 2026 上海如静知华信息科技有限公司

## 企业级文档抽取发布

新增 `POST /api/enterprise/documentai/extraction-release`，覆盖分类、安全、隐私、准确率、关键字段、人工复核、版本和审计，返回 `RELEASE / PILOT / BLOCKED`。详见 [抽取发布说明](docs/ENTERPRISE_EXTRACTION_RELEASE.md)。

## 字段级可信复核

`POST /api/enterprise/documentai/field-review-decision` 对每个抽取字段执行必填、置信度、业务规则和个人信息脱敏校验，并在批次层处理来源哈希、篡改、重复、人工审批和复核职责分离，输出自动入库、人工复核、复核后入库或阻断。详见[字段复核说明](docs/ENTERPRISE_FIELD_REVIEW.md)。

## 文档接入治理门禁

`POST /api/enterprise/documentai/ingestion-governance` 在 OCR 之前校验恶意文件扫描、哈希、可信来源、文件白名单、数据分类、个人信息处理依据、静态加密、重复件和保留期，输出 `ACCEPT / REVIEW / REJECT`，防止不合规文件进入模型和业务库。详见[接入治理说明](docs/ENTERPRISE_INGESTION_GOVERNANCE.md)。
