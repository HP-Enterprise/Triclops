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
                    System.out.println("新的连接来自:"+sc.socket().getRemoteSocketAddress());
                    channels.put("input:12358", sc);
                    //保存连接  校验连接是否合法，合法保留 否则断开
                    // socketRedis.saveSessionOfVal(String.valueOf(new Date().getTime()), sc.toString(), 200);
                    //
                    System.out.println("连接成功保存到HashMap");

                    // 设置采用非阻塞模式
                    sc.configureBlocking(false);
                    // 将该SocketChannel也注册到selector
                    sc.register(selector, SelectionKey.OP_READ);
                }
                // 如果sk对应的通道有数据需要读取
                if (sk.isReadable()) {
                    // 获取该SelectionKey对应的Channel，该Channel中有可读的数据
                    SocketChannel sc = (SocketChannel) sk.channel();
                    // 开始读取数据

                    try {
                        while (sc.read(buff) > 0) {
                            buff.flip();
                            System.out.println("收到来自" + sc.socket().getRemoteSocketAddress() + "的数据包:" + buff);
                            byte[] data = buff.array();
                            //保存数据包到redis
                            String key=getKeyByValue(sc);
                            if(key!=null){
                                socketRedis.saveObject(key, data, 300);
                                System.out.println("数据成功保存到Redis!"+key);
                                byte[] aaa=(byte[])socketRedis.getObject(key);
                                System.out.println(key+"Redis数据读取:"+ aaa.length);
                            }else{
                                System.out.println("未找到key,数据包非法，不保存!");
                            }

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
                        sk.cancel();
                        if (sk.channel() != null) {
                            sk.channel().close();
                        }
                    }
                }
            }
        }
    }
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



