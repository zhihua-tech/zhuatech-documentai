/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.controller;

import cn.zhuatech.documentai.common.ApiResponse;
import cn.zhuatech.documentai.service.ExtractionFieldReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/documentai")
public class ExtractionFieldReviewController {
    private final ExtractionFieldReviewService service;

    public ExtractionFieldReviewController(ExtractionFieldReviewService service) {
        this.service = service;
    }

    @PostMapping("/field-review-decision")
    public ApiResponse<ExtractionFieldReviewService.ReviewResult> evaluate(
            @Valid @RequestBody ExtractionFieldReviewService.ReviewRequest request) {
        return ApiResponse.ok("文档字段复核决策完成", service.evaluate(request));
    }
}
