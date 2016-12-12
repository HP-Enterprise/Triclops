package com.hp.triclops.service;

import com.hp.triclops.entity.*;
import com.hp.triclops.repository.*;
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
    private Logger _logger = LoggerFactory.getLogger(TboxService.class);
    public boolean activationTBox(String vin,Short modelId,String t_sn,String imei,String iccid){
        Vehicle _vehicle=vehicleRepository.findByVin(vin);
        TBox tb=tBoxRepository.findByImei(imei);
        Vehicle sVehicle=null;
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
            sVehicle=vehicleRepository.save(vehicle);
        }else{
            VehicleModelConfig vehicleModelConfig=vehicleModelConfigRepository.findByModelId(modelId);
            if(vehicleModelConfig!=null){
                _vehicle.setModel(vehicleModelConfig.getModelName());
                _logger.info("更新车辆对应的车型信息:"+modelId+" "+vehicleModelConfig.getModelName());
                sVehicle=vehicleRepository.save(_vehicle);
            }else{
                _logger.info("没有查询到对应的车型信息:"+modelId);
            }
        }
            //更新TBox信息
        if(tb!=null){//已经存在TBox 激活TBox
            tb.setIs_activated(1);
            tb.setActivation_time(new Date());
            tb.setVin(vin);
            tb.setT_sn(t_sn);
            if(iccid!=null){
                IccidPhone iccidPhone=iccidPhoneRepository.findByIccid(iccid);
                if(iccidPhone!=null){
                    tb.setMobile(iccidPhone.getPhone());
                    _logger.info("find phone record for iccid:"+iccid+"-"+iccidPhone.getPhone());
                }else{
                    _logger.info("cann not find phone record for iccid:"+iccid);
                }
            }else{
                _logger.info("no iccid");
            }
            tBoxRepository.save(tb);
            return true;
        }else{//不存在TBox 新增TBox
            TBoxEx tBox=new TBoxEx();
            if(_vehicle!=null){
                tBox.setVid(_vehicle.getId());
            }else{
                tBox.setVid(sVehicle.getId());
            }

            if(iccid!=null){
                IccidPhone iccidPhone=iccidPhoneRepository.findByIccid(iccid);
                if(iccidPhone!=null){
                    tBox.setMobile(iccidPhone.getPhone());
                    _logger.info("find phone record for iccid:" + iccid + "-" + iccidPhone.getPhone());
                }else{
                    _logger.info("cann not find phone record for iccid:"+iccid);
                }
            }else{
                _logger.info("no iccid");
            }

            tBox.setVin(vin);
            tBox.setT_sn(t_sn);
            tBox.setIs_activated(1);
            tBox.setImei(imei);
            tBox.setActivation_time(new Date());
            tBoxExRepository.save(tBox);
            return true;
        }
    }
}
