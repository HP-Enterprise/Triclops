package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import org.slf4j.Logger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by luj on 2015/9/15.
 */
public class Sender extends Thread{


    SocketRedis socketRedis;
    // 日志
    private Logger _logger;

    private Selector selector = null;

    private HashMap<String,SocketChannel> channels;
    public Sender(HashMap<String,SocketChannel> cs,SocketRedis s){
        this.channels=cs;
        this.socketRedis=s;
    }

    public synchronized void run()
    {

                while (true){
                    try{
                        Thread.sleep(2000);
                    }catch (InterruptedException e){
                    }
                    System.out.println("connection count>>:"+channels.keySet().size());
                    //读取数据库中所有的命令集合
                    Set<String> setKey = socketRedis.getKeySet("output:*");
                    if(setKey.size()>0){
                        System.out.println("size:"+setKey.size());
                    }
                    Iterator keys = setKey.iterator();
                    while (keys.hasNext()){
                        //遍历待发数据,处理
                        String k=(String)keys.next();
                        String scKey=k.replace("output:","");
                        SendMesage(scKey,k);
                    }
                }
    }
    public void SendMesage(String scKey,String k){
        try{
            String msg=socketRedis.getString(k);
            System.out.println("send msg:"+msg);
            System.out.println("sckey>>"+scKey);
            SocketChannel sc=channels.get(scKey);
            if(sc!=null){
                System.out.println(sc.toString());
                sc.write(ByteBuffer.wrap(msg.getBytes("GBK")));
                socketRedis.delString(k);
            }else{
                System.out.println("Connection is Dead");
            }
        }catch (IOException e){
            System.out.println(e.toString());
        }
    }
}
