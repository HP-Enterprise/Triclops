package com.hp.triclops.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hp.triclops.entity.*;
import com.hp.triclops.management.OrganizationVehicleManagement;
import com.hp.triclops.repository.*;
import com.hp.triclops.utils.HttpClientTool;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by luj on 2015/10/9.
 */
@Component
public class TboxService {
    @Autowired
    TBoxRepository tBoxRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    TBoxExRepository tBoxExRepository;
    @Autowired
    IccidPhoneRepository iccidPhoneRepository;
    @Autowired
    VehicleModelConfigRepository vehicleModelConfigRepository;
    @Autowired
    HttpClientTool httpClientTool;
    @Autowired
    VehicleTBoxRelativeRepository vehicleTboxRelativeRepository;
    @Autowired
    OrganizationVehicleManagement organizationVehicleManagement;


    private Logger _logger = LoggerFactory.getLogger(TboxService.class);

    /**
     * 更新车型信息
     */
    public void modifyVehicleModelInfo(String vin,Short modelId){
        Vehicle _vehicle=vehicleRepository.findByVin(vin);
        if(_vehicle!=null){
            VehicleModelConfig vehicleModelConfig=vehicleModelConfigRepository.findByModelId(modelId);
            if(vehicleModelConfig!=null){
                _vehicle.setModel(vehicleModelConfig.getModelName());
                _logger.info("更新车辆对应的车型信息:"+modelId+" "+vehicleModelConfig.getModelName());
                vehicleRepository.save(_vehicle);
            }else{
                _logger.info("没有查询到对应的车型信息:"+modelId);
            }
        }
    }

