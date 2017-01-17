package com.hp.triclops.service;

import com.hp.triclops.entity.*;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.repository.UserVehicleRelativedRepository;
import com.hp.triclops.repository.VehicleModelConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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


    public  List<ModelFunction> getConfigByUser(int userId){

        List<ModelFunction> result=new ArrayList<>();
        User u=userRepository.findById(userId);
        if(u!=null){
            List<UserVehicleRelatived> userVehicleRelativedList=userVehicleRelativedRepository.findByUid(u);
            if(userVehicleRelativedList!=null&&userVehicleRelativedList.size()>0){
                for (int i = 0; i < userVehicleRelativedList.size(); i++) {
                    UserVehicleRelatived relatived=userVehicleRelativedList.get(i);
                    Vehicle vehicle=relatived.getVid();
                    List<VehicleModelConfig> vehicleModelConfigList=vehicleModelConfigRepository.findByModelName(vehicle.getModel());
                    if(vehicleModelConfigList!=null && vehicleModelConfigList.size()>0){
                        result.add(new ModelFunction(vehicleModelConfigList.get(0).getModelName(),vehicleModelConfigList.get(0).getFunction()));
                    }

                }

            }

        }
        return result;
    }


}
