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

    private int _acquirePort;


    SocketRedis socketRedis;
    // 日志
    private Logger _logger;

    private Selector selector = null;

    private HashMap<String,SocketChannel> channels;
    public Receiver(HashMap<String,SocketChannel> cs,SocketRedis s,int port){
        this.channels=cs;
        this.socketRedis=s;
        this._acquirePort=port;
    }

    public synchronized void run()
    {
        try{
            System.out.println("start listen...");
            listen();
        }catch (Exception e){
            System.out.println(e.toString());
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
                    System.out.println("新的连接来自:" + sc.socket().getRemoteSocketAddress());


                    String vin=String.valueOf(new Date().getTime());
                    channels.put(vin, sc);
                    //保存连接  校验连接是否合法，合法保留 否则断开
                    System.out.println("连接" + vin + "成功保存到HashMap");

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
                            byte[] receiveData=getBytesFromByteBuffer(buff);
                            System.out.println("Receive date from " + sc.socket().getRemoteSocketAddress() + ">>>:" + new String(receiveData));
                            if(!DataTool.checkByteArray(receiveData)) {
                                System.out.println(">>>>>bytes data is invalid,we will not save them");
                             }else{
                                byte dataType=DataTool.getApplicationType(receiveData);
                                System.out.println("标识位"+Integer.toHexString(dataType));
                                switch(dataType)
                                {
                                    case 0x13:
                                        System.out.println("注册");
                                        sc.write(ByteBuffer.wrap("register success!".getBytes()));//回发数据
                                        //如果注册成功记录连接，后续可以通过redis主动发消息，不成功不记录连接
                                        break;
                                    case 0x11:
                                        System.out.println("电检");
                                        sc.write(ByteBuffer.wrap("test passed!".getBytes()));//回发数据直接回消息
                                        //不记录连接，只能通过请求-应答方式回消息，无法通过redis主动发消息
                                        break;
                                    default:
                                        System.out.println(">>其他操作,保存数据至redis");
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
                        System.out.println("连接断开:"+scKey);
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


    public byte[] getBytesFromByteBuffer(ByteBuffer buff){
        byte[] result = new byte[buff.remaining()];
        if (buff.remaining() > 0) {
            buff.get(result, 0, buff.remaining());
        }
        return result;
    }

    public void saveBytesToRedis(String scKey,byte[] bytes){
        if(DataTool.checkByteArray(bytes)){
        if(scKey!=null){
            String inputKey="input:"+scKey;//保存数据包到redis里面的key，格式input:{vin}
            socketRedis.saveObject(inputKey, bytes, 300);
            socketRedis.updateObject(inputKey, bytes);
            System.out.println("Save data to Redis:"+inputKey);
            byte[] aaa=(byte[])socketRedis.getObject(inputKey);
            System.out.println(inputKey+" Read from Redis:"+ new String(aaa));
        }else{
            System.out.println("未找到scKey,数据包非法，不保存!");
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



