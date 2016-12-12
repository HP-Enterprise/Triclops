package com.hp.triclops.service;

import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.entity.VehicleModelConfig;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.repository.UserVehicleRelativedRepository;
import com.hp.triclops.repository.VehicleModelConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jackl on 2016/12/12.
 */
@Component
public class VehicleModelConfigService {
    @Autowired
    private VehicleModelConfigRepository vehicleModelConfigRepository;
    @Autowired
    private UserVehicleRelativedRepository userVehicleRelativedRepository;
    @Autowired
    private UserRepository userRepository;

    public VehicleModelConfig getConfigByUser(int userId){
        VehicleModelConfig result=null;
        User u=userRepository.findById(userId);
        if(u!=null){
            List<UserVehicleRelatived> userVehicleRelativedList=userVehicleRelativedRepository.findByUidAndVflag(u, 1);
            if(userVehicleRelativedList!=null&&userVehicleRelativedList.size()>0){
                UserVehicleRelatived relatived=userVehicleRelativedList.get(0);
                Vehicle vehicle=relatived.getVid();
                if(vehicle!=null){
                    List<VehicleModelConfig> vehicleModelConfigList=vehicleModelConfigRepository.findByModelName(vehicle.getModel());//通过车型名称查询到车辆的配置参数
                    if(vehicleModelConfigList!=null&&vehicleModelConfigList.size()>0) {
                        result =vehicleModelConfigList.get(0);
                    }
                }
            }

        }
        return result;
    }


}
