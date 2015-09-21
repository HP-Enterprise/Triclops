package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by luj on 2015/9/15.
 */
public class Receiver extends Thread{
//本类代码目前已被重新整理为多线程处理，目前废弃，代码暂时保留。将在后续提交中删除
    private int _acquirePort;


    SocketRedis socketRedis;

    DataTool dataTool;
    // 日志
    private Logger _logger;

    private Selector selector = null;

    private HashMap<String,SocketChannel> channels;
    public Receiver(HashMap<String,SocketChannel> cs,SocketRedis s,DataTool dt,int port){
        this.channels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
        this._acquirePort=port;
        this._logger = LoggerFactory.getLogger(Receiver.class);
    }

    public synchronized void run()
    {
        try{
            _logger.info("start listen...");
            listen();
        }catch (Exception e){
            _logger.info(e.toString());
        }
    }

    public void listen() throws IOException {
        this._logger = LoggerFactory.getLogger(AcquirePort.class);
        selector = Selector.open();
        // 通过open方法来打开一个未绑定的ServerSocketChannel实例
        ServerSocketChannel server = ServerSocketChannel.open();
        InetSocketAddress isa = new InetSocketAddress(_acquirePort);
        // 将该ServerSocketChannel绑定到指定IP地址
        server.socket().bind(isa);
        // 设置ServerSocket以非阻塞方式工作
        server.configureBlocking(false);
        // 将server注册到指定Selector对象
        server.register(selector, SelectionKey.OP_ACCEPT);
        // 定义准备执行读取数据的ByteBuffer
        ByteBuffer buff = ByteBuffer.allocate(1024);

        while (selector.select() > 0) {
            // 依次处理selector上的每个已选择的SelectionKey
            Set<SelectionKey> sks=selector.selectedKeys();
            Iterator keys = sks.iterator();
            while (keys.hasNext()){
                SelectionKey sk=(SelectionKey)keys.next();
                // 从selector上的已选择Key集中删除正在处理的SelectionKey
                keys.remove();
                // 如果sk对应的通道包含客户端的连接请求
                if (sk.isAcceptable()) {
                    // 调用accept方法接受连接，产生服务器端对应的SocketChannel
                    SocketChannel sc = server.accept();

                    // 设置采用非阻塞模式
                    sc.configureBlocking(false);
                    // 将该SocketChannel也注册到selector
                    sc.register(selector, SelectionKey.OP_READ);

                    //保存连接  校验连接是否合法，合法保留 否则断开
                   //

                }
                // 如果sk对应的通道有数据需要读取
                if (sk.isReadable()) {
                    // 获取该SelectionKey对应的Channel，该Channel中有可读的数据
                    SocketChannel sc = (SocketChannel) sk.channel();
                    // 开始读取数据

                    try {
                        while (sc.read(buff) > 0) {
                            buff.flip();
                            //将缓冲区的数据读出到byte[]
                            byte[] receiveData=dataTool.getBytesFromByteBuffer(buff);
                            String receiveDataHexString=dataTool.bytes2hex(receiveData);
                            _logger.info("Receive date from " + sc.socket().getRemoteSocketAddress() + ">>>:" + receiveDataHexString);
                            if(!dataTool.checkByteArray(receiveData)) {
                                _logger.info(">>>>>bytes data is invalid,we will not save them");
                             }else{
                                byte dataType=dataTool.getApplicationType(receiveData);
                                switch(dataType)
                                {
                                    case 0x13:
                                        _logger.info("Register start...");
                                        HashMap<String,String> vinAndSerialNum=dataTool.getVinDataFromRegBytes(receiveData);
                                        String eventId=vinAndSerialNum.get("eventId");
                                        String vin=vinAndSerialNum.get("vin");
                                        String serialNum=vinAndSerialNum.get("serialNum");
                                        boolean checkVinAndSerNum= dataTool.checkVinAndSerialNum(vin, serialNum);
                                        //如果注册成功记录连接，后续可以通过redis主动发消息，不成功不记录连接
                                        ByteBuffer send=dataTool.getRegResultByteBuffer(receiveData, Integer.valueOf(eventId), checkVinAndSerNum);
                                        //发往客户端的数据，根据验证结果+收到的数据生成
                                        sc.write(send);
                                        if(checkVinAndSerNum){
                                            channels.put(vin, sc);
                                            _logger.info("resister success,contection" + vin + "Save to HashMap");
                                        }else{
                                            _logger.info("resister faild,close contection");
                                            sc.close();
                                        }
                                        break;
                                    case 0x11:
                                        _logger.info("check check");
                                        sc.write(ByteBuffer.wrap("test passed!".getBytes()));//回发数据直接回消息
                                        //不记录连接，只能通过请求-应答方式回消息，无法通过redis主动发消息
                                        break;
                                    case 0x26:
                                        _logger.info("心跳请求");
                                        sc.write(ByteBuffer.wrap("test passed!".getBytes()));//回发数据直接回消息
                                        //不记录连接，只能通过请求-应答方式回消息，无法通过redis主动发消息
                                        break;
                                    default:
                                        _logger.info(">>other request dave,data to redis");
                                        saveBytesToRedis(getKeyByValue(sc), receiveData);
                                        //一般数据，判断是否已注册，注册的数据保存
                                        break;
                                }
                            }
                            /////////////////////////////////
                            if (buff.hasRemaining()) {
                                buff.compact();
                            } else {
                                buff.clear();
                            }
                        }
                        // 打印从该sk对应的Channel里读取到的数据
                    }
                    // 如果捕捉到该sk对应的Channel出现了异常，即表明该Channel
                    // 对应的Client出现了问题，所以从Selector中取消sk的注册
                    catch (IOException ex) {
                        // 从Selector中删除指定的SelectionKey
                        String scKey=getKeyByValue(((SocketChannel) sk.channel()));
                        _logger.info("连接断开:" + scKey);
                        channels.remove(scKey);
                        sk.cancel();
                        if (sk.channel() != null) {
                            sk.channel().close();
                        }
                    }
                }
            }
        }
    }


    public void saveBytesToRedis(String scKey,byte[] bytes){
        //存储接收数据到redis 采用redis Set结构，一个key对应一个Set<String>
        if(dataTool.checkByteArray(bytes)){
        if(scKey!=null){
            String inputKey="input:"+scKey;//保存数据包到redis里面的key，格式input:{vin}
            String receiveDataHexString=dataTool.bytes2hex(bytes);
            socketRedis.saveString(inputKey, receiveDataHexString);
            _logger.info("Save data to Redis:" + inputKey);
        }else{
            _logger.info("未找到scKey,数据包非法，不保存!");
        }
        }
    }


    //根据SocketChannel得到对应的sc在HashMap中的key,为{vin}
    public  String getKeyByValue(SocketChannel sc)
    {
        Iterator<String> it= channels.keySet().iterator();
        while(it.hasNext())
        {
            String keyString=it.next();
            if(channels.get(keyString).equals(sc))
                return keyString;
        }
        return null;
    }




}



