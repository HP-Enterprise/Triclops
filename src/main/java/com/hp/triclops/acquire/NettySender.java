package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.OutputHexService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luj on 2015/9/22.
 */
public class NettySender extends Thread {
    private SocketRedis socketRedis;
    // 日志
    private Logger _logger;
    private String serverId;
    private DataTool dataTool;
    private OutputHexService outputHexService;
    private ConcurrentHashMap<String, Channel> channels;

    public NettySender(ConcurrentHashMap<String, Channel> cs, SocketRedis s, String serverId, DataTool dt, OutputHexService outputHexService) {
        this.channels = cs;
        this.socketRedis = s;
        this.serverId = serverId;
        this.dataTool = dt;
        this.outputHexService = outputHexService;
        this._logger = LoggerFactory.getLogger(NettySender.class);
    }

    public void run() {

        while (true) {
//            try{
//                Thread.sleep(10);//开发调试用
//            }catch (InterruptedException e){e.printStackTrace(); }
//            Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
//            _logger.info("Connection count>>:" + channels.keySet().size()+"|Thread count>>:" + maps.size());
            //读取数据库中所有的命令集合
            Set<String> setKey = socketRedis.getKeysSet(serverId + "-" + dataTool.out_cmd_preStr + "*");//read output:*  change to-> server1-output:*
            if (setKey.size() > 0) {
                _logger.info(setKey.size() + " message wait to be handle ");
            }
            Iterator keys = setKey.iterator();
            while (keys.hasNext()) {
                //遍历待发数据,处理
                String k = (String) keys.next();
                String scKey = k.replace(dataTool.out_cmd_preStr, "");
                SendMessage(scKey, k);
            }
        }
    }

    public void SendMessage(String scKey, String k) {
        //将output:{vin}对应的十六进制字符串发送给客户端
        String msg = socketRedis.popSetOneString(k);
        _logger.info("sckey>>" + scKey + "|send msg:" + msg);
        String _vin = scKey.split("-")[1];
        Channel ch = channels.get(_vin);
        if (ch != null) {
            //此处存在一个逻辑问题，对于已经确定知道客户端当前没有连接的消息如何处理，是依旧取出发送失败还是保留在redis中
            //ch.writeAndFlush(dataTool.getByteBuf(msg));
            new CommandHandler(ch, _vin, socketRedis, dataTool, outputHexService, serverId, msg).start();
        } else {
            //一般情况下，不会出现此种情况，出现此情况是由于连接判断和连接实际情况不一致导致，比如异常的连接断开单服务端没有收到任何信息，依然认为连接可用。
//            _logger.info("Connection is Dead"+_vin);
//            socketRedis.deleteHashString(dataTool.connection_hashmap_name,_vin);//从redis里面清除连接记录
            //socketRedis.saveSetString(k, msg,-1);发送时异常不返回redis，执行结果依靠响应数据来判断
        }
    }
}
