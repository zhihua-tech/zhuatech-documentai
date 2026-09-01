# 企业级文档抽取服务发布

`POST /api/enterprise/documentai/extraction-release` 检查文档分类、安全扫描、隐私、样本准确率、关键字段、人工复核、版本、审计、监控和回滚，返回 `RELEASE / PILOT / BLOCKED`。

建议生产部署把低置信度和关键字段强制转人工复核，并保留原文、抽取结果、修改记录与版本证据。
