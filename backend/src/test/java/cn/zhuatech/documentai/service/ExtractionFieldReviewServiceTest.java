/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class ExtractionFieldReviewServiceTest {
    private final ExtractionFieldReviewService service = new ExtractionFieldReviewService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void autoReleasesCompleteHighConfidenceFields() {
        var result = service.evaluate(request(List.of(field("invoiceNo", "INV-100", 0.98,
                true, false, false, true)), false, false, null, false, false));
        assertThat(result.decision()).isEqualTo(ExtractionFieldReviewService.Decision.AUTO_RELEASE);
        assertThat(result.fields()).extracting(ExtractionFieldReviewService.FieldDecision::status)
                .containsExactly(ExtractionFieldReviewService.FieldStatus.ACCEPT);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void routesLowConfidenceAndDuplicateDocumentToReview() {
        var result = service.evaluate(request(List.of(field("amount", "1080.00", 0.66,
                true, false, false, true)), true, false, null, false, false));
        assertThat(result.decision()).isEqualTo(ExtractionFieldReviewService.Decision.HUMAN_REVIEW);
        assertThat(result.reviewReasons()).hasSize(2);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void releasesReviewedFieldsWithIndependentApprover() {
        var result = service.evaluate(request(List.of(field("amount", "1080.00", 0.66,
                true, false, false, true)), false, false, "reviewer-b", true, true));
        assertThat(result.decision()).isEqualTo(ExtractionFieldReviewService.Decision.RELEASE_REVIEWED);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void blocksTamperingMissingValuesAndUnmaskedPii() {
        var result = service.evaluate(request(List.of(
                field("customerName", "", 0.99, true, false, false, true),
                field("mobile", "13800000000", 0.99, true, true, false, true)
        ), false, true, null, false, false));
        assertThat(result.decision()).isEqualTo(ExtractionFieldReviewService.Decision.BLOCKED);
        assertThat(result.blockers()).hasSize(3);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private ExtractionFieldReviewService.ReviewRequest request(
            List<ExtractionFieldReviewService.FieldResult> fields, boolean duplicate, boolean tamper,
            String reviewer, boolean approval, boolean finalApproval) {
        return new ExtractionFieldReviewService.ReviewRequest("DOC-100", "invoice", "v2", fields,
                0.9, duplicate, tamper, true, true, true, "extractor-a", reviewer, approval,
                finalApproval, finalApproval);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private ExtractionFieldReviewService.FieldResult field(String name, String value, double confidence,
                                                            boolean required, boolean pii, boolean masked,
                                                            boolean businessRulePassed) {
        return new ExtractionFieldReviewService.FieldResult(name, value, confidence, required, pii, masked,
                businessRulePassed);
    }
}