    //@Transactional
    public boolean activationTBox(String vin,Short modelId,String t_sn,String imei,String iccid){
        long startTime = System.currentTimeMillis();
        Vehicle _vehicle=vehicleRepository.findByVin(vin);
        TBox tb=tBoxRepository.findByImei(imei);
        Vehicle sVehicle=null;
        long startTime1 = System.currentTimeMillis();
        VehicleTBoxRelative vehicleTBoxRelative = new VehicleTBoxRelative();
        //先查询有没有记录，有记录则激活没有记录新增记录
        if(_vehicle==null){//没有车辆新增车辆
            Vehicle vehicle=new Vehicle();
            vehicle.setVin(vin);
            vehicle.setProduct_date(new Date(0));
            VehicleModelConfig vehicleModelConfig=vehicleModelConfigRepository.findByModelId(modelId);
            if(vehicleModelConfig!=null){
                vehicle.setModel(vehicleModelConfig.getModelName());
                _logger.info("更新车辆对应的车型信息:"+modelId+" "+vehicleModelConfig.getModelName());
            }else{
                _logger.info("没有查询到对应的车型信息:"+modelId);
            }

            vehicle.setTboxsn(t_sn);
            vehicle.setSecurity_pwd("14427FF4F90B790CAED65FC2DD854351");
            vehicle.setSecurity_salt("a5pb");
            vehicle.setRegTime(new Date());
            if (modelId ==1){
                vehicle.setRealNameAuthentication("4");
            }else{
                vehicle.setRealNameAuthentication("0");
            }
            sVehicle = vehicleRepository.save(vehicle);

            organizationVehicleManagement.addVehicle(1, sVehicle.getId());   // 默认添加到华晨汽车根组织
            organizationVehicleManagement.addVehicle(2, sVehicle.getId());   // 默认添加到生产辅助根组织
        }else{
            vehicleTBoxRelative.setOldTboxsn(_vehicle.getTboxsn());
            VehicleModelConfig vehicleModelConfig=vehicleModelConfigRepository.findByModelId(modelId);
            if(vehicleModelConfig!=null){
                _vehicle.setModel(vehicleModelConfig.getModelName());
                _logger.info("更新车辆对应的车型信息:"+modelId+" "+vehicleModelConfig.getModelName());
            }else{
//                sVehicle = _vehicle;
                _logger.info("没有查询到对应的车型信息:"+modelId);
            }
            if (_vehicle.getRealNameAuthentication().equals("1")){
                changeTbox(vin,iccid);
            }
            _vehicle.setTboxsn(t_sn);
            sVehicle=vehicleRepository.save(_vehicle);

        }

        vehicleTBoxRelative.setVin(sVehicle.getVin());
        vehicleTBoxRelative.setTboxsn(t_sn);
        vehicleTBoxRelative.setCreateTime(new Date());
        long middleTime = System.currentTimeMillis();
        //修改tbox表中的vin
        try {
            TBox tb1=tBoxRepository.findByVin(vin);
            if (tb1 !=null){
                tb1.setVin(null);
                tBoxRepository.save(tb1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        //更新TBox信息
        if(tb!=null){//已经存在TBox 激活TBox
            //查询该tbox激活前对应的车辆信息
            Vehicle oldVehicle = vehicleRepository.findByVin(tb.getVin());
            if(oldVehicle != null) {
                vehicleTBoxRelative.setOldVin(oldVehicle.getVin());
                if(!sVehicle.getVin().equals(oldVehicle.getVin())){
                    //清除tboxsn
                    oldVehicle.setTboxsn("");
                    vehicleRepository.save(oldVehicle);
                }
            }
            tb.setIs_activated(1);
            tb.setActivation_time(new Date());
            tb.setVin(vin);
            tb.setT_sn(t_sn);
            tb.setVehicle(sVehicle);
            tBoxRepository.save(tb);
            long endTime = System.currentTimeMillis();
            //保存变更关系
            vehicleTboxRelativeRepository.save(vehicleTBoxRelative);
            long endTime1 = System.currentTimeMillis();
            _logger.info(vin + " get vin and imei time" + (startTime1 - startTime));
            _logger.info(vin + " save vehicle  time" + (middleTime - startTime1));
            _logger.info(vin + " save tbox   time" + (endTime - middleTime));
            _logger.info(vin + " save vehicleTboxRelative time" + (endTime1 - endTime));
            _logger.info(vin + " save sub total time" + (endTime1 - startTime));
            return true;
        }else{//不存在TBox 新增TBox

            TBoxEx tBox=new TBoxEx();
//            if(_vehicle!=null){
//                tBox.setVid(_vehicle.getId());
//            }else{
//                tBox.setVid(sVehicle.getId());
//            }
            tBox.setVid(sVehicle.getId());
            tBox.setVin(vin);
            tBox.setT_sn(t_sn);
            tBox.setIs_activated(1);
            tBox.setImei(imei);
            tBox.setActivation_time(new Date());
            tBoxExRepository.save(tBox);
            long endTime = System.currentTimeMillis();
            //保存变更关系
            vehicleTboxRelativeRepository.save(vehicleTBoxRelative);
            long endTime1 = System.currentTimeMillis();
            _logger.info(vin + " get vin and imei time" + (startTime1 - startTime));
            _logger.info(vin + " save vehicle  time" + (middleTime - startTime1));
            _logger.info(vin + " save tbox time" + (endTime - middleTime));
            _logger.info(vin + " save vehicleTboxRelative  time" + (endTime1 - endTime));
            //更新车辆表中的sn信息
            if(!sVehicle.getTboxsn().equals(tBox.getT_sn())){
                sVehicle.setTboxsn(t_sn);
                vehicleRepository.save(sVehicle);
            }

            long endTime2 = System.currentTimeMillis();
            _logger.info(vin +" update vehicle tbox  time" + (endTime2 - endTime1));
            _logger.info(vin + " save sub total time" + (endTime2 - startTime));
            return true;
        }
    }

    private void changeTbox(String vin,String iccid){
        String result = httpClientTool.doHttp("/api/real/name/update/vin/inside?vin="+vin+"&&newiccid="+iccid,null);
        _logger.warn("changeTboxUnicom vin:"+vin+" result:"+result);
    }

    public boolean saveIccidPhone(String vin,String imei,String iccid) {
        long startTime = System.currentTimeMillis();
        TBox tb=tBoxRepository.findByImei(imei);
        if(iccid!=null){
            JSONObject obj = new JSONObject();
            obj.put("iccid", iccid);
            //根据iccid查询手机号
            String result = httpClientTool.doHttp("/api/terminal/getTerminalDetails", obj.toJSONString());
            if(StringUtil.isNullOrEmpty(result)){
                _logger.info("没有查询到对应的手机号:" + iccid);
            }else{
                JSONObject json = JSONObject.parseObject(result);
                String status = json.getString("status");
                if("success".equals(status)){
                    JSONObject msg = json.getJSONObject("message");
                    if(msg != null){
                        JSONArray list = msg.getJSONArray("list");
                        if(list != null && list.size() > 0){
                            JSONObject info = list.getJSONObject(0);
                            String phone = info.getString("msisdn");
                            if(phone.length() > 12){
                                phone = phone.substring(2, 13);
                                tb.setMobile(phone);
                                tBoxRepository.save(tb);
                                //保存手机号到数据库
                                IccidPhone iccidPhone = iccidPhoneRepository.findByIccid(iccid);
                                if(iccidPhone == null){
                                    iccidPhone = new IccidPhone();
                                    iccidPhone.setIccid(iccid);
                                    iccidPhone.setPhone(phone);
                                    iccidPhoneRepository.save(iccidPhone);
                                }else if(iccidPhone != null && !phone.equals(iccidPhone.getPhone())){
                                    iccidPhone.setPhone(phone);
                                    iccidPhoneRepository.save(iccidPhone);
                                }
                            }
                        }
                    }
                }else{
                    _logger.info("getTerminalDetails接口调用失败:" + json.getString("message"));
                    return false;
                }
            }
        }else{
            _logger.info("no iccid");
            return false;
        }
        long endTime = System.currentTimeMillis();
        _logger.info(vin + " save unicom data time" + (startTime - endTime));
        return true;
    }

}
