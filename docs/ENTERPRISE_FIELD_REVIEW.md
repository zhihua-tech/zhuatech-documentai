# 企业文档字段复核与入库决策

Copyright © 2026 上海如静知华信息科技有限公司 · <https://www.zhuatech.cn/>

`POST /api/enterprise/documentai/field-review-decision` 对一次文档抽取中的每个字段进行真实可执行的质量控制：必填值、字段置信度、业务规则、个人信息脱敏、原文哈希、安全扫描、篡改风险和重复文档都会进入统一决策。

接口返回逐字段状态 `ACCEPT / HUMAN_REVIEW / MASK_REQUIRED / REJECT`，以及批次级 `AUTO_RELEASE / HUMAN_REVIEW / RELEASE_REVIEWED / BLOCKED`。低置信度或疑似重复文档可由独立复核人确认后入库；抽取人与复核人为同一人时会触发职责分离阻断。

生产接入时，调用方应保存字段原文坐标、修改前后值、模板与模型版本、复核人和审批时间，并使用响应中的 `nextAction` 驱动人工队列或结构化数据写入。
