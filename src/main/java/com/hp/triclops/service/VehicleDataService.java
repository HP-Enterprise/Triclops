package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.hp.triclops.acquire.AcquirePort;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.*;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.repository.*;
import com.hp.triclops.utils.GpsTool;
import com.hp.triclops.utils.Page;
import com.hp.triclops.utils.SMSHttpTool;
import com.hp.triclops.vo.RealTimeDataShow;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    FailureMessageDataRespository failureMessageDataRespository;
    @Autowired
    WarningMessageConversionRepository warningMessageConversionRepository;
    @Autowired
    GpsDataRepository gpsDataRepository;
    @Autowired
    GpsTool gpsTool;
    private Logger _logger = LoggerFactory.getLogger(VehicleDataService.class);
    @Autowired
    private SocketRedis socketRedis;
    @Autowired
    RemoteControlRespositoryDao remoteControlRespositoryDao;

    @Value("${com.hp.remoteControl.maxCount}")
    private int _maxCount;//远程控制最大次数

    @Value("${com.hp.remoteControl.maxDistance}")
    private int _maxDistance;//远程控制最大距离

    @Autowired
    TBoxRepository tBoxRepository;
    @Autowired
    SMSHttpTool smsHttpTool;

    private int wakeUpWaitSeconds=90;//唤醒等待时间，超过即为唤醒失败
    private int remoteControlTimeOut=70;//远程控制API返回超时秒数（唤醒成功后等待时间）
    private int remoteSettingTimeOut=40;//远程设置超时秒数(唤醒成功后等待时间)

    /**
     * 下发远程控制命令
     * @param uid user id
     * @param vin vin
     * @param remoteControlBody app remoteControlBody
     * @param isRefCommand 是否是依赖的启动发动机命令
     * @return 持久化后的RemoteControl对象
     */
    public RemoteControl handleRemoteControl(int uid,long id,String vin,RemoteControlBody remoteControlBody,boolean isRefCommand){

         //先检测是否有连接，如果没有连接。需要先执行唤醒，通知TBOX发起连接
        //保存远程控制记录
        Long eventId= (long) dataTool.getCurrentSeconds();
        RemoteControl rc=new RemoteControl();
        if(remoteControlBody==null){
            rc=remoteControlRepository.findOne(id);
            eventId=Long.parseLong(rc.getSessionId());
        }else{
            rc.setUid(uid);
            rc.setSessionId(String.valueOf(eventId));//根据application和eventid生成的session_id
            rc.setVin(vin);
            rc.setRefId(remoteControlBody.getRefId());
            rc.setSendingTime(new Date());
            rc.setControlType(remoteControlBody.getcType());
            rc.setAcTemperature(remoteControlBody.getTemp());
            rc.setLightNum(remoteControlBody.getLightNum());
            rc.setHornNum(remoteControlBody.getHornNum());
            rc.setActTime(remoteControlBody.getActTime());
            rc.setDeActive(remoteControlBody.getDeActive());
            rc.setAutoMode(remoteControlBody.getAutoMode());
            rc.setRecirMode(remoteControlBody.getRecirMode());
            rc.setAcMode(remoteControlBody.getAcMode());
            rc.setFan(remoteControlBody.getFan());
            rc.setMode(remoteControlBody.getMode());
            rc.setMasterStat(remoteControlBody.getMasterStat());
            rc.setMasterLevel(remoteControlBody.getMasterLevel());
            rc.setSlaveStat(remoteControlBody.getSlaveStat());
            rc.setSlaveLevel(remoteControlBody.getSlaveLevel());
            rc.setStatus((short) 2);//处理中
            rc.setRemark("命令下发成功，处理中");
            rc.setRemarkEn("sending command");
            rc.setAvailable((short) 1);
            rc.setIsAnnounce((short)0);//默认要求直接做perform，会在后续处理逻辑处理
            if(isRefCommand){
                rc.setAvailable((short) 0);
            }
            remoteControlRepository.save(rc);
            _logger.info("[0x31]保存远程控制记录到数据库，id:"+rc.getId());
        }
        //20160525取消T平台对控制次数的检查
       /* if(!initCheck(vin,rc.getControlType())){
            _logger.info("[0x31]vin:"+vin+" initCheck失败,无法继续远程控制");
            rc.setRemark("车辆初始状态不满足控制条件，无法下发远程控制指令！");
            rc.setRemarkEn("can not send command,because init check failed.");
            rc.setStatus((short)0);
            remoteControlRepository.save(rc);
            return null;
        }*///取消基于历史数据的initCheck，改为precondition判断
        if(!hasConnection(vin)){
            _logger.info("[0x31]vin:"+vin+" 当前不在线，正在唤醒...");
            int wakeUpResult=remoteWakeUp(vin,wakeUpWaitSeconds);
            _logger.info("[0x31]vin:"+vin+" 唤醒结果:"+wakeUpResult+" (参考值1:成功 0:失败)");
            if(wakeUpResult==1) {
                _logger.info("[0x31]vin:"+vin+" 唤醒成功，等待200ms...");
                try {
                    Thread.sleep(200l);
                } catch (InterruptedException e) {

                }
            }
        }
        //唤醒可能成功也可能失败，只有连接建立才可以发送指令
        if(hasConnection(vin)){
            _logger.info("[0x31]vin:"+vin+" 在线,发送Precondition请求...");
            //保存到数据库
            String byteStr=outputHexService.getRemoteControlPreHex(rc,eventId);
            //读取connection val 确定写入的key
            String _val=socketRedis.getHashString(dataTool.connection_hashmap_name,vin);
            String _serverId=_val.split("-")[0];
            outputHexService.saveCmdToRedis(_serverId,vin,byteStr);//发送预命令
            _logger.info("[0x31]Precondition请求hex:"+byteStr);
            return rc;
        }else{
            rc.setRemark("由于网络信号质量差或电瓶电量低，车联网远程控制功能暂时无法使用");
            rc.setRemarkEn("Due to poor network signal quality or low battery power, car networking remote control function temporarily unavailable");
            rc.setStatus((short)0);
            remoteControlRepository.save(rc);
            String key=vin+"-"+eventId;
            String value=String.valueOf(rc.getId());
            socketRedis.saveHashString(dataTool.remoteControl_hashmap_name, key, value, dataTool.remoteControlResult_ttl);
        }
        return rc;
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
        Long eventId=(long)dataTool.getCurrentSeconds();
        tBoxParmSet.setEventId(eventId);
        tBoxParmSet.setSendingTime(new Date());
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
            String _val=socketRedis.getHashString(dataTool.connection_hashmap_name,tBoxParmSet.getVin());
            String _serverId=_val.split("-")[0];
            outputHexService.saveCmdToRedis(_serverId,tBoxParmSet.getVin(),byteStr);
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
            _logger.info("[0x14]vin:"+diagnosticData.getVin()+" 当前不在线，正在唤醒...");
            int wakeUpResult=remoteWakeUp(diagnosticData.getVin(),wakeUpWaitSeconds);
            _logger.info("[0x14]vin:"+diagnosticData.getVin()+" 唤醒结果:"+wakeUpResult+" (参考值1:成功 0:失败)");

        }
        if(hasConnection(diagnosticData.getVin())){
            diagnosticData.setHasAck((short) 0);
            diagnosticData.setSendDate(new Date());
            diagnosticDataRepository.save(diagnosticData);

            String byteStr=outputHexService.getDiagCmdHex(diagnosticData);
            //生成output数据包并进入redis
            _logger.info(byteStr);
            String _val=socketRedis.getHashString(dataTool.connection_hashmap_name,diagnosticData.getVin());
            String _serverId=_val.split("-")[0];
            outputHexService.saveCmdToRedis(_serverId,diagnosticData.getVin(), byteStr);
            return diagnosticData;
        }
        return null;//TBox不在线 Controller通知出去
    }

    /**
     * 检测redis是否有结果数据
     * @param hashName
     * @param key
     * @param checkCount
     * @return 1有结果 0无结果
     */
    public int checkResultFromRedis(String hashName,String key,int checkCount){
        //远程唤醒动作
        _logger.info("[0x31][0x32]从Redis检查 " + key + "是否有结果返回......(超时时间" + checkCount + "s)");
        int count=0;
        while (count<checkCount){
            //发送一次短信，然后间隔1s检测是否产生结果信息是否建立
            count++;
            try{
                Thread.sleep(1*1000);//唤醒后等待1s循环检测
            }catch (InterruptedException e){e.printStackTrace(); }
            //检测连接是否已经建立
            if(socketRedis.existHashString(hashName, key)){
                return 1;
            }
        }
        return 0;
    }

    /**
     * 远程唤醒流程 最多三次 每次唤醒后等待10s检测结果
     * @param vin vin
     */
    public int remoteWakeUp(String vin,int checkCount){
        //远程唤醒动作
        _logger.info("[0x31]正在唤醒......(超时时间"+checkCount+"s)");
        wakeup(vin);
        int count=0;
        while (count<checkCount){
            //发送一次短信，然后间隔1s检测连接是否建立
            count++;
            if(count==40){
                wakeup(vin);//40秒之后后台再retry一次
            }
            try{
                Thread.sleep(1*1000);//唤醒后等待1s循环检测
            }catch (InterruptedException e){e.printStackTrace(); }
            //检测连接是否已经建立
            if(hasConnection(vin)){
                return 1;
            }
        }
        return 0;
      }

    /**
     * 调用具体实现的唤醒接口 可能是Ring或者SMS To Tbox
     * @param vin vin
     */
    public void wakeup(String vin){
        //本部分代码为调用外部唤醒接口
        _logger.info("[0x31]准备向vin:"+vin+"的T-Box手机号发送短信");
        TBox tBox=tBoxRepository.findByVin(vin);
        if(tBox!=null){
            String tboxMobile=tBox.getMobile();
            smsHttpTool.doHttp(tboxMobile,"WAKEUP");
        }else{
            _logger.info("[0x31]无法找到vin:"+vin+"对应T-Box的手机号");
        }
    }

    /**
     * 检测对应TBox是否有连接可用
     * @param vin vin
     * @return 是否有连接
     */
    private boolean hasConnection(String vin){
        //检测对应vin是否有连接可用
        boolean re=false;
        _logger.info("[0x31]从redis检查vin:"+vin+"是否在线"+socketRedis.listHashKeys(dataTool.connection_hashmap_name));
        re=socketRedis.existHashString(dataTool.connection_hashmap_name,vin);
        return re;
    }

    /**
     * initCheck
     * @param vin vin
     * @return 校验
     */
    private boolean initCheck(String vin,short cType){
        //initCheck
        if(cType!=(short)0){//只有远程启动发动机才需要做initCheck
            return true;
        }
        boolean re=false;
        boolean isM8X=true;

        boolean vehicleSpeedCheck=false;
        boolean sunroofCheck=false;
        boolean windowsCheck=false;
        boolean doorsCheck=false;
        boolean trunkCheck=false;
        boolean bonnetCheck=false;
        RealTimeReportData realData=null;
        GpsData gpsData=null;

        //根据vin获取车型，根据是否是F60决定initCheck的条件
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        if(vehicle==null){
            _logger.info("根据vin:"+vin+"没有找到对应的车辆信息，initCheck无法继续.");
            return re;
        }else{
            String _vehicleModel=vehicle.getModel();
            if(_vehicleModel.trim().equalsIgnoreCase("F60")){
                isM8X=false;//F60车型
            }
            _logger.info("根据vin:"+vin+"找到对应的车辆信息，车型:"+_vehicleModel+" isM8X:"+isM8X);
        }

        List<RealTimeReportData> realTimeReportDataList=realTimeReportDataRespository.findLatestOneByVin(vin);
        if(realTimeReportDataList!=null){
            if(realTimeReportDataList.size()>0){
                realData=realTimeReportDataList.get(0);
            }
        }
        List<GpsData> gpsDataList=gpsDataRepository.findLatestByVin(vin);
        if(gpsDataList!=null){
            if(gpsDataList.size()>0){
                gpsData=gpsDataList.get(0);
            }
        }
        if(realData!=null&&gpsData!=null) {
            if (realData.getLeftFrontDoorInformation().equals("1") && realData.getLeftRearDoorInformation().equals("1") &&
                    realData.getRightFrontDoorInformation().equals("1") && realData.getRightRearDoorInformation().equals("1")) {
                doorsCheck = true;
            }//左前车门信息 0开1关2保留3信号异常
           if (realData.getLeftFrontWindowInformation().equals("2") && realData.getLeftRearWindowInformation().equals("2")
                    && realData.getRightFrontWindowInformation().equals("2") && realData.getRightRearWindowInformation().equals("2")) {
                windowsCheck = true;
            }
            if (realData.getSkylightState().equals("2")) {
                sunroofCheck = true;
            }
            if (realData.getTrunkLidState().equals("1")) {
                trunkCheck = true;
            }
            if (realData.getEngineCoverState().equals("1")) {
                bonnetCheck = true;
            }
            if (gpsData.getSpeed() == 0) {
                vehicleSpeedCheck = true;
            }
        }
        //右后车窗信息 0开1半开2关3信号异常
        sunroofCheck = true;
        trunkCheck = true;
        bonnetCheck = true;
        windowsCheck=true;//todo 关于车窗全部屏蔽检查，后续实车加上
        _logger.info("[0x31]initCheck:"+vehicleSpeedCheck +"-"+ sunroofCheck +"-"+ windowsCheck +"-"+ doorsCheck +"-"+ trunkCheck +"-"+ bonnetCheck);
        re=vehicleSpeedCheck && sunroofCheck && windowsCheck && doorsCheck && trunkCheck && bonnetCheck;
       /* if(isM8X) {//todo M8X车型直接忽略check,返回true
            re = true;//临时处理 返回true
        }*/
        _logger.info("[0x31]initCheck result:"+re);
        return re;
    }

    /**
     * 检测对应TBox远程次数是否达到最大值
     * @param vin vin
     * @return 是否达到最大值
     */
    private boolean isRemoteMaxCountReached(String vin){
        //测对应TBox远程次数是否达到最大值
        boolean re=false;
        Vehicle v=vehicleRepository.findByVin(vin);
        if(v!=null){
            if(v.getRemoteCount()>=_maxCount)
                re=true;
        }
        return re;
    }


    /**
     *
     * @param remoteControlSettingShow 设置参数
     * @param vin 目标vin
     * @return 0设置成功 1唤醒失败 2 设置失败 3响应超时
     */
    public int sendRemoteSetting( RemoteControlSettingShow remoteControlSettingShow,String vin){
        if(!hasConnection(vin)){
            //如果不在线，先唤醒
            _logger.info("vin:"+vin+" 当前不在线，正在唤醒...");
            int wakeUpResult=remoteWakeUp(vin,wakeUpWaitSeconds);
            _logger.info("vin:"+vin+" 唤醒结果:"+wakeUpResult+" (参考值1:成功 0:失败)");
            if(wakeUpResult==0){
                return 1;//唤醒失败
            }
        }
        long eventId=dataTool.getCurrentSeconds();
        //唤醒成功
        if(hasConnection(vin)){
            _logger.info("vin:"+vin+" 在线，即将发送命令...");
            //保存到数据库
            String byteStr=outputHexService.getRemoteControlSettingReqHex(remoteControlSettingShow,eventId);
            String _val=socketRedis.getHashString(dataTool.connection_hashmap_name,vin);
            String _serverId=_val.split("-")[0];
            outputHexService.saveCmdToRedis(_serverId,vin,byteStr);//发送命令
         }
        //1有结果 0无结果
        String key=vin+"-"+eventId;
        int checkResultUntilTimeOut=checkResultFromRedis(dataTool.remoteControlSet_hashmap_name, key, remoteSettingTimeOut);
        if(checkResultUntilTimeOut==1){
            //读取结果并返回至api
            String settingResult=socketRedis.getHashString(dataTool.remoteControlSet_hashmap_name,key);
            if(settingResult!=null){
                if(settingResult.equals("0")){
                    return 0;//设置成功
                }
            }
         return 2;//设置失败
        }
        return 3;//响应超时
    }


    /**
     *
     * @param id 记录id
     * @param vin 目标vin
     * @return  >null超时导致 非null包含结果信息
     */
    public RemoteControl getRemoteControlResult(String id,String vin){
      long resultId=checkRemoteControlResult(id, vin);
        _logger.info("exist resultId:" + resultId);
        if(resultId>0){
            RemoteControl remoteControl=remoteControlRespositoryDao.findById(resultId);
            remoteControl=remoteControlRespositoryDao.findById(resultId);//2次调用执行SQL，第一次走缓存
            return remoteControl;
        }else{
            return null;
        }
    }

    /**
     *
     * @param id 记录id
     * @param vin 目标vin
     * @return  >0成功的结果id 0响应超时
     */
    public long checkRemoteControlResult(String id,String vin){
        //1有结果 0无结果
        String key=vin+"-"+id;
        int checkResultUntilTimeOut = checkResultFromRedis(dataTool.remoteControl_hashmap_name, key, remoteControlTimeOut);
        if(checkResultUntilTimeOut==1){
            //读取结果并返回至api
            String resultId=socketRedis.getHashString(dataTool.remoteControl_hashmap_name,key);
            if(resultId!=null){
                return Long.parseLong(resultId);
            }
          }
        return 0;//响应超时
    }

    /**
     * 获取车辆实时数据
     * @param vin vin
     * @return 封装数据的vo实体
     */
    public RealTimeDataShow getRealTimeData(String vin){
        if(!hasConnection(vin)){
            //如果不在线，先唤醒
            _logger.info("vin:"+vin+" 当前不在线，正在唤醒...");
            int wakeUpResult=remoteWakeUp(vin,1);//唤醒后等待50s
            _logger.info("vin:"+vin+" 唤醒结果:"+wakeUpResult+" (参考值1:成功 0:失败)");
            try{
                _logger.info("vin："+vin+"不在线，发送唤醒请求后，等待一段时间再去获取最新实时数据，目前全过程时间50s");
                Thread.sleep(49 * 1000);
                _logger.info("时间到，返回vin："+vin+" 的最新一条实时数据");
            }catch (InterruptedException e){

            }
        }
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

            data.setFuelOil((int) rd.getFuelOil());
            data.setAvgOilA(rd.getAvgOilA());
            data.setAvgOilB(rd.getAvgOilB());
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

            data.setOilLife(rd.getOilLife());
            data.setParkingState(Integer.parseInt(rd.getParkingState()));
            data.setMileageRange(rd.getMileageRange());
            data.setDrivingRange(rd.getDrivingRange());
            data.setDrivingTime(rd.getDrivingTime());
            data.setSkylightState(Integer.parseInt(rd.getSkylightState()));
            data.setEngineDoorInformation(Integer.parseInt(rd.getEngineCoverState()));
            data.setTrunkDoorInformation(Integer.parseInt(rd.getTrunkLidState()));
            data.setAverageSpeedA(rd.getAverageSpeedA());
            data.setAverageSpeedB(rd.getAverageSpeedB());
            data.setMtGearPostion(rd.getMtGearPostion());

            data.setFmax(280);
            data.setFmin(200);
            data.setRmax(290);
            data.setRmin(210);
            data.setLfok(this.getLfokStatu(rd.getLeftFrontTirePressure()));
            data.setLrok(this.getLrokStatu(rd.getLeftRearTirePressure()));
            data.setRfok(this.getRfokStatu(rd.getRightRearTirePressure()));
            data.setRrok(this.getRrokStatu(rd.getRightRearTirePressure()));

            data.setIsLocation(gd.getIsLocation());//
            data.setNorthSouth(gd.getNorthSouth());//
            data.setEastWest(gd.getEastWest());//
            data.setLatitude(gd.getLatitude());
            data.setLongitude(gd.getLongitude());
            data.setSpeed(gd.getSpeed());
            data.setHeading(gd.getHeading());
            data.setBatteryVoltage(rd.getVoltage());
            //数据转换处理
            int p = 100 * data.getFuelOil() / 64;
            p = p < 0 ? 0 : p;
            p = p > 100 ? 100 : p;
            data.setFuelOil(p);//返回百分比整数  64=100%  0-100
            if (rd.getFuelOil() == -200) {//保持无效值返回到api
                data.setFuelOil(-200);
            }
            float _avgOilA = data.getAvgOilA();
            BigDecimal bdA = new BigDecimal((double) _avgOilA);
            bdA = bdA.setScale(1, BigDecimal.ROUND_HALF_DOWN);//四舍五入保留一位小数
            data.setAvgOilA(bdA.floatValue());
            if (rd.getAvgOilA() == -200) {//保持无效值返回到api
                data.setAvgOilA(-200);
            }
            float _avgOilB = data.getAvgOilB();
            BigDecimal bdB = new BigDecimal((double) _avgOilB);
            bdB = bdB.setScale(1, BigDecimal.ROUND_HALF_DOWN);//四舍五入保留一位小数
            data.setAvgOilB(bdB.floatValue());
            if (rd.getAvgOilB() == -200) {//保持无效值返回到api
                data.setAvgOilB(-200);
            }
            float _voltage = data.getBatteryVoltage();
            BigDecimal bdC = new BigDecimal((double) _voltage);
            bdC = bdC.setScale(2, BigDecimal.ROUND_HALF_DOWN);//四舍五入保留2位小数
            data.setBatteryVoltage(bdC.floatValue());
            if (rd.getVoltage() == -200) {//保持无效值返回到api
                data.setBatteryVoltage(-200);
            }
            //胎压处理
            data.setLeftFrontTirePressure(dataTool.getRoundHalfDown(rd.getLeftFrontTirePressure(), 1));
            if (rd.getLeftFrontTirePressure() == -200) {//保持无效值返回到api
                data.setLeftFrontTirePressure(-200);
            }
            data.setLeftRearTirePressure(dataTool.getRoundHalfDown(rd.getLeftRearTirePressure(), 1));
            if (rd.getLeftRearTirePressure() == -200) {//保持无效值返回到api
                data.setLeftRearTirePressure(-200);
            }
            data.setRightFrontTirePressure(dataTool.getRoundHalfDown(rd.getRightFrontTirePressure(), 1));
            if (rd.getRightFrontTirePressure() == -200) {//保持无效值返回到api
                data.setRightFrontTirePressure(-200);
            }
            data.setRightRearTirePressure(dataTool.getRoundHalfDown(rd.getRightRearTirePressure(), 1));
            if (rd.getRightRearTirePressure() == -200) {//保持无效值返回到api
                data.setRightRearTirePressure(-200);
            }
            return data;
        }

        }

    public int getLfokStatu(Float leftFrontTirePressure){
        if(leftFrontTirePressure>=200 && leftFrontTirePressure<=280){
            return 0;
        }
        return 1;
    }
    public int getLrokStatu(Float leftRearTirePressure){
        if(leftRearTirePressure>=210 && leftRearTirePressure<=290){
            return 0;
        }
        return 1;
    }
    public int getRfokStatu(Float rightFrontTirePressure){
        if(rightFrontTirePressure>=200 && rightFrontTirePressure<=280){
            return 0;
        }
        return 1;
    }
    public int getRrokStatu(Float rightRearTirePressure){
        if(rightRearTirePressure>=210 && rightRearTirePressure<=290){
            return 0;
        }
        return 1;
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
            //gd=gpsTool.convertToGCJ02(gd); //2016.11.28取消服务端坐标系统转换
        }
        return gd;
    }

    public Page getRemoteControlShowList(String vin,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage){
        Page remoteControlPage = remoteControlRespositoryDao.findRemoteControlByVin(vin, orderByProperty, ascOrDesc, pageSize, currentPage);

        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List list = remoteControlPage.getItems();
        List<RemoteControl> newList = new ArrayList<>();
        for(Object obj:list)
        {
            newList.add((RemoteControl)obj);
        }

        remoteControlPage.setItems(transFormRemoteControl(newList,vehicle));
        return remoteControlPage;
    }

    /**
     * 获取remotecontrol表的全部属性，并添加Vehicle的车牌属性，组成一个新的集合
     * */
    public List<RemoteControlShow> transFormRemoteControl(List <RemoteControl>remoteControllList,Vehicle vehicle ){
        List<RemoteControlShow> remoteControlAndVehicle=new ArrayList<>();
        for(int i=0;i<remoteControllList.size();i++){
            RemoteControlShow remoteControlShow = new RemoteControlShow();
           if (vehicle!=null) {
               remoteControlShow.setLicensePlate(vehicle.getLicense_plate());
           }
            remoteControlShow.setId(remoteControllList.get(i).getId());
            remoteControlShow.setSessionId(remoteControllList.get(i).getSessionId());
            remoteControlShow.setSendingTime(remoteControllList.get(i).getSendingTime());
            remoteControlShow.setRemark(remoteControllList.get(i).getRemark());
            remoteControlShow.setRemarkEn(remoteControllList.get(i).getRemarkEn());
            remoteControlShow.setStatus(remoteControllList.get(i).getStatus());
            remoteControlShow.setUid(remoteControllList.get(i).getUid());
            remoteControlShow.setControlType(remoteControllList.get(i).getControlType());
            remoteControlShow.setAcTemperature(remoteControllList.get(i).getAcTemperature());
            //0625新增加的控制指令
            remoteControlShow.setLightNum(remoteControllList.get(i).getLightNum());
            remoteControlShow.setHornNum(remoteControllList.get(i).getHornNum());
            remoteControlShow.setActTime(remoteControllList.get(i).getActTime());
            remoteControlShow.setDeActive(remoteControllList.get(i).getDeActive());
            remoteControlShow.setAutoMode(remoteControllList.get(i).getAutoMode());
            remoteControlShow.setRecirMode(remoteControllList.get(i).getRecirMode());
            remoteControlShow.setAcMode(remoteControllList.get(i).getAcMode());
            remoteControlShow.setFan(remoteControllList.get(i).getFan());
            remoteControlShow.setMode(remoteControllList.get(i).getMode());
            remoteControlShow.setMasterStat(remoteControllList.get(i).getMasterStat());
            remoteControlShow.setMasterLevel(remoteControllList.get(i).getMasterLevel());
            remoteControlShow.setSlaveStat(remoteControllList.get(i).getSlaveStat());
            remoteControlShow.setSlaveLevel(remoteControllList.get(i).getSlaveLevel());

            remoteControlShow.setVin(remoteControllList.get(i).getVin());
            remoteControlAndVehicle.add(remoteControlShow);
        }
        return remoteControlAndVehicle;
    }

    /**
     * 获取诊断数据，来自故障数据转换
     * @param vin 目标vin
     * @return DiagnosticData或者null
     */
    public DiagnosticDataShow getDiagDataShowFromFailure(String vin,int lang){
        ///{lang} 1中文 2英文
        FailureMessageData failureMessageData=failureMessageDataRespository.findTopByVinOrderByReceiveTimeDescIdDesc(vin);
        if(failureMessageData==null){
            return null;
        }
        DiagnosticDataShow diagnosticDataShow=new DiagnosticDataShow();
        diagnosticDataShow.setId(failureMessageData.getId());
        diagnosticDataShow.setVin(failureMessageData.getVin());
        diagnosticDataShow.setHasAck((short) 1);
        diagnosticDataShow.setEventId((long) dataTool.getCurrentSeconds());
        diagnosticDataShow.setSendDate(failureMessageData.getSendingTime());
        diagnosticDataShow.setReceiveDate(failureMessageData.getSendingTime());
        HashMap<String,String>  content=getDiagDataFromFailure(failureMessageData,lang);
        diagnosticDataShow.setList(content);
        return  diagnosticDataShow;
    }

    /**
     * 获取诊断数据，来自故障数据转换
     * @param failureMessageData 故障数据
     * @return DiagnosticData或者null
     */
    public HashMap<String,String> getDiagDataFromFailure(FailureMessageData failureMessageData,int lang){
        //todo 转换逻辑暂缺
        if(failureMessageData!=null){
            List<WarningMessageConversion> allList=warningMessageConversionRepository.findAll();
            String[] failureId=failureMessageData.getIdArray();//包含故障信息ID的数组
            String[] array=failureMessageData.getIdArray();
            HashMap<String,String>  content=new HashMap<>();

            for(int i=0;i<allList.size();i++){
                WarningMessageConversion warningMessageConversion=allList.get(i);
                if(lang==1){
                    content.put(warningMessageConversion.getGroupMessage(), "0");
                }else{
                    content.put(warningMessageConversion.getGroupMessageEn(), "0");
                }
            }
            for (int i = 0; i <failureId.length ; i++) {
                for(int j=0;j<allList.size();j++){
                    WarningMessageConversion warningMessageConversion=allList.get(j);
                    if(warningMessageConversion.getMessageId().equals(failureId[i])){
                        if(lang==1){
                            content.put(warningMessageConversion.getGroupMessage(), "1");
                        }else{
                            content.put(warningMessageConversion.getGroupMessageEn(), "1");
                        }
                    }
                }
            }
            return content;
        }else{
            _logger.info("no failure data exist!");
            return null;//没有故障数据
        }
    }

    /**
     * 修改远程控制
     * */
    public int modifyRemoteControl(String ids){
        return remoteControlRespositoryDao.modifyRemoteControl(ids);
    }

}
