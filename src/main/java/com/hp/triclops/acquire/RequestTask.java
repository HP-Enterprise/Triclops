package com.hp.triclops.acquire;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by luj on 2015/12/4.
 */
public class RequestTask  implements Runnable{
    private String requestHexString;
    private Logger _logger= LoggerFactory.getLogger(RequestTask.class);
    private HashMap<String,Channel> channels;

    public RequestTask(String requestHexString) {
        super();
        this.requestHexString = requestHexString;
    }

    @Override
    public void run() {
        //handle request
      _logger.info("handle request:" + requestHexString);
    }

}
