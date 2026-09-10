# DocumentAI API
Copyright 2026 上海如静知华信息科技有限公司。业务接口使用 JWT 鉴权。

| 方法 | 地址 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录 |
| POST | `/api/ai/document/extract` | 分类、字段抽取和可信校验 |
| GET | `/api/admin/dashboard` | 文档处理与质量看板 |
| GET | `/api/admin/work-orders` | 文档处理批次 |
| GET | `/api/workspace/dashboard` | 人工复核工作台 |
| POST | `/api/enterprise/documentai/extraction-release` | 校验文档抽取服务生产发布条件 |
| POST | `/api/enterprise/documentai/field-review-decision` | 执行逐字段质量、隐私与独立复核决策 |

可信校验综合 OCR、版式、来源、缺失字段、签章与重复指纹，返回 `AUTO_ACCEPT`、`HUMAN_REVIEW` 或 `REJECT`。
