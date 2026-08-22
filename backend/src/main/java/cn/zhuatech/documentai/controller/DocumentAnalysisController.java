/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.controller;

import cn.zhuatech.documentai.common.ApiResponse;
import cn.zhuatech.documentai.service.DocumentAnalysisService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/document")
@PreAuthorize("hasAnyRole('DOMAIN_USER','DOMAIN_OPERATOR','ADMIN')")
public class DocumentAnalysisController {
    private final DocumentAnalysisService service;
    public DocumentAnalysisController(DocumentAnalysisService service) { this.service = service; }
    @PostMapping("/extract")
    public ApiResponse<DocumentAnalysisService.Result> extract(@Valid @RequestBody DocumentAnalysisService.Request request) {
        return ApiResponse.ok("文档抽取与可信校验完成", service.extract(request));
    }
}
