/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai; import cn.zhuatech.documentai.service.DocumentAnalysisService; import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class DocumentAnalysisServiceTests {private final DocumentAnalysisService s=new DocumentAnalysisService();
 @Test void acceptsTrustedCompleteDocument(){var r=s.extract(new DocumentAnalysisService.Request("DOC-1","采购合同",98,95,96,18,0,true,true,false));assertThat(r.decision()).isEqualTo("AUTO_ACCEPT");}
 @Test void rejectsUnsignedDuplicate(){var r=s.extract(new DocumentAnalysisService.Request("DOC-2","发票",68,72,60,12,3,true,false,true));assertThat(r.decision()).isEqualTo("REJECT");assertThat(r.humanReviewRequired()).isTrue();}}
