/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.controller;

import cn.zhuatech.documentai.common.ApiResponse;
import cn.zhuatech.documentai.service.DocumentIngestionGovernanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/documentai")
public class DocumentIngestionGovernanceController {
    private final DocumentIngestionGovernanceService service;

    public DocumentIngestionGovernanceController(DocumentIngestionGovernanceService service) {
        this.service = service;
    }

    @PostMapping("/ingestion-governance")
    public ApiResponse<DocumentIngestionGovernanceService.DecisionResult> evaluate(
            @Valid @RequestBody DocumentIngestionGovernanceService.IngestionRequest request) {
        return ApiResponse.ok("文档接入治理评估完成", service.evaluate(request));
    }
}
