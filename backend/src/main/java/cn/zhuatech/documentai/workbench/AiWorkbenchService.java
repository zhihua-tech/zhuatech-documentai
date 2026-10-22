/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.documentai.workbench;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;
import static cn.zhuatech.documentai.workbench.WorkbenchInput.*;
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
    /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public Map<String,Object> execute(Map<String,Object> r){
        validate(r);

        keys(r,"text","chunkSize","overlap");String input=text(r,"text",64000);int size=integer(r,"chunkSize",8,8000),overlap=integer(r,"overlap",0,7999);if(overlap>=size)throw new IllegalArgumentException("重叠必须小于分块长度");
        int[] points=input.codePoints().toArray();List<Map<String,Object>> chunks=new ArrayList<>();int start=0;
        while(start<points.length){if(chunks.size()>=500)throw new IllegalArgumentException("分块超过500块，请增大分块长度");int end=Math.min(start+size,points.length);chunks.add(Map.of("index",chunks.size(),"startCodePoint",start,"endCodePoint",end,"text",new String(points,start,end-start)));if(end==points.length)break;start=end-overlap;}
        return Map.of("chunks",List.copyOf(chunks),"codePoints",points.length,"offsetUnit","UNICODE_CODE_POINT","overlap",overlap);

    }
}
