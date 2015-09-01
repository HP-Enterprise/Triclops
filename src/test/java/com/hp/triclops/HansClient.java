package com.hp.triclops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Created by liz on 2015/8/28.
 */
public class HansClient {

    // 定义检测SocketChannel的Selector对象
    private Selector selector = null;
    // 客户端SocketChannel
    private SocketChannel sc = null;
    // 日志
    private Logger _logger;

    public void init() throws IOException {
        this._logger = LoggerFactory.getLogger(HansClient.class);
        selector = Selector.open();
        InetSocketAddress isa = new InetSocketAddress(9000);//可以在第一个参数加上IP
        // 调用open静态方法创建连接到指定主机的SocketChannel
        sc = SocketChannel.open(isa);
        // 设置该sc以非阻塞方式工作
        sc.configureBlocking(false);
        // 将SocketChannel对象注册到指定Selector
        sc.register(selector, SelectionKey.OP_READ);
        // 启动读取服务器端数据的线程
        new ClientThread().start();
        // 创建键盘输入流
        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()) {
            // 读取键盘输入
            String line = scan.nextLine();
            // 将键盘输入的内容输出到SocketChannel中
            sc.write(StandardCharsets.UTF_8.encode(line));
        }
    }

    // 定义读取服务器数据的线程
    private class ClientThread extends Thread {
        public void run() {
            try {
                while (selector.select() > 0) {
                    // 遍历每个有可用IO操作Channel对应的SelectionKey
                    for (SelectionKey sk : selector.selectedKeys()) {
                        // 删除正在处理的SelectionKey
                        selector.selectedKeys().remove(sk);
                        // 如果该SelectionKey对应的Channel中有可读的数据
                        if (sk.isReadable()) {
                            // 使用NIO读取Channel中的数据
                            SocketChannel sc = (SocketChannel) sk.channel();
                            ByteBuffer buff = ByteBuffer.allocate(1024);
                            while (sc.read(buff) > 0) {
                                sc.read(buff);
                                buff.flip();
                                // 打印输出读取的内容
                                System.out.println("读取信息：" + buff);
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        new HansClient().init();
    }
}

