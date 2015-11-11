package com.hp.triclops.vehicle;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.vo.UserVehicleRelativedShow;
import org.springframework.stereotype.Component;

/**
 * Created by Teemol on 2015/11/11.
 */
@Component
public class VehicleManagement {

    /**
     * 修改用户与车辆的绑定信息
     * @param userVehicleRelativedShow 用户与车辆绑定信息
     */
    public boolean updateUserVehicleRelatived(UserVehicleRelativedShow userVehicleRelativedShow){
        return true;
    }

    /**
     * 查询用户的默认车辆
     * @param uid 用户ID
     */
    public Vehicle getDefaultCar(int uid){

        return null;
    }


}
