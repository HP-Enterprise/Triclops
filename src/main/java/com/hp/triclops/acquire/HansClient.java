package com.hp.triclops.acquire;

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

    // ������SocketChannel��Selector����
    private Selector selector = null;
    // �ͻ���SocketChannel
    private SocketChannel sc = null;

    public void init() throws IOException {
        selector = Selector.open();
        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 9000);
        // ����open��̬�����������ӵ�ָ��������SocketChannel
        sc = SocketChannel.open(isa);
        // ���ø�sc�Է�������ʽ����
        sc.configureBlocking(false);
        // ��SocketChannel����ע�ᵽָ��Selector
        sc.register(selector, SelectionKey.OP_READ);
        // ������ȡ�����������ݵ��߳�
        new ClientThread().start();
        // ��������������
        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()) {
            // ��ȡ��������
            String line = scan.nextLine();
            // ��������������������SocketChannel��
            sc.write(StandardCharsets.UTF_8.encode(line));
        }
    }

    // �����ȡ���������ݵ��߳�
    private class ClientThread extends Thread {
        public void run() {
            try {
                while (selector.select() > 0) {
                    // ����ÿ���п���IO����Channel��Ӧ��SelectionKey
                    for (SelectionKey sk : selector.selectedKeys()) {
                        // ɾ�����ڴ����SelectionKey
                        selector.selectedKeys().remove(sk);
                        // �����SelectionKey��Ӧ��Channel���пɶ�������
                        if (sk.isReadable()) {
                            // ʹ��NIO��ȡChannel�е�����
                            SocketChannel sc = (SocketChannel) sk.channel();
                            ByteBuffer buff = ByteBuffer.allocate(1024);
                            String content = "";
                            while (sc.read(buff) > 0) {
                                sc.read(buff);
                                buff.flip();
                                content += StandardCharsets.UTF_8.decode(buff);
                            }
                            // ��ӡ�����ȡ������
                            System.out.println("������Ϣ��" + content);
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) throws IOException {
//        new HansClient().init();
//    }
}

