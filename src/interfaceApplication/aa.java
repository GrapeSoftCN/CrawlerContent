package interfaceApplication;

import java.util.Base64.Encoder;

import httpServer.booter;
import io.netty.handler.codec.dns.DefaultDnsRecordEncoder;
import nlogger.nlogger;
import security.codec;

public class aa {
    public static void main(String[] args) {
       String a ="{\"title_0\":\"{\"content\":\"郊区人民政府直属机构\"}\",\"content_0\":\"{\"content\":\"铜陵市郊区在新一轮机构改革方案中列出政府工作部门和直属事业单位，未作直属机构划分\"}\"}";
       a = codec.encodeFastJSON(a);
       System.out.println(a);
    }
}
