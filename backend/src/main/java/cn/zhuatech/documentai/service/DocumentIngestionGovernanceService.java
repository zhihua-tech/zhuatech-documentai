/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.service;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 在 OCR 和字段抽取之前执行文件、来源、隐私、保留期与重复件治理。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class DocumentIngestionGovernanceService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public DecisionResult evaluate(IngestionRequest request) {
        List<String> blockers = new ArrayList<>();
        List<String> reviewReasons = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        String mimeType = request.mimeType().toLowerCase(Locale.ROOT);
        Set<String> allowedMimeTypes = request.allowedMimeTypes().stream()
                .map(value -> value.toLowerCase(Locale.ROOT)).collect(java.util.stream.Collectors.toSet());

        if (!request.malwareScanPassed()) blockers.add("恶意文件扫描未通过");
        if (!request.checksumVerified()) blockers.add("文件校验和未通过来源校验");
        if (!allowedMimeTypes.contains(mimeType)) blockers.add("文件类型不在允许清单: " + mimeType);
        if (request.fileSizeBytes() > request.maxFileSizeBytes()) blockers.add("文件大小超过接入上限");
        if (request.retentionDays() > request.maxRetentionDays() && !request.legalHold()) {
            blockers.add("保留期超过策略上限且无法律保全依据");
        }
        if (request.containsPersonalData() && !request.processingBasisRecorded()) {
            blockers.add("个人信息处理依据未登记");
        }
        if (request.classification().requiresEncryption() && !request.encryptionAtRest()) {
            blockers.add("高敏文档未启用静态加密");
        }

        if (!request.sourceTrusted()) reviewReasons.add("文档来源尚未进入可信来源清单");
        if (request.duplicateDetected() && !request.duplicateOverrideApproved()) {
            reviewReasons.add("检测到重复文档且未批准覆盖");
        }
        if (request.classification() == Classification.RESTRICTED && !request.humanApproval()) {
            reviewReasons.add("受限文档需要数据责任人审批");
        }

        String storageClass = switch (request.classification()) {
            case PUBLIC -> "STANDARD";
            case INTERNAL -> "PRIVATE";
            case CONFIDENTIAL, RESTRICTED -> "ENCRYPTED_PRIVATE";
        };
        int effectiveRetentionDays = request.legalHold()
                ? request.retentionDays() : Math.min(request.retentionDays(), request.maxRetentionDays());

        if (!blockers.isEmpty()) {
            actions.add("隔离原文件，禁止进入 OCR、向量化和业务入库流程");
            return result(Decision.REJECT, storageClass, effectiveRetentionDays, blockers, reviewReasons, actions);
        }
        if (!reviewReasons.isEmpty()) {
            actions.add("转入人工接入队列并保留来源、哈希、分类和审批证据");
            return result(Decision.REVIEW, storageClass, effectiveRetentionDays, blockers, reviewReasons, actions);
        }
        actions.add("写入不可变接入审计并按分类启动 OCR 与字段抽取");
        if (request.containsPersonalData()) actions.add("对个人信息字段启用最小化抽取与脱敏展示");
        return result(Decision.ACCEPT, storageClass, effectiveRetentionDays, blockers, reviewReasons, actions);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private DecisionResult result(Decision decision, String storageClass, int retentionDays,
                                  List<String> blockers, List<String> reviewReasons, List<String> actions) {
        return new DecisionResult(decision, storageClass, retentionDays, List.copyOf(blockers),
                List.copyOf(reviewReasons), List.copyOf(actions));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record IngestionRequest(
            @NotBlank String documentId,
            @NotBlank String sourceSystem,
            @NotBlank String mimeType,
            @NotEmpty Set<@NotBlank String> allowedMimeTypes,
            @Positive long fileSizeBytes,
            @Positive long maxFileSizeBytes,
            @NotBlank @Pattern(regexp = "(?i)[a-f0-9]{64}") String sha256,
            @NotNull Classification classification,
            boolean malwareScanPassed,
            boolean checksumVerified,
            boolean sourceTrusted,
            boolean containsPersonalData,
            boolean processingBasisRecorded,
            boolean encryptionAtRest,
            boolean duplicateDetected,
            boolean duplicateOverrideApproved,
            boolean humanApproval,
            boolean legalHold,
            @Min(1) @Max(36500) int retentionDays,
            @Min(1) @Max(36500) int maxRetentionDays
    ) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record DecisionResult(Decision decision, String storageClass, int effectiveRetentionDays,
                                 List<String> blockers, List<String> reviewReasons, List<String> actions) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Decision { ACCEPT, REVIEW, REJECT }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Classification {
        PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED;
        /**
         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
         */
        public boolean requiresEncryption() {
            return this == CONFIDENTIAL || this == RESTRICTED;
        }
    }
}
