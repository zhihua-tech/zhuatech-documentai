# DocumentAI 架构

Vue 3 提供文档运营管理端与人工复核 H5；Spring Boot 的 `DocumentAnalysisService` 综合 OCR、版式、来源、字段完整性、签章和重复指纹；JPA/Flyway/MySQL 保存批次、字段证据、修订和复核轨迹。

生产环境必须实施文档分级、最小权限、传输与存储加密、脱敏样本和删除策略。低置信或敏感结果必须进入人工复核。

版权所有 © 2026 上海如静知华信息科技有限公司。
