package com.hp.triclops.manager;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.VehicleRepository;
import com.hp.triclops.utils.MD5;
import com.hp.triclops.utils.SaltStringUtil;
import org.springframework.context.ApplicationContext;

/**
 * Created by liz on 2015/10/13.
 */

public class Vehicle6S{


    public VehicleRepository vehicleRepository;

    private Vehicle vehicle;


    private int vid;

    public ApplicationContext appContext;

    public Vehicle6S() {
    }

    /**
     * 构造有参函数
     * @param vid 车辆ID
     */

    public Vehicle6S(int vid){
         this.setVid(vid);
    }

    /**
     * 根据车辆ID查找车辆信息
     * @param vid 车辆ID
     * @return 执行完后的Vehicle
     */
    public Vehicle findVehicleById(int vid){
        return this.vehicleRepository.findById(vid);
    }


    /**
     * 手动注入Repository
     * @param appContext ApplicationContext
     */
    public void setAppCtxAndInit(ApplicationContext appContext){
        this.appContext = appContext;
        this.vehicleRepository = this.appContext.getBean(VehicleRepository.class);
        this.setVehicle(this.findVehicleById(this.getVid()));
    }

    /**
     * 设置车辆安防密码
     * @param securityPwd 安防密码
     * @return 执行完后的Vehicle
     */
    public Vehicle setSecurityPwd(String securityPwd){
        MD5 md5 = new MD5();
        String saltStr = SaltStringUtil.getSaltString(4);
        String pwdWithSaltOfMD5 = md5.getMD5ofStr(securityPwd + saltStr);

        Vehicle vehicle = new Vehicle();
        vehicle.setSecurity_pwd(pwdWithSaltOfMD5);
        vehicle.setSecurity_salt(saltStr);
        return vehicle;

    }

    /**
     * 检验车辆安防密码
     * @param vid 车辆id
     * @param securityPwd 安防密码
     * @return 检测安防密码成功返回true,失败为false
     */
    public boolean verifySecurityPwd(int vid,String securityPwd){
        boolean flag = false;
        MD5 md5 = new MD5();
        Vehicle vehicle=this.vehicleRepository.findById(vid);
        if(vehicle != null){
            String pwdVerify = md5.getMD5ofStr(securityPwd + vehicle.getSecurity_salt());
            if(vehicle.getSecurity_pwd().equals(pwdVerify)){
                flag = true;
            }else {
                flag = false;
            }
        }
        return flag;
    }



    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }


}
