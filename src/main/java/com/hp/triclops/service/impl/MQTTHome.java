package com.hp.triclops.service.impl;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

/**
 * MQTT业务类
 */
@Service
public class MQTTHome {
    private String queueName="";
    private  String host="";
    private int port=1000;
    private String userName="";
    private String password="";
    private Channel channel=null;


    private void init()throws IOException, TimeoutException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(userName);
        factory.setPassword(password);
        //getting a connection
        Connection connection = factory.newConnection();
        //creating a channel
        channel = connection.createChannel();
        //declaring a queue for this channel. If queue does not exist,
        //it will be created on the server.
        channel.queueDeclare(queueName, false, false, false, null);
    }
    public void sendMessage(Serializable object) throws IOException, TimeoutException {
        if (channel==null){
            init();
        }
        byte[] bytes= SerializationUtils.serialize(object);
        channel.basicPublish("",queueName, null, bytes);
    }

}
