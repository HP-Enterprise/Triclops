package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.hp.data.bean.tbox.*;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.*;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 生成下行数据包hex 并写入redis
 */
@Service
public class OutputHexService {
    @Autowired
    Conversion conversionTBox;
    @Autowired
    DataTool dataTool;
    @Autowired
    SocketRedis socketRedis;
    @Autowired
    TBoxParmSetRepository tBoxParmSetRepository;
    @Autowired
    RemoteControlRepository remoteControlRepository;
    @Autowired
    UserVehicleRelativedRepository userVehicleRelativedRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    WarningMessageConversionRepository warningMessageConversionRepository;
    @Autowired
    MQService mqService;

    private Logger _logger = LoggerFactory.getLogger(OutputHexService.class);

    public String getRemoteControlPreHex(RemoteControl remoteControl,long eventId){
        //产生远程控制预指令hex
        RemoteControlPreconditionReq remoteControlCmd=new RemoteControlPreconditionReq();
        remoteControlCmd.setApplicationID((short) 49);
        remoteControlCmd.setMessageID((short) 1);
        remoteControlCmd.setEventID((long) eventId);
        remoteControlCmd.setSendingTime((long)dataTool.getCurrentSeconds());
        remoteControlCmd.setTestFlag((short) 0);

        DataPackage dpw=new DataPackage("8995_49_1");//>>>
        dpw.fillBean(remoteControlCmd);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr= PackageEntityManager.getByteString(bbw);

        return byteStr;
    }

    public String getRemoteControlCmdHex(RemoteControl remoteControl,long eventId){
        //产生远程控制指令hex
        RemoteControlCmd  remoteControlCmd=new RemoteControlCmd();
        remoteControlCmd.setRemoteControlType(remoteControl.getControlType().intValue());
        remoteControlCmd.setAcTemperature(remoteControl.getAcTemperature());
        remoteControlCmd.setApplicationID((short) 49);
        remoteControlCmd.setMessageID((short) 3);
        remoteControlCmd.setEventID(eventId);
        remoteControlCmd.setSendingTime((long)dataTool.getCurrentSeconds());
        remoteControlCmd.setTestFlag((short) 0);

        DataPackage dpw=new DataPackage("8995_49_3");//>>>
        dpw.fillBean(remoteControlCmd);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr= PackageEntityManager.getByteString(bbw);

        return byteStr;
    }

    /**
     * TBox注册成功后 检查是否有参数设置未下发 如果有则下发处理
     * @param vin vin
     */
    public void  sendParmSetAfterRegister(String vin){
        //注册通过后下发参数设置
        List<TBoxParmSet> tpss=tBoxParmSetRepository.getLatestOneByVin(vin);
        if(tpss.size()>0){
            TBoxParmSet tps=tpss.get(0);
            String byteString=getParmSetCmdHex(tps);
            _logger.info("ParmSet for TBox:"+vin+" will be send>"+byteString);
            saveCmdToRedis(vin,byteString);
            tps.setStatus((short)1);//参数设置指令向tbox发出 消息状态由0->1
            tBoxParmSetRepository.save(tps);
        }else{
            _logger.info("No ParmSet for TBox:"+vin+" will be send>");
        }

    }
    public String getParmSetCmdHex(TBoxParmSet tps){
        PramSetCmd pramSetCmd=new PramSetCmd();
        pramSetCmd.setApplicationID((short) 82);//>>>
        pramSetCmd.setMessageID((short) 1);//>>>
        pramSetCmd.setEventID(tps.getEventId());
        pramSetCmd.setTestFlag((short) 0);
        pramSetCmd.setSendingTime((long)dataTool.getCurrentSeconds());
        pramSetCmd.setPramSetNumber((short) 13);
        pramSetCmd.setPramSetID(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, (byte) 13});
        pramSetCmd.setFrequencySaveLocalMedia(tps.getFrequencySaveLocalMedia());
        pramSetCmd.setFrequencyForReport(tps.getFrequencyForReport());
        pramSetCmd.setFrequencyForWarningReport(tps.getFrequencyForWarningReport());
        pramSetCmd.setFrequencyHeartbeat(tps.getFrequencyHeartbeat());
        pramSetCmd.setTimeOutForTerminalSearch(tps.getTimeOutForTerminalSearch());
        pramSetCmd.setTimeOutForServerSearch(tps.getTimeOutForServerSearch());
        pramSetCmd.setUploadType(tps.getUploadType());
        pramSetCmd.setEnterpriseBroadcastAddress1(dataTool.getIpBytes(tps.getEnterpriseBroadcastAddress1()));
        pramSetCmd.setEnterpriseBroadcastPort1(tps.getEnterpriseBroadcastPort1());
        pramSetCmd.setEnterpriseBroadcastAddress2(dataTool.getIpBytes(tps.getEnterpriseBroadcastAddress2()));
        pramSetCmd.setEnterpriseBroadcastPort2(tps.getEnterpriseBroadcastPort2());
        pramSetCmd.setEnterpriseDomainNameSize(tps.getEnterpriseDomainNameSize());
        pramSetCmd.setEnterpriseDomainName(tps.getEnterpriseDomainName());

