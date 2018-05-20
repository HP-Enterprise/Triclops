package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.DataHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by luj on 2015/9/28.
 */
public class DataHandlerTask implements Runnable {
    private SocketRedis socketRedis;
    private DataHandleService dataHandleService;
    private byte dataType;
    private Logger _logger;
    private DataTool dataTool;
    private List<String> msgList;

    public DataHandlerTask(byte dataType, SocketRedis socketRedis, DataHandleService dataHandleService, DataTool dt, List<String> msgList) {
        this.dataType = dataType;
        this.socketRedis = socketRedis;
        this.dataHandleService = dataHandleService;
        this.dataTool = dt;
        this.msgList = msgList;
        this._logger = LoggerFactory.getLogger(DataHandlerTask.class);
    }

    public synchronized void run() {
//        long startTime = System.currentTimeMillis();
        handleMessageList(dataType, msgList);
//        long time = System.currentTimeMillis() - startTime;
//        if (time > 100)
//            _logger.info("Handle msg time:" + time);
    }

    public void handleMessageList(byte dataType, List<String> msgList) {
        //将input{suffix}:{vin}对应的十六进制字符串处理入库
//        _logger.info("[0x21][0x22][0x23][0x24][0x25][0x28][0x29]处理数据>>>>>:" + message);
        dataHandleService.saveMessage(dataType, msgList);
    }
}
