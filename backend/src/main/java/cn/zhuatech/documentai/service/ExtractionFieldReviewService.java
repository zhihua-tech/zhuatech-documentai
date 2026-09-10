/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 对文档抽取字段执行置信度、必填项、业务规则、隐私与复核职责分离决策。 */
@Service
public class ExtractionFieldReviewService {
    public ReviewResult evaluate(ReviewRequest request) {
        List<String> blockers = new ArrayList<>();
        List<String> reviewReasons = new ArrayList<>();
        List<FieldDecision> fields = new ArrayList<>();

        if (!request.malwareScanPassed()) blockers.add("文档安全扫描未通过");
        if (!request.sourceHashVerified()) blockers.add("原始文档哈希校验失败");
        if (request.tamperDetected()) blockers.add("文档存在篡改风险");

        for (FieldResult field : request.fields()) {
            String value = field.value() == null ? "" : field.value().trim();
            if (field.required() && value.isEmpty()) {
                blockers.add("必填字段缺失: " + field.name());
                fields.add(new FieldDecision(field.name(), FieldStatus.REJECT, "必填值为空"));
            } else if (!field.businessRulePassed()) {
                blockers.add("字段业务规则失败: " + field.name());
                fields.add(new FieldDecision(field.name(), FieldStatus.REJECT, "业务规则校验失败"));
            } else if (field.pii() && !field.masked()) {
                blockers.add("个人信息字段未脱敏: " + field.name());
                fields.add(new FieldDecision(field.name(), FieldStatus.MASK_REQUIRED, "入库前必须脱敏"));
            } else if (field.confidence() < request.minAutoConfidence()) {
                reviewReasons.add("低置信度字段: " + field.name());
                fields.add(new FieldDecision(field.name(), FieldStatus.HUMAN_REVIEW, "置信度低于自动入库阈值"));
            } else {
                fields.add(new FieldDecision(field.name(), FieldStatus.ACCEPT, "字段校验通过"));
            }
        }

        if (request.duplicateDetected()) reviewReasons.add("检测到疑似重复文档");
        if (request.finalApprovalRequired() && !request.finalApprovalComplete()) {
            reviewReasons.add("最终入库审批尚未完成");
        }

        if (!blockers.isEmpty()) {
            return new ReviewResult(Decision.BLOCKED, List.copyOf(blockers), List.copyOf(reviewReasons),
                    List.copyOf(fields), "修复安全、必填、业务规则或隐私问题后重新提交");
        }

        if (!reviewReasons.isEmpty()) {
            if (!request.humanReviewEnabled()) {
                return new ReviewResult(Decision.BLOCKED, List.of("需要人工复核但复核流程未启用"),
                        List.copyOf(reviewReasons), List.copyOf(fields), "启用人工复核队列");
            }
            if (request.reviewerApproval()) {
                if (request.extractorUserId().equals(request.reviewerId())) {
                    return new ReviewResult(Decision.BLOCKED, List.of("抽取提交人与复核人必须职责分离"),
                            List.copyOf(reviewReasons), List.copyOf(fields), "由独立复核人重新审批");
                }
                if (!request.finalApprovalRequired() || request.finalApprovalComplete()) {
                    return new ReviewResult(Decision.RELEASE_REVIEWED, List.of(), List.copyOf(reviewReasons),
                            List.copyOf(fields), "归档人工修订、审批人与字段证据后入库");
                }
            }
            return new ReviewResult(Decision.HUMAN_REVIEW, List.of(), List.copyOf(reviewReasons),
                    List.copyOf(fields), "进入人工复核队列并保留原文定位");
        }

        return new ReviewResult(Decision.AUTO_RELEASE, List.of(), List.of(), List.copyOf(fields),
                "写入结构化数据并记录模板、模型和来源版本");
    }

    public record ReviewRequest(
            @NotBlank String documentId,
            @NotBlank String documentType,
            @NotBlank String schemaVersion,
            @NotEmpty List<@Valid FieldResult> fields,
            @DecimalMin("0.0") @DecimalMax("1.0") double minAutoConfidence,
            boolean duplicateDetected,
            boolean tamperDetected,
            boolean malwareScanPassed,
            boolean sourceHashVerified,
            boolean humanReviewEnabled,
            @NotBlank String extractorUserId,
            String reviewerId,
            boolean reviewerApproval,
            boolean finalApprovalRequired,
            boolean finalApprovalComplete
    ) {}

    public record FieldResult(@NotBlank String name, String value,
                              @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
                              boolean required, boolean pii, boolean masked, boolean businessRulePassed) {}

    public record FieldDecision(String name, FieldStatus status, String reason) {}
    public record ReviewResult(Decision decision, List<String> blockers, List<String> reviewReasons,
                               List<FieldDecision> fields, String nextAction) {}

    public enum FieldStatus { ACCEPT, HUMAN_REVIEW, MASK_REQUIRED, REJECT }
    public enum Decision { AUTO_RELEASE, HUMAN_REVIEW, RELEASE_REVIEWED, BLOCKED }
}
