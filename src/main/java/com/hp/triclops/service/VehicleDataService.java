package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.hp.triclops.acquire.AcquirePort;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.*;
import com.hp.triclops.repository.*;
import com.hp.triclops.utils.GpsTool;
import com.hp.triclops.vo.RealTimeDataShow;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 车辆控制相关业务代码
 */
@Service
public class VehicleDataService {
    @Autowired
    RemoteControlRepository remoteControlRepository;
    @Autowired
    OutputHexService outputHexService;
    @Autowired
    TBoxParmSetRepository tBoxParmSetRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    DataTool dataTool;
    @Autowired
    DiagnosticDataRepository diagnosticDataRepository;
    @Autowired
    RealTimeReportDataRespository realTimeReportDataRespository;
    @Autowired
    GpsDataRepository gpsDataRepository;
    @Autowired
    GpsTool gpsTool;
    private Logger _logger = LoggerFactory.getLogger(VehicleDataService.class);


    /**
     * 下发参数设置命令
     * @param uid user id
     * @param vin vin
     * @param cType 控制类别
     * @param acTmp 空调温度
     * @return 持久化后的RemoteControl对象
     */
    public RemoteControl handleRemoteControl(int uid,String vin,short cType,short acTmp){
         //先检测是否有连接，如果没有连接。需要先执行唤醒，通知TBOX发起连接
        if(!hasConnection(vin)){
            _logger.info("vin:"+vin+" have not connection,do wake up...");
            remoteWakeUp(vin);
        }
        //唤醒可能成功也可能失败，只有连接建立才可以发送指令
        if(hasConnection(vin)){
            _logger.info("vin:"+vin+" have connection,sending command...");
            Long eventId=(long)dataTool.getCurrentSeconds();
            RemoteControl rc=new RemoteControl();
            rc.setUid(uid);
            rc.setSessionId(49+"-"+eventId);//根据application和eventid生成的session_id
            rc.setVin(vin);
            rc.setSendingTime(new Date());
            rc.setControlType(cType);
            rc.setAcTemperature(acTmp);
            rc.setStatus((short) 0);
            rc.setRemark("");
            remoteControlRepository.save(rc);
            _logger.info("save RemoteControl to db");
            //保存到数据库
            //产生预控制数据包hex并入redis发送，将远程控制参数暂存redis
            //remoteCommand:vin:eventId->type,acTmp
            outputHexService.saveRemoteCmdValueToRedis(vin,eventId,rc);
            String byteStr=outputHexService.getRemoteControlPreHex(rc,eventId);
            outputHexService.saveCmdToRedis(vin,byteStr);//发送预命令
            _logger.info("pre command hex:"+byteStr);
            return rc;
        }
        return null;
        //命令下发成功，返回保存后的rc  否则返回null
    }

    /**
     * 批量参数设置下发
     * @param tBoxParmSet 参数设置
     * @return 记录数
     */
    public int handleParmSetToAllVehicle(TBoxParmSet tBoxParmSet){
        int count=0;
        return count;
    }
    public TBoxParmSet handleParmSet(TBoxParmSet tBoxParmSet){
        tBoxParmSet.setStatus((short)0);//初始标识
        tBoxParmSet.setFrequencySaveLocalMediaResult((short)1);//标识单条参数结果默认值 默认为未成功 等待响应数据来标识
        tBoxParmSet.setFrequencyForReportResult((short)1);
        tBoxParmSet.setFrequencyForWarningReportResult((short)1);
        tBoxParmSet.setFrequencyHeartbeatResult((short)1);
        tBoxParmSet.setTimeOutForTerminalSearchResult((short)1);
        tBoxParmSet.setTimeOutForServerSearchResult((short)1);
        tBoxParmSet.setUploadTypeResult((short)1);
        tBoxParmSet.setEnterpriseBroadcastAddress1Result((short)1);
        tBoxParmSet.setEnterpriseBroadcastPort1Result((short)1);
        tBoxParmSet.setEnterpriseBroadcastAddress2Result((short)1);
        tBoxParmSet.setEnterpriseBroadcastPort2Result((short)1);
        tBoxParmSet.setEnterpriseDomainNameSizeResult((short)1);
        tBoxParmSet.setEnterpriseDomainNameResult((short)1);
        tBoxParmSetRepository.save(tBoxParmSet);
        //参数数据保存到数据库表 如果TBox在线，通过连接下发；如果不在线，保存到数据库，等待注册后进行下发。
        if(hasConnection(tBoxParmSet.getVin())){
            String byteStr=outputHexService.getParmSetCmdHex(tBoxParmSet);
            //生成output数据包并进入redis
            outputHexService.saveCmdToRedis(tBoxParmSet.getVin(),byteStr);
            tBoxParmSet.setStatus((short)1);//参数设置指令向tbox发出 消息状态由0->1
            tBoxParmSetRepository.save(tBoxParmSet);
            return tBoxParmSet;
        }
        return null;//TBox不在线 Controller通知出去
    }

