/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.service;
import jakarta.validation.constraints.*; import org.springframework.stereotype.Service; import java.util.*;
/**
 * 对OCR、字段完整性、版式与来源可信度进行文档质量判定。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class DocumentAnalysisService {
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public Result extract(Request r){int c=(int)Math.round(r.ocrConfidence()*.45+r.layoutConfidence()*.25+r.sourceTrustScore()*.30);List<String>w=new ArrayList<>();if(r.ocrConfidence()<80)w.add("OCR识别置信度偏低");if(r.missingRequiredFields()>0){c-=Math.min(30,r.missingRequiredFields()*8);w.add("存在必填字段缺失");}if(r.signatureRequired()&&!r.signatureDetected()){c-=25;w.add("未检测到所需签章");}if(r.duplicateFingerprint()){c-=20;w.add("文档指纹与历史文件重复");}c=Math.max(0,Math.min(100,c));String d=c>=85&&w.isEmpty()?"AUTO_ACCEPT":c>=60?"HUMAN_REVIEW":"REJECT";if(w.isEmpty())w.add("字段、版式与来源校验通过");return new Result(r.documentNo(),r.documentType(),c,d,Math.max(0,r.totalFields()-r.missingRequiredFields()),r.missingRequiredFields(),w,!"AUTO_ACCEPT".equals(d));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record Request(@NotBlank String documentNo,@NotBlank String documentType,@Min(0)@Max(100)int ocrConfidence,@Min(0)@Max(100)int layoutConfidence,@Min(0)@Max(100)int sourceTrustScore,@Min(1)int totalFields,@Min(0)int missingRequiredFields,boolean signatureRequired,boolean signatureDetected,boolean duplicateFingerprint){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record Result(String documentNo,String documentType,int confidenceScore,String decision,int extractedFields,int missingFields,List<String>warnings,boolean humanReviewRequired){}
}