        DataPackage dpw=new DataPackage("8995_82_1");//>>>
        dpw.fillBean(pramSetCmd);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr= PackageEntityManager.getByteString(bbw);

        return byteStr;
    }

    /**
     * 根据报警hex信息生成文本性质的报警提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getWarningMessageAndPush(String vin,String msg){
        String pushMsg=getWarningMessageForPush(vin, msg);
        pushWarningMessage(vin,pushMsg);
    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getResendWarningMessageAndPush(String vin,String msg){
        String pushMsg=getResendWarningMessageForPush(vin, msg);
        pushWarningMessage(vin,pushMsg);
    }


    /**
     * 根报警提示push到对应user
     * @param vin vin
     * @param pushMsg 16进制报警信息
     */
    public void pushWarningMessage(String vin,String pushMsg){
        _logger.info("push message:"+pushMsg);
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid 报警消息都push过去
        if(uvr.size()>0){
            Iterator<UserVehicleRelatived> iterator=uvr.iterator();
            while (iterator.hasNext()){
                int uid=iterator.next().getUid().getId();
                _logger.info("push to:"+uid+":"+pushMsg);
                try {
                    this.mqService.pushToUser(uid, pushMsg);
                }catch (RuntimeException e){e.printStackTrace();}
            }
        }else{
            _logger.info("can not push warning message,because no user found for vin:"+vin);
        }
    }

    /**
     * 根据报警hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制报警信息
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public String getWarningMessageForPush(String vin,String msg){
        //报警数据保存
        _logger.info(">>get WarningMessage For Push:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        WarningMessage bean=dp.loadBean(WarningMessage.class);
        WarningMessageData wd=new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[0] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[1] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[2] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());
        wd.setInfo1((short) (bean.getInfo1().shortValue() & 0xFF));
        wd.setInfo2((short) (bean.getInfo2().shortValue() & 0xFF));
        wd.setInfo3((short) (bean.getInfo3().shortValue() & 0xFF));
        wd.setInfo4((short) (bean.getInfo4().shortValue() & 0xFF));
        wd.setInfo5((short) (bean.getInfo5().shortValue() & 0xFF));
        wd.setInfo6((short) (bean.getInfo6().shortValue() & 0xFF));
        wd.setInfo7((short) (bean.getInfo7().shortValue() & 0xFF));
        wd.setInfo8((short) (bean.getInfo8().shortValue() & 0xFF));

        //生成报警信息
        String warningMessage=buildWarningString(wd);
        return warningMessage;
    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制报警信息
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public String getResendWarningMessageForPush(String vin,String msg){
        //报警数据保存
        _logger.info(">>get Resend WarningMessage For Push:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendWarningMes bean=dp.loadBean(DataResendWarningMes.class);
        WarningMessageData wd=new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[0] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[1] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[2] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());
        wd.setInfo1((short) (bean.getInfo1().shortValue() & 0xFF));
        wd.setInfo2((short) (bean.getInfo2().shortValue() & 0xFF));
        wd.setInfo3((short) (bean.getInfo3().shortValue() & 0xFF));
        wd.setInfo4((short) (bean.getInfo4().shortValue() & 0xFF));
        wd.setInfo5((short) (bean.getInfo5().shortValue() & 0xFF));
        wd.setInfo6((short) (bean.getInfo6().shortValue() & 0xFF));
        wd.setInfo7((short) (bean.getInfo7().shortValue() & 0xFF));
        wd.setInfo8((short) (bean.getInfo8().shortValue() & 0xFF));

        //生成报警信息
        String warningMessage=buildWarningString(wd);
        return warningMessage;
    }

    /**
     * 根据报警消息类生成报警消息
     * @param wd 报警消息实体类
     * @return 便于阅读的报警消息
     */
    public String buildWarningString(WarningMessageData wd){

        StringBuilder sb=new StringBuilder() ;
        sb.append("车辆报警信息: ");
        if(wd.getIsLocation()==(short)0){
            //0有效 1无效
            sb.append("当前位置:");
            sb.append("经度:").append(wd.getLongitude()).append(wd.getEastWest()).append(",");
            sb.append("纬度").append(wd.getLatitude()).append(wd.getNorthSouth()).append(";");
            sb.append("速度:").append(wd.getSpeed()).append("km/h;");
            sb.append("方向:").append(wd.getHeading()).append(";");
        }
        Iterator<WarningMessageConversion> iterator=warningMessageConversionRepository.findAll().iterator();
        HashMap<Short,String> messages=dataTool.messageIteratorToMap(iterator);
        String info1=messages.get(wd.getInfo1());
        if(info1!=null){
            sb.append(info1+";");
        }
        String info2=messages.get(wd.getInfo2());
        if(info2!=null){
            sb.append(info2+";");
        }
        String info3=messages.get(wd.getInfo3());
        if(info3!=null){
            sb.append(info3+";");
        }
        String info4=messages.get(wd.getInfo4());
        if(info4!=null){
            sb.append(info4+";");
        }
        String info5=messages.get(wd.getInfo5());
        if(info5!=null){
            sb.append(info5+";");
        }
        String info6=messages.get(wd.getInfo6());
        if(info6!=null){
            sb.append(info6+";");
        }
        String info7=messages.get(wd.getInfo7());
        if(info7!=null){
            sb.append(info7+";");
        }
        String info8=messages.get(wd.getInfo8());
        if(info8!=null){
            sb.append(info8+";");
        }
        return sb.toString();
    }

    /**
     * 远程控制参数暂存redis
     * @param vin vin
     * @param eventId eventId
     * @param rc 封装远程控制参数的RemoteControl对象
     */
    public  void saveRemoteCmdValueToRedis(String vin,long eventId,RemoteControl rc){
        String valueStr=rc.getControlType()+","+rc.getAcTemperature();//类型和温度值 15,25
        socketRedis.saveValueString(dataTool.remote_cmd_value_preStr +"-"+ vin+"-"+eventId, valueStr, -1);
        //控制参数暂存redis
    }

    /**
     * 从redis取出暂存的远程控制参数
     * @param vin vin
     * @param eventId eventId
     * @return 封装远程控制参数的RemoteControl对象
     */
    public  RemoteControl getRemoteCmdValueFromRedis(String vin,long eventId){
        String cmdValueKey=dataTool.remote_cmd_value_preStr +"-"+ vin+"-"+eventId;
        String valueStr=socketRedis.getValueString(cmdValueKey);
        socketRedis.delValueString(cmdValueKey);
        if(!valueStr.equalsIgnoreCase("null")&&valueStr.length()>0){
            String[] values=valueStr.split(",");
            RemoteControl  rc=new RemoteControl();
            rc.setControlType(Short.parseShort(values[0]));
            rc.setAcTemperature(Short.parseShort(values[1]));
            return rc;
        }
        return null;
        //取出暂存redis控制参数
    }

    /**
     * 处理远程控制Ack上行（持久化 push）
     * @param vin
     * @param eventId
     */
    public void handleRemoteControlPreconditionResp(String vin,long eventId){
        String sessionId=49+"-"+eventId;
        Short dbResult=2;//参考建表sql 1 返回无效 2不符合条件终止 3执行成功 4执行失败
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin,sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId);
        }else{
            //持久化远程控制记录状态，push to sender
            _logger.info("RemoteControl PreconditionResp persistence and push start");
            //返回无效才更新db记录
            rc.setStatus(dbResult);
            remoteControlRepository.save(rc);
            String pushMsg="远程命令不符合发送条件:"+sessionId;
            try{
                this.mqService.pushToUser(rc.getUid(), pushMsg);
            }catch (RuntimeException e){e.printStackTrace();}

            _logger.info("RemoteControl PreconditionResp persistence and push success");
              }
    }

    /**
     * 处理远程控制Ack上行（持久化 push）
     * @param vin
     * @param eventId
     * @param result
     */
    public void handleRemoteControlAck(String vin,long eventId,Short result){
        String sessionId=49+"-"+eventId;
        Short dbResult=(result==(short)0)?(short)1:(short)-1;//参考建表sql 1 返回无效 2不符合条件终止 3执行成功 4执行失败,  Rst 0：无效 1：命令已接收
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin,sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId+"|result:"+result);
        }else{
            //持久化远程控制记录状态，push to sender
           if(dbResult==(short)1){
               _logger.info("RemoteControl Ack persistence and push start");
               //返回无效才更新db记录
               rc.setStatus(dbResult);
               remoteControlRepository.save(rc);
               String pushMsg="远程命令无效:"+sessionId;
               try{
               this.mqService.pushToUser(rc.getUid(), pushMsg);
               }catch (RuntimeException e){e.printStackTrace();}
               _logger.info("RemoteControl Ack persistence and push success");
           }

        }
    }

    /**
     * 处理远程控制结果上行（持久化 push）
     * @param vin
     * @param eventId
     * @param result
     */
    public void handleRemoteControlRst(String vin,long eventId,Short result){
        String sessionId=49+"-"+eventId;
        Short dbResult=(short)(result+(short)3);//参考建表sql  1 返回无效 2不符合条件终止 3执行成功 4执行失败',  Rst 0：成功 1：失败
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin,sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId+"|result:"+result);
        }else{
            //持久化远程控制记录状态，push to sender
            _logger.info("RemoteControl Rst persistence and push start");
            rc.setStatus(dbResult);
            remoteControlRepository.save(rc);
            String pushMsg=(result==(short)0)?"远程命令执行成功:":"远程命令执行失败:";
            pushMsg=pushMsg+sessionId;
            try{
            this.mqService.pushToUser(rc.getUid(), pushMsg);
            }catch (RuntimeException e){e.printStackTrace();}
            _logger.info("RemoteControl Rst persistence and push success");
        }
    }

    public  void saveCmdToRedis(String vin,String hexStr){
        socketRedis.saveSetString(dataTool.out_cmd_preStr + vin, hexStr, -1);
        //保存到redis


    }

}
