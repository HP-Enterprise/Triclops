package com.hp.triclops.acquire;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by liz on 2015/8/28.
 */
@Component
public class HansServer {
    // ���ڼ������Channel״̬��Selector
    @Value("${com.hp.acquire.port}")
    private int _acquirePort;

    private Selector selector = null;
    public void init() throws IOException {
        selector = Selector.open();
        // ͨ��open��������һ��δ�󶨵�ServerSocketChannelʵ��
        ServerSocketChannel server = ServerSocketChannel.open();
        InetSocketAddress isa = new InetSocketAddress("127.0.0.1", _acquirePort);
        // ����ServerSocketChannel�󶨵�ָ��IP��ַ
        server.socket().bind(isa);
        // ����ServerSocket�Է�������ʽ����
        server.configureBlocking(false);
        // ��serverע�ᵽָ��Selector����
        server.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select() > 0) {
            // ���δ���selector�ϵ�ÿ����ѡ���SelectionKey
            for (SelectionKey sk : selector.selectedKeys()) {
                // ��selector�ϵ���ѡ��Key����ɾ�����ڴ����SelectionKey
                selector.selectedKeys().remove(sk);
                // ���sk��Ӧ��ͨ�������ͻ��˵���������
                if (sk.isAcceptable()) {
                    // ����accept�����������ӣ������������˶�Ӧ��SocketChannel
                    SocketChannel sc = server.accept();
                    // ���ò��÷�����ģʽ
                    sc.configureBlocking(false);
                    // ����SocketChannelҲע�ᵽselector
                    sc.register(selector, SelectionKey.OP_READ);
                }
                // ���sk��Ӧ��ͨ����������Ҫ��ȡ
                if (sk.isReadable()) {
                    // ��ȡ��SelectionKey��Ӧ��Channel����Channel���пɶ�������
                    SocketChannel sc = (SocketChannel) sk.channel();
                    // ����׼��ִ�ж�ȡ���ݵ�ByteBuffer
                    ByteBuffer buff = ByteBuffer.allocate(1024);
                    String content = "";
                    // ��ʼ��ȡ����
                    try {
                        while (sc.read(buff) > 0) {
                            buff.flip();
                            content += StandardCharsets.UTF_8.decode(buff);
                        }
                        // ��ӡ�Ӹ�sk��Ӧ��Channel���ȡ��������
                        System.out.println("=====" + content);
                    }
                    // �����׽����sk��Ӧ��Channel�������쳣����������Channel
                    // ��Ӧ��Client���������⣬���Դ�Selector��ȡ��sk��ע��
                    catch (IOException ex) {
                        // ��Selector��ɾ��ָ����SelectionKey
                        sk.cancel();
                        if (sk.channel() != null) {
                            sk.channel().close();
                        }
                    }
                    // ���content�ĳ��ȴ���0����������Ϣ��Ϊ��
                    if (content.length() > 0) {
                        // ������selector��ע�������SelectKey
                        for (SelectionKey key : selector.keys()) {
                            // ��ȡ��key��Ӧ��Channel
                            Channel targetChannel = key.channel();
                            // �����channel��SocketChannel����
                            if (targetChannel instanceof SocketChannel) {
                                // ������������д���Channel��
                                SocketChannel dest = (SocketChannel) targetChannel;
                                dest.write(StandardCharsets.UTF_8.encode(content));
                            }
                        }
                    }
                }
            }
        }
    }
}
