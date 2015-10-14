package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.hp.data.bean.tbox.PramSetCmd;
import com.hp.data.bean.tbox.RemoteControlCmd;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.entity.TBoxParmSet;
import com.hp.triclops.redis.SocketRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

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

    public String getRemoteControlHex(RemoteControl remoteControl,int eventId){
        //产生远程控制指令hex
        RemoteControlCmd  remoteControlCmd=new RemoteControlCmd();
        remoteControlCmd.setRemoteControlType(remoteControl.getControlType().intValue());
        remoteControlCmd.setAcTemperature(remoteControl.getAcTemperature());
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

    public String getParmSetCmdHex(TBoxParmSet tps){
        PramSetCmd pramSetCmd=new PramSetCmd();
        pramSetCmd.setApplicationID((short) 82);//>>>
        pramSetCmd.setMessageID((short) 1);//>>>
        pramSetCmd.setEventID(tps.getEventId());
        pramSetCmd.setTestFlag((short) 0);
        pramSetCmd.setSendingTime((long)dataTool.getCurrentSeconds());
        pramSetCmd.setPramSetNumber((short) 13);
        pramSetCmd.setPramSetID(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, (byte) 13});
        pramSetCmd.setFrequencySaveLocalMedia(1000);
        pramSetCmd.setFrequencyForReport(1000);
        pramSetCmd.setFrequencyForWarningReport(1000);
        pramSetCmd.setFrequencyHeartbeat((short) 10);
        pramSetCmd.setTimeOutForTerminalSearch(10);
        pramSetCmd.setTimeOutForServerSearch(10);
        pramSetCmd.setUploadType((short) 1);
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

    public  void saveCmdToRedis(String vin,String hexStr){
        socketRedis.saveSetString(dataTool.out_cmd_preStr + vin, hexStr, -1);
        //保存到redis


    }

}
