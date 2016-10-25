package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.OutputHexService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luj on 2015/12/4.
 */
public class RequestTask  implements Runnable{
    private String receiveDataHexString;
    private SocketRedis socketRedis;
    private RequestHandler requestHandler;
    private DataTool dataTool;
    private ConcurrentHashMap<String,Channel> channels;
    private ConcurrentHashMap<String,String> connections;
    private Channel ch;
    private OutputHexService outputHexService;
    private Logger _logger;
    private int maxDistance;

    public RequestTask(ConcurrentHashMap<String, Channel> cs,ConcurrentHashMap<String,String> connections,int maxDistance,Channel ch,SocketRedis s,DataTool dt,RequestHandler rh,OutputHexService ohs,String receiveDataHexString){
        super();
        this.channels=cs;
        this.connections=connections;
        this.maxDistance=maxDistance;
        this.ch=ch;
        this.socketRedis=s;
        this.dataTool=dt;
        this.requestHandler=rh;
        this.outputHexService=ohs;
        this.receiveDataHexString = receiveDataHexString;
        this._logger = LoggerFactory.getLogger(RequestTask.class);
    }


    @Override
    public void run() {
        //handle request
        //_logger.info("handle request:" + receiveDataHexString);
        byte[] receiveData=dataTool.getBytesFromByteBuffer(dataTool.getByteBuffer(receiveDataHexString));
        byte dataType=dataTool.getApplicationType(receiveData);
        String chKey;
        String respStr;
        ByteBuf buf;
        switch(dataType)
        {
            case 0x11://电检
                _logger.info("[0x11]收到电检请求...");

                respStr=requestHandler.getDiagResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                break;

            case 0x12://激活
                _logger.info("[0x12]收到激活请求...");
                respStr=requestHandler.getActiveHandle(receiveDataHexString);
                if(respStr!=null){
                    //如果是激活请求，会有messageId=2的响应，如果是激活结果 没有响应
                    buf=dataTool.getByteBuf(respStr);
                    ch.writeAndFlush(buf);//回发数据直接回消息
                }

                break;
            case 0x13://注册
                _logger.info("[0x13]收到注册请求...");
                HashMap<String,String> vinAndSerialNum=dataTool.getVinDataFromRegBytes(receiveData);
                String eventId=vinAndSerialNum.get("eventId");
                String vin=vinAndSerialNum.get("vin");
                String serialNum=vinAndSerialNum.get("serialNum");
                boolean checkVinAndSerNum= dataTool.checkVinAndSerialNum(vin, serialNum);
                //发往客户端的注册结果数据，根据验证结果+收到的数据生成
                respStr=requestHandler.getRegisterResp(receiveDataHexString, vin,checkVinAndSerNum);
                buf=dataTool.getByteBuf(respStr);
                //如果注册成功记录连接，后续可以通过redis主动发消息，不成功不记录连接
                if(checkVinAndSerNum){
                    channels.put(vin, ch);
                    connections.put(ch.remoteAddress().toString(),vin);
                    socketRedis.saveHashString(dataTool.connection_hashmap_name, vin, ch.remoteAddress().toString(), -1);//连接名称保存到redis
                    ch.writeAndFlush(buf);//回发数据直接回消息,此处2016.1.15修改，客户端发起数据之前确定已有连接注册记录
                    _logger.info("[0x13]注册成功，保存连接:" + vin + "" + ch.remoteAddress());
                    _logger.info("[0x13]连接信息Redis:"+socketRedis.listHashKeys(dataTool.connection_hashmap_name));
                    _logger.info("[0x13]连接map:"+channels.entrySet());
                    afterRegisterSuccess(vin);
                }else{
                    ch.writeAndFlush(buf);//回发数据直接回消息
                    _logger.info("[0x13]注册失败，断开连接。");
                    ch.close();//关闭连接
                }
                break;

            case 0x14://远程唤醒
                _logger.info("[0x14]收到远程唤醒请求...");
                //通过byte[] receiveData获取 eventIdWake,vinWake,serialNumWake的map集合
                HashMap<String,String> vinAndSerialNumWakeUp=dataTool.getVinDataFromRegBytes(receiveData);
                String eventIdWake=vinAndSerialNumWakeUp.get("eventId");
                String vinWake=vinAndSerialNumWakeUp.get("vin");
                String serialNumWake=vinAndSerialNumWakeUp.get("serialNum");

                //通过vin和tboxsn验证t_vehicle表中是否存在
                boolean checkVinAndSerNumWake= dataTool.checkVinAndSerialNum(vinWake, serialNumWake);
                respStr=requestHandler.getRemoteWakeUpResp(receiveDataHexString,vinWake,checkVinAndSerNumWake);

                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                //如果远程唤醒成功连接，后续可以通过redis主动发消息，不成功不记录连接
                if(checkVinAndSerNumWake){
                    channels.put(vinWake, ch);
                    connections.put(ch.remoteAddress().toString(),vinWake);
                    socketRedis.saveHashString(dataTool.connection_hashmap_name, vinWake, ch.remoteAddress().toString(), -1);//连接名称保存到redis
                    _logger.info("[0x14]唤醒成功，保存连接" + vinWake + "" + ch.remoteAddress());
                    _logger.info("[0x14]连接信息Redis:"+socketRedis.listHashKeys(dataTool.connection_hashmap_name));
                    _logger.info("[0x14]连接信息HashMap:"+channels.entrySet());
                    afterRegisterSuccess(vinWake);
                }else{
                    _logger.info("[0x14]唤醒失败，断开连接");
                    ch.close();//关闭连接
                }
                break;
            case 0x15://流量查询
                _logger.info("[0x15]收到流量查询请求...");
                respStr=requestHandler.getFlowResp(receiveDataHexString);
                if(respStr!=null){
                    buf=dataTool.getByteBuf(respStr);
                    ch.writeAndFlush(buf);//回发数据直接回消息
                }

                break;
            case 0x21://固定数据上报
                _logger.info("[0x21]收到固定数据上报...");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x21]报文对应的连接没有注册，不处理报文");
                    return;
                }
                saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                break;
            case 0x22://实时数据上报
                _logger.info("[0x22]收到实时数据上报...");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x22] "+ch.remoteAddress().toString()+" 报文对应的连接没有注册，不处理报文");
                    return;
                }
                respStr=requestHandler.getRealTimeDataResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                break;
            case 0x23://补发实时数据上报
                _logger.info("[0x23]收到补发实时数据上报...");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x23]报文对应的连接没有注册，不处理报文");
                    return;
                }
                respStr=requestHandler.getResendRealTimeDataResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                break;
            case 0x24://报警数据上报
                _logger.info("[0x24]收到报警数据上报");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x24]报文对应的连接没有注册，不处理报文");
                    return;
                }
                respStr=requestHandler.getWarningMessageResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                saveSpecialBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                break;
            case 0x25://补发报警数据上报
                _logger.info("[0x25]收到补发报警数据上报");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x25]报文对应的连接没有注册，不处理报文");
                    return;
                }
                respStr=requestHandler.getDataResendWarningDataResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                saveSpecialBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                //补发报警数据是否需要push
                break;
            case 0x26://心跳
                _logger.info("[0x26]收到心跳请求");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x26]报文对应的连接没有注册，不处理报文");
                    return;
                }
                respStr=requestHandler.getHeartbeatResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                break;
            case 0x27://休眠请求
                _logger.info("[0x27]收到休眠请求");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x27报文对应的连接没有注册，不处理报文");
                    return;
                }
                respStr=requestHandler.getSleepResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                _logger.info("[0x27]已发休眠响应报文，即将断开连接");
                ch.close();//关闭连接
                break;
            case 0x28://故障数据上报
                _logger.info("[0x28]收到故障数据上报");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x28]报文对应的连接没有注册，不处理报文");
                    return;
                }
                respStr=requestHandler.getFailureDataResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                saveSpecialBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                break;
            case 0x29://补发故障数据上报
                _logger.info("[0x29]收到补发故障数据上报");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x29]报文对应的连接没有注册，不处理报文");
                    return;
                }
                respStr=requestHandler.getResendFailureDataResp(receiveDataHexString);
                buf=dataTool.getByteBuf(respStr);
                ch.writeAndFlush(buf);//回发数据直接回消息
                saveSpecialBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                //补发故障数据是否需要push
                break;
            case 0x31://远程控制响应(上行)包含mid 2 4 5
                _logger.info("[0x31]收到远程控制响应");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x31]报文对应的连接没有注册，不处理报文");
                    return;
                }
                String _vin=chKey;
                requestHandler.handleRemoteControlRequest(receiveDataHexString, _vin, maxDistance);
                //远程控制上行处理，无数据下行
                break;
            case 0x32://远程控制设置响应(上行)包含mid 2
                _logger.info("[0x32]收到远程控制设置响应");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("[0x32]报文对应的连接没有注册，不处理报文");
                    return;
                }
                 _vin=chKey;
                requestHandler.handleRemoteControlSettingRequest(receiveDataHexString, _vin);
                //远程控制上行处理，无数据下行
                break;
            case 0x41://参数查询响应(上行)
                _logger.info("ParamStatus Ack");
                saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                break;
            case 0x42://远程车辆诊断响应(上行)
                _logger.info("DiagnosticCommand Ack");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("报文对应的连接没有注册，不处理报文");
                    return;
                }
                _vin=chKey;
                requestHandler.handleDiagnosticAck(receiveDataHexString, _vin);
                break;
            case 0x51://上报数据设置响应(上行)
                _logger.info("SignalSetting Ack");
                saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                break;
            case 0x52://参数设置响应(上行)
                _logger.info("PramSetupAck Ack");
                chKey=geVinByAddress(ch.remoteAddress().toString());
                if(chKey==null){
                    _logger.info("报文对应的连接没有注册，不处理报文");
                    return;
                }
                _vin=chKey;
                requestHandler.handleParmSetAck(receiveDataHexString, _vin);
                break;
            case 0x61://解密失败报告(上行)
                _logger.info("[0x61]收到解密失败报告，即将断开连接...");
                ch.close();
                break;
            default:
                _logger.info("未知类型的数据，记录到日志:" + receiveDataHexString);
                //一般数据，判断是否已注册，注册的数据保存
                saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                break;
    }
    }
    public void saveBytesToRedis(String scKey,byte[] bytes){
        //存储接收数据到redis 采用redis Set结构，一个key对应一个Set<String>
        if(dataTool.checkByteArray(bytes)){
            if(scKey!=null){
                String inputKey="input"+dataTool.getRandomRealTimeDataSuffix()+":"+scKey;//保存数据包到redis里面的key，格式input:{vin}
                String receiveDataHexString=dataTool.bytes2hex(bytes);
                socketRedis.saveSetString(inputKey, receiveDataHexString,-1);
                _logger.info("保存数据到Redis:" + inputKey);
            }else{
                _logger.info("未能找到对应的vin，无法保存数据!");
            }
        }
    }
    public void saveSpecialBytesToRedis(String scKey,byte[] bytes){
        //存储接收数据到redis 采用redis Set结构，一个key对应一个Set<String>
        if(dataTool.checkByteArray(bytes)){
            if(scKey!=null){
                String inputKey="input"+dataTool.getWarningDataSuffix()+":"+scKey;//保存数据包到redis里面的key，格式input:{vin}
                String receiveDataHexString=dataTool.bytes2hex(bytes);
                socketRedis.saveSetString(inputKey, receiveDataHexString,-1);
                _logger.info("保存数据到Redis:" + inputKey);
            }else{
                _logger.info("未能找到对应的vin，无法保存数据!");
            }
        }
    }


    //根据SocketChannel得到对应的vin
    public  String geVinByAddress(String remoteAddress)
    {
        String vin=connections.get(remoteAddress);
        return vin;
    }
    //根据SocketChannel得到对应的sc在HashMap中的key,为{vin}
    public  String getKeyByValue_(Channel sc)
    {
        try{
            Iterator<String> it= channels.keySet().iterator();
            while(it.hasNext())
            {
                String keyString=it.next();
                if(channels.get(keyString).equals(sc))
                    return keyString;
            }
            _logger.info("can not find the scKey,sc:"+sc.remoteAddress());
            return null;
        }catch (Exception e){
            e.printStackTrace();
            _logger.info("getKeyByValue exception:"+e.getLocalizedMessage());
        }

        return null;
    }
    private void afterRegisterSuccess(String vin){
        //注册成功并返回数据包后的流程
        outputHexService.sendParmSetAfterRegister(vin);
    }
}
