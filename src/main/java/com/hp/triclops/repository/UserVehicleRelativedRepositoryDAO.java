package com.hp.triclops.repository;

import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.vo.UserVehicleRelativedShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Teemol on 2015/9/5.
 */

@Component
public class UserVehicleRelativedRepositoryDAO<T>  {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserVehicleRelativedRepository userVehicleRelativedRepository;
    @Autowired
    VehicleRepository vehicleRepository;

    /**
     * 查询车主车辆关系集
     * @param uid 用户id
     * @param vid 车辆vid
     * @param iflag '车主标识 1 车主 0 朋友'
     * @return 车主车辆关系
     */
    public UserVehicleRelativedShow getRelative(int uid, int vid,Integer iflag)
    {

        Vehicle vehilce = vehicleRepository.findById(vid);
        User user = userRepository.findById(uid);
        UserVehicleRelatived userVehicleRelatived = userVehicleRelativedRepository.findOneReative(user,vehilce,iflag,user);
        if(userVehicleRelatived == null)
            return null;
        UserVehicleRelativedShow us = new UserVehicleRelativedShow();
        us.setId(userVehicleRelatived.getId());
        us.setUid(userVehicleRelatived.getUid().getId());
        us.setVid(userVehicleRelatived.getVid().getId());
        us.setIflag(userVehicleRelatived.getIflag());
        us.setVflag(userVehicleRelatived.getVflag());
        us.setParentuser(userVehicleRelatived.getParentuser().getId());
        return us;
    }

    /**
     * 通过vid查询用户与车辆的关系
     * @param vid 车辆ID
     * @param iflag '车主标识 1 车主 0 朋友'
     * @return 车主车辆关系集
     */
    public List<UserVehicleRelativedShow> getRelativeByVid(int vid, Integer iflag)
    {
        Vehicle vehicle = vehicleRepository.findById(vid);
        List<UserVehicleRelatived> userVehicleRelativedList =  userVehicleRelativedRepository.findByVid(vehicle);
        List<UserVehicleRelativedShow> list = new ArrayList<UserVehicleRelativedShow>();
        for (int i = 0; i < userVehicleRelativedList.size(); i++)
        {
            UserVehicleRelatived userVehicleRelatived = userVehicleRelativedList.get(i);
            if(iflag!=null)
            {
                if(userVehicleRelatived.getIflag() == iflag)
                {
                    list.add(new UserVehicleRelativedShow(userVehicleRelatived));
                }
            }
            else
            {
                list.add(new UserVehicleRelativedShow(userVehicleRelatived));
            }
        }
        return list;
    }

    /**
     * 查询车主与车辆关系
     * @param vehicle 车辆信息
     * @return 车主车辆关系集
     */
    public UserVehicleRelativedShow getRelativeByVid(Vehicle vehicle)
    {
        List<UserVehicleRelatived> userVehicleRelativedList =  userVehicleRelativedRepository.findByVid(vehicle);
        List<UserVehicleRelativedShow> list = new ArrayList<UserVehicleRelativedShow>();

        UserVehicleRelativedShow UserVehicleRelativedShow = null;
        for (UserVehicleRelatived u:userVehicleRelativedList)
        {
            if(u.getIflag() == 1)
            {
                UserVehicleRelativedShow = new UserVehicleRelativedShow(u);
                break;
            }
        }

        return UserVehicleRelativedShow;
    }

}


