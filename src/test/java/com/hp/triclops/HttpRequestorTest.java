package com.hp.triclops;

/**
 * Created by yuh on 2015/11/19.
 */

import com.hp.triclops.utils.HttpRequestTool;
import com.hp.triclops.utils.HttpRequestor;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestorTest {

    @Test
    public  void test()throws Exception {

        /* Post Request */
/*        @RequestParam("sourceId") Integer sourceId,
        @RequestParam("resourceFrom") Integer resourceFrom,
        @RequestParam("targetType") Integer targetType,
        @RequestParam("targetId") String targetId,
        @RequestParam("resourceTo") Integer resourceTo,
        @RequestParam("funType") Integer funType,
        @RequestParam("pType") Integer pType,
        @RequestParam("contentType") Integer contentType,
        @RequestParam("textContent") String textContent,
        @RequestParam("messageNums") Integer messageNums,
        @RequestParam("cleanFlag") Integer cleanFlag,*/

        Map pushMsg = new HashMap();
        //pushMsg.put("sourceId",1);
/*        pushMsg.put("resourceFrom",1);
        pushMsg.put("targetType",1);
        pushMsg.put("resourceTo",1);
        pushMsg.put("contentType",1);
        pushMsg.put("messageNums",1);
        pushMsg.put("cleanFlag",1);*/
        pushMsg.put("targetId", "1");
        pushMsg.put("resourceTo","1");
        pushMsg.put("funType", "2");
        //MultipartFile mFile = null;
       // pushMsg.put("file",mFile);
      // System.out.println(new HttpRequestor().doPost("http://localhost:8080/api/message/filePush", pushMsg));

       // String ADD_URL = "http://10.0.12.146:8080/api/message/push?sourceId=1&resourceFrom=1&targetType=1&targetId=1&resourceTo=1&funType=1&pType=1&contentType=1&textContent=1&messageNums=1&cleanFlag=1";
        //System.out.println(ADD_URL);
        /* Get Request */
       // System.out.println(new HttpRequestor().doGet("http://10.0.12.146:8080/api/message/filePush"));
        //new HttpRequestor().doGet("http://localhost:8080/api/message/query?ids=1");

        //new HttpRequestTool().upload();
    }

}
