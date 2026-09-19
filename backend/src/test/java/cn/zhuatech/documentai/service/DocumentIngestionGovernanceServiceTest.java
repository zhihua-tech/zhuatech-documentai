/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.service;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentIngestionGovernanceServiceTest {
    private static final String HASH = "a".repeat(64);
    private final DocumentIngestionGovernanceService service = new DocumentIngestionGovernanceService();

    @Test
    void acceptsTrustedAndGovernedDocument() {
        var result = service.evaluate(request(true, false, true, true, true,
                DocumentIngestionGovernanceService.Classification.CONFIDENTIAL));
        assertThat(result.decision()).isEqualTo(DocumentIngestionGovernanceService.Decision.ACCEPT);
        assertThat(result.storageClass()).isEqualTo("ENCRYPTED_PRIVATE");
        assertThat(result.actions()).hasSize(2);
    }

    @Test
    void sendsUntrustedOrDuplicateDocumentToReview() {
        var result = service.evaluate(request(false, true, false, true, true,
                DocumentIngestionGovernanceService.Classification.INTERNAL));
        assertThat(result.decision()).isEqualTo(DocumentIngestionGovernanceService.Decision.REVIEW);
        assertThat(result.reviewReasons()).hasSize(2);
    }

    @Test
    void rejectsUnsafePersonalDocument() {
        var request = new DocumentIngestionGovernanceService.IngestionRequest(
                "DOC-3", "mail", "application/pdf", Set.of("application/pdf"), 500, 1_000,
                HASH, DocumentIngestionGovernanceService.Classification.RESTRICTED,
                false, false, true, true, false, false,
                false, false, false, false, 800, 365);
        var result = service.evaluate(request);
        assertThat(result.decision()).isEqualTo(DocumentIngestionGovernanceService.Decision.REJECT);
        assertThat(result.blockers()).hasSize(5);
    }

    private DocumentIngestionGovernanceService.IngestionRequest request(
            boolean trusted, boolean duplicate, boolean overrideApproved,
            boolean encryption, boolean humanApproval,
            DocumentIngestionGovernanceService.Classification classification) {
        return new DocumentIngestionGovernanceService.IngestionRequest(
                "DOC-1", "erp", "application/pdf", Set.of("application/pdf", "image/png"),
                500, 1_000, HASH, classification, true, true, trusted,
                true, true, encryption, duplicate, overrideApproved, humanApproval,
                false, 365, 730);
    }
}