    /**
     * 远程车辆诊断相关
     * @param diagnosticData 来自api的诊断指令
     * @return handleDiag或者null
     */
    public DiagnosticData handleDiag(DiagnosticData diagnosticData){
        //参数数据保存到数据库表
        //先检测是否有连接，如果没有连接。需要先执行唤醒，通知TBOX发起连接
        if(!hasConnection(diagnosticData.getVin())){
            _logger.info("vin:"+diagnosticData.getVin()+" have not connection,do wake up...");
            remoteWakeUp(diagnosticData.getVin());
        }
        if(hasConnection(diagnosticData.getVin())){
            diagnosticData.setDiaId((short)0);
            diagnosticData.setHasAck((short) 0);
            diagnosticData.setSendDate(new Date());
            diagnosticDataRepository.save(diagnosticData);

            String byteStr=outputHexService.getDiagCmdHex(diagnosticData);
            //生成output数据包并进入redis
            _logger.info(byteStr);
            outputHexService.saveCmdToRedis(diagnosticData.getVin(), byteStr);
            return diagnosticData;
        }
        return null;//TBox不在线 Controller通知出去
    }


    /**
     * 远程唤醒流程 最多三次 每次唤醒后等待10s检测结果
     * @param vin vin
     */
    public void remoteWakeUp(String vin){
        //远程唤醒动作
        _logger.info("doing wake up......");
        int count=0;
        while (count<3){
            //最多执行唤醒三次
            wakeup(vin);
            count++;
            try{
                Thread.sleep(10*1000);//唤醒后等待10s
            }catch (InterruptedException e){e.printStackTrace(); }
            //检测连接是否已经建立
            if(hasConnection(vin)){
                return;
            }
        }
      }

    /**
     * 调用具体实现的唤醒接口 可能是Ring或者SMS To Tbox
     * @param vin vin
     */
    private void wakeup(String vin){
        //本部分代码为调用外部唤醒接口
        _logger.info(" wake up tbox"+vin);
    }

    /**
     * 检测对应TBox是否有连接可用
     * @param vin vin
     * @return 是否有连接
     */
    private boolean hasConnection(String vin){
        //检测对应vin是否有连接可用
        boolean re=false;
        Channel ch=AcquirePort.channels.get(vin);
        if(ch!=null){
            re=true;
        }
        return re;
    }

    /**
     * 获取车辆实时数据
     * @param vin vin
     * @return 封装数据的vo实体
     */
    public RealTimeDataShow getRealTimeData(String vin){
        RealTimeReportData rd=null;
        GpsData gd=null;
        List<RealTimeReportData> rdList=realTimeReportDataRespository.findLatestOneByVin(vin);
        if(rdList!=null&&rdList.size()>0){
                rd=rdList.get(0);
                List<GpsData> gdList=gpsDataRepository.findByVinAndSendingTime(vin,rd.getSendingTime());
                if(gdList!=null&&gdList.size()>0){
                gd=gdList.get(0);}
        }
        if(rd==null||gd==null){
                return null;
        }else{
                RealTimeDataShow data=new RealTimeDataShow();
                data.setId(rd.getId());
                data.setVin(rd.getVin());
                data.setImei(rd.getImei());
                data.setApplicationId(rd.getApplicationId());
                data.setMessageId(rd.getMessageId());
                data.setSendingTime(rd.getSendingTime());
                data.setFuelOil(rd.getFuelOil());
                data.setAvgOil(rd.getAvgOil());
                data.setOilLife(rd.getOilLife());
                data.setDriveRange(rd.getDriveRange());//
                data.setLeftFrontTirePressure(rd.getLeftFrontTirePressure());
                data.setLeftRearTirePressure(rd.getLeftRearTirePressure());
                data.setRightFrontTirePressure(rd.getRightFrontTirePressure());
                data.setRightRearTirePressure(rd.getRightRearTirePressure());
                data.setLeftFrontWindowInformation(rd.getLeftFrontWindowInformation());
                data.setRightFrontWindowInformation(rd.getRightFrontWindowInformation());
                data.setLeftRearWindowInformation(rd.getLeftRearWindowInformation());
                data.setRightRearWindowInformation(rd.getRightRearWindowInformation());
                data.setVehicleTemperature(rd.getVehicleTemperature());//
                data.setVehicleOuterTemperature(rd.getVehicleOuterTemperature());
                data.setLeftFrontDoorInformation(rd.getLeftFrontDoorInformation());
                data.setLeftRearDoorInformation(rd.getLeftRearDoorInformation());
                data.setRightFrontDoorInformation(rd.getRightFrontDoorInformation());
                data.setRightRearDoorInformation(rd.getRightRearDoorInformation());
                data.setTrunkDoorInformation(rd.getTrunkDoorInformation());
                data.setEngineDoorInformation(rd.getEngineDoorInformation());
                data.setEngineCondition(rd.getEngineCondition());
                data.setEngineSpeed(rd.getEngineSpeed());
                data.setRapidAcceleration(rd.getRapidAcceleration());
                data.setRapidDeceleration(rd.getRapidDeceleration());
                data.setSpeeding(rd.getSpeeding());
                data.setSignalStrength(rd.getSignalStrength());

                data.setIsLocation(gd.getIsLocation());//
                data.setNorthSouth(gd.getNorthSouth());//
                data.setEastWest(gd.getEastWest());//
                data.setLatitude(gd.getLatitude());
                data.setLongitude(gd.getLongitude());
                data.setSpeed(gd.getSpeed());
                data.setHeading(gd.getHeading());
                return data;
        }

        }

    /**
     * 获取车辆实时位置数据
     * @param vin vin
     * @return 封装位置数据的实体
     */
    public GpsData getLatestGpseData(String vin){
        GpsData gd=null;
        List<GpsData> gdList=gpsDataRepository.findLatestByVin(vin);
        if(gdList!=null&&gdList.size()>0){
            gd=gdList.get(0);
            gd=gpsTool.convertToGCJ02(gd);
        }
        return gd;
    }
}
