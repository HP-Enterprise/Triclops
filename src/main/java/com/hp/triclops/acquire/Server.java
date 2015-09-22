package com.hp.triclops.acquire;

/**
 * Created by luj on 2015/9/21.
 */
import com.hp.triclops.redis.SocketRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server  extends Thread{

    private int _acquirePort;
    private SocketRedis socketRedis;
    private DataTool dataTool;
    private HashMap<String,SocketChannel> socketChannels;
    private ExecutorService pool;
    private ServerSocketChannel ssc;
    private Selector selector;
    private static Charset charset = Charset.forName("utf-8");
    private int n;
    // 日志
    private Logger _logger;
    private int count=0;


    public Server(HashMap<String,SocketChannel> cs,SocketRedis s,DataTool dt,int port){
        this.socketChannels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
        this._acquirePort=port;
        this._logger = LoggerFactory.getLogger(Server.class);
    }
    public synchronized void run()
    {
        try{
            _logger.info("start listen...");
            listen();
            doService();
        }catch (Exception e){
            _logger.info(e.toString());
        }
    }
    public void listen(){
        try{
            pool = Executors.newFixedThreadPool(20000);
            //线程池的大小设置还需要研究
            ssc = ServerSocketChannel.open();
            // 通过open方法来打开一个未绑定的ServerSocketChannel实例
            ssc.configureBlocking(false);
            ServerSocket ss = ssc.socket();
            ss.bind(new InetSocketAddress(_acquirePort));
            // 将该ServerSocketChannel绑定到指定IP端口
            selector = Selector.open();
            // 将server注册到指定Selector对象
            ssc.register(selector,SelectionKey.OP_ACCEPT);
            _logger.info("Server started...");
        }catch (IOException e){e.printStackTrace();}
    }

    public void doService(){
        while(true){
            try{
                n = selector.select();
            }catch (IOException e) {
                throw new RuntimeException("Selector.select() Exception!");
            }
            if(n==0)
                continue;
            // 依次处理selector上的每个已选择的SelectionKey
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();
                // 从selector上的已选择Key集中删除正在处理的SelectionKey
                iter.remove();
                // 如果sk对应的通道包含客户端的连接请求
                if(key.isAcceptable()){
                    SocketChannel sc = null;
                    try{
                        // 调用accept方法接受连接，产生服务器端对应的SocketChannel
                        sc = ((ServerSocketChannel)key.channel()).accept();
                        // 设置采用非阻塞模式
                        sc.configureBlocking(false);
                        _logger.info("client:" + sc.socket().getRemoteSocketAddress() + " connected");
                        // 将该SocketChannel也注册到selector
                        SelectionKey k = sc.register(selector, SelectionKey.OP_READ);
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        k.attach(buf);
                    }catch (Exception e) {
                        try{
                            sc.close();
                        }catch (Exception ex) {
                        }
                    }
                }
                else if(key.isReadable()){
                    // 如果sk对应的通道有数据需要读取
                    key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                    //新建数据处理线程并加入线程池
                    pool.execute(new Worker(key));
                }
            }
        }
    }

    public  class Worker implements Runnable{
        private SelectionKey key;
        public Worker(SelectionKey key){
            count++;
            this.key = key;
        }

        @Override
        public void run() {
            SocketChannel sc = (SocketChannel)key.channel();
            ByteBuffer buff = (ByteBuffer)key.attachment();
            buff.clear();
            int len = 0;
            try{
                while((len=sc.read(buff))>0){//非阻塞，立刻读取缓冲区可用字节
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
                                    socketChannels.put(vin, sc);
                                    _logger.info("resister success,contection" + vin + "Save to HashMap");
                                }else{
                                    _logger.info("resister faild,close contection");
                                    sc.close();
                                }
                                break;
                            case 0x11:
                                _logger.info("check check!");
                                sc.write(ByteBuffer.wrap("test passed!".getBytes()));//回发数据直接回消息
                                //不记录连接，只能通过请求-应答方式回消息，无法通过redis主动发消息
                                break;
                            case 0x26:
                                _logger.info("Heartbeat request");
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

                if(len==-1){
                    String scKey=getKeyByValue(sc);
                    _logger.info("client Disconnect..." + scKey);
                    socketChannels.remove(scKey);
                    sc.close();
                }
                //没有可用字节,继续监听OP_READ
                key.interestOps(key.interestOps()|SelectionKey.OP_READ);
                key.selector().wakeup();
            }catch (Exception e) {
                try {
                    sc.close();
                } catch (IOException e1) {
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
                _logger.info("can not find the scKey,data is invalid，do not save!");
            }
        }
    }


    //根据SocketChannel得到对应的sc在HashMap中的key,为{vin}
    public  String getKeyByValue(SocketChannel sc)
    {
        Iterator<String> it= socketChannels.keySet().iterator();
        while(it.hasNext())
        {
            String keyString=it.next();
            if(socketChannels.get(keyString).equals(sc))
                return keyString;
        }
        return null;
    }
}
