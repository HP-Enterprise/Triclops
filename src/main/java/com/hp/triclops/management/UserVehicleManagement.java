package com.hp.triclops.management;

import com.hp.triclops.repository.UserVehicleRelativeExRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@Component
public class UserVehicleManagement {

    @Autowired
    UserVehicleRelativeExRepository userVehicleRelativeExRepository;

    /**
     * 查询用户车辆
     * @param uid 用户ID
     * @return 车辆ID集合
     */
    public List<Integer> findVidByUid(int uid)
    {
        return userVehicleRelativeExRepository.findVidByUid(uid);
    }
}
