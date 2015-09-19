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

    DataTool dataTool;

    private HashMap<String,SocketChannel> channels;
    public Sender(HashMap<String,SocketChannel> cs,SocketRedis s,DataTool dt){
        this.channels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
    }

    public synchronized void run()
    {

                while (true){
                    try{
                        Thread.sleep(2000);
                    }catch (InterruptedException e){e.printStackTrace(); }
                    System.out.println("connection count>>:"+channels.keySet().size());
                    //读取数据库中所有的命令集合
                    Set<String> setKey = socketRedis.getKeysSet("output:*");
                    if(setKey.size()>0){ System.out.println("size:"+setKey.size()); }
                    Iterator keys = setKey.iterator();
                    while (keys.hasNext()){
                        //遍历待发数据,处理
                        String k=(String)keys.next();
                        String scKey=k.replace("output:", "");
                        SendMesage(scKey,k);
                    }
                }
    }
    public void SendMesage(String scKey,String k){
        //将output:{vin}对应的十六进制字符串发送给客户端
        try{
                String msg =socketRedis.popOneString(k);
                System.out.println("send msg:"+msg);
                System.out.println("sckey>>"+scKey);
                SocketChannel sc=channels.get(scKey);
                if(sc!=null){
                    System.out.println(sc.toString());
                    sc.write(dataTool.getByteBuffer(msg));
                    socketRedis.delString(k);
                }else{
                    System.out.println("Connection is Dead");
                    socketRedis.saveString(k, msg);
                }

          }catch (IOException e){
            System.out.println(e.toString());
        }
    }
}


//Set<String> all=socketRedis.getSmembers("nameAAA");
//Iterator ir=all.iterator();
//while (ir.hasNext())
//        System.out.println(ir.next());