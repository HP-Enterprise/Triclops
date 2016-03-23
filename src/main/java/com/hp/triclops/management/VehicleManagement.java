package com.hp.triclops.management;

import com.hp.triclops.entity.VehicleEx;
import com.hp.triclops.repository.VehicleExRepository;
import com.hp.triclops.utils.MD5;
import com.hp.triclops.vo.VehicleExShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@Component
public class VehicleManagement {

    @Autowired
    OrganizationVehicleManagement organizationVehicleManagement;

    @Autowired
    VehicleExRepository vehicleExRepository;

    /**
     * 根据id查询车辆信息
     * @param vid 车辆ID
     * @return 车辆信息
     */
    public VehicleExShow findById(int vid)
    {
        VehicleEx vehicle = vehicleExRepository.findById(vid);
        if(vehicle == null)
            return null;
        return new VehicleExShow(vehicle);
    }

    /**
     * 根据车辆ID集合查询车辆信息
     * @param vids 车辆ID集合
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息
     */
    public Page<VehicleExShow> findByVids(List<Integer> vids, Integer currentPage, Integer pageSize)
    {
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        if(vids == null || vids.size()==0)
        {
            return new PageImpl<>(new ArrayList<>(),p,0);
        }

        Page<VehicleEx> vehiclePage = vehicleExRepository.findByVids(vids,p);

        List<VehicleEx> list = vehiclePage.getContent();
        List<VehicleExShow> returnList = new ArrayList<>();
        for(VehicleEx vehicle:list)
        {
            VehicleExShow vehicleExShow = new VehicleExShow(vehicle);
            returnList.add(vehicleExShow);
        }

        return new PageImpl<>(returnList,p,vehiclePage.getTotalElements());
    }

    /**
     * 根据vin查询车辆信息
     * @param vin 车架号
     * @return 车辆信息
     */
    public VehicleExShow findByVin(String vin)
    {
        VehicleEx vehicle = vehicleExRepository.findByVin(vin);
        if(vehicle == null)
            return null;
        return new VehicleExShow(vehicle);
    }

    /**
     * 根据tbox码查询车辆信息
     * @param tboxsn Tbox码
     * @return 车辆信息
     */
    public VehicleExShow findByTboxsn(String tboxsn)
    {
        VehicleEx vehicle = vehicleExRepository.findByTboxsn(tboxsn);
        if(vehicle == null)
            return null;
        return new VehicleExShow(vehicle);
    }

    /**
     * 根据Vin、Tbox码查询车辆信息
     * @param vin 车辆VIN码
     * @param tboxsn 车辆TBOX码
     * @return 车辆信息
     */
    public VehicleExShow findByVinAndTbox(String vin, String tboxsn)
    {
        VehicleEx vehicle = vehicleExRepository.findByVinAndTbox(vin,tboxsn);
        if(vehicle == null)
            return null;
        return new VehicleExShow(vehicle);
    }

    /**
     * 条件查询车辆(具有Read权限的组织成员)
     * @param oid 组织ID
     * @param vin 车架号
     * @param tboxsn tbox码
     * @param vendor 厂家
     * @param model 型号
     * @param start_date 生产日期开始范围
     * @param end_date 生产日期结束范围
     * @param license_plate 车牌号
     * @param t_flag 是否可用 0：车辆未禁用 1：已禁用'
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<VehicleExShow> readSelect(int oid, String vin, String tboxsn, String vendor, String model, Date start_date, Date end_date, String license_plate, Integer t_flag, Integer currentPage, Integer pageSize)
    {
        if(vin!=null) vin = "%" + vin + "%";
        if(tboxsn!=null) tboxsn = "%" + tboxsn + "%";
        if(vendor!=null) vendor = "%" + vendor + "%";
        if(model!=null) model = "%" + model + "%";
        if(license_plate!=null) license_plate = "%" + license_plate + "%";
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<Integer> vidsPage = organizationVehicleManagement.findVidsByOid(oid,currentPage,pageSize);
        List<Integer> vids = vidsPage.getContent();
        if(vids == null || vids.size()==0)
        {
            return new PageImpl<>(new ArrayList<>(),p,0);
        }

        List<VehicleEx> list = vehicleExRepository.selectVehicle(vids,vin,tboxsn,vendor,model,start_date,end_date,license_plate,t_flag);
        List<VehicleExShow> returnList = new ArrayList<>();
        for(VehicleEx vehicle:list)
        {
            VehicleExShow vehicleExShow = new VehicleExShow(vehicle);
            returnList.add(vehicleExShow);
        }

        return new PageImpl<>(returnList,p,vidsPage.getTotalElements());
    }

    /**
     * 条件查询车辆(组织中普通成员查询)
     * @param oid 组织ID
     * @param vin 车架号
     * @param tboxsn tbox码
     * @param vendor 厂家
     * @param model 型号
     * @param start_date 生产日期开始范围
     * @param end_date 生产日期结束范围
     * @param license_plate 车牌号
     * @param t_flag 是否可用 0：车辆未禁用 1：已禁用'
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<VehicleExShow> select(int oid, String vin, String tboxsn, String vendor, String model, Date start_date, Date end_date, String license_plate, Integer t_flag, Integer currentPage, Integer pageSize)
    {
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<VehicleExShow> vehiclePage = readSelect(oid, vin, tboxsn, vendor, model, start_date, end_date, license_plate, t_flag, currentPage, pageSize);

        List<VehicleExShow> list = vehiclePage.getContent();
        List<VehicleExShow> returnList = new ArrayList<>();
        for(VehicleExShow vehicle:list)
        {
            vehicle.blur();
            returnList.add(vehicle);
        }

        return new PageImpl<>(returnList,p,vehiclePage.getTotalElements());
    }

    /**
     * 条件查询车辆（超级管理员）
     * @param vin 车架号
     * @param tboxsn tbox码
     * @param vendor 厂家
     * @param model 型号
     * @param start_date 生产日期开始范围
     * @param end_date 生产日期结束范围
     * @param license_plate 车牌号
     * @param t_flag 是否可用 0：车辆未禁用 1：已禁用'
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<VehicleExShow> adminSelect(String vin, String tboxsn, String vendor, String model, Date start_date,Date end_date,String license_plate,Integer t_flag, Integer currentPage,Integer pageSize)
    {
        if(vin!=null) vin = "%" + vin + "%";
        if(tboxsn!=null) tboxsn = "%" + tboxsn + "%";
        if(vendor!=null) vendor = "%" + vendor + "%";
        if(model!=null) model = "%" + model + "%";
        if(license_plate!=null) license_plate = "%" + license_plate + "%";
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<VehicleEx> vehiclePage  = vehicleExRepository.selectVehicle(vin,tboxsn,vendor,model,start_date,end_date,license_plate,t_flag,p);

        List<VehicleEx> list = vehiclePage.getContent();
        List<VehicleExShow> returnList = new ArrayList<>();
        for(VehicleEx vehicle:list)
        {
            VehicleExShow vehicleExShow = new VehicleExShow(vehicle);
            returnList.add(vehicleExShow);
        }

        return new PageImpl<>(returnList,p,vehiclePage.getTotalElements());
    }

    /**
     * 条件查询车辆（普通用户）
     * @param vin 车架号
     * @param tboxsn tbox码
     * @param vendor 厂家
     * @param model 型号
     * @param start_date 生产日期开始范围
     * @param end_date 生产日期结束范围
     * @param license_plate 车牌号
     * @param t_flag 是否可用 0：车辆未禁用 1：已禁用'
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<VehicleExShow> generalSelect(String vin, String tboxsn, String vendor, String model, Date start_date, Date end_date, String license_plate, Integer t_flag, Integer currentPage, Integer pageSize)
    {
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<VehicleExShow> vehiclePage = adminSelect(vin, tboxsn, vendor, model, start_date,end_date,license_plate,t_flag,currentPage,pageSize);

        List<VehicleExShow> list = vehiclePage.getContent();
        List<VehicleExShow> returnList = new ArrayList<>();
        for(VehicleExShow vehicle:list)
        {
            vehicle.blur();
            returnList.add(vehicle);
        }

        return new PageImpl<>(returnList,p,vehiclePage.getTotalElements());
    }

    /**
     * 条件查询组织外的车辆
     * @param oid 组织ID
     * @param vin 车架号
     * @param tboxsn tbox码
     * @param vendor 厂家
     * @param model 型号
     * @param start_date 生产日期开始范围
     * @param end_date 生产日期结束范围
     * @param license_plate 车牌号
     * @param t_flag 是否可用 0：车辆未禁用 1：已禁用'
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<VehicleExShow> selectVehicleNotInOrg(int oid, String vin, String tboxsn, String vendor, String model, Date start_date, Date end_date, String license_plate, Integer t_flag, Integer currentPage, Integer pageSize)
    {
        if(vin!=null) vin = "%" + vin + "%";
        if(tboxsn!=null) tboxsn = "%" + tboxsn + "%";
        if(vendor!=null) vendor = "%" + vendor + "%";
        if(model!=null) model = "%" + model + "%";
        if(license_plate!=null) license_plate = "%" + license_plate + "%";
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);
        
        List<Integer> vids = organizationVehicleManagement.findVidsByOid(oid);
        if(vids.size()==0)
        {
            vids.add(0);
        }
        Page<VehicleEx> vehiclePage = vehicleExRepository.selectVehicleAbsent(vids,vin,tboxsn,vendor,model,start_date,end_date,license_plate,t_flag,p);

        List<VehicleEx> list = vehiclePage.getContent();
        List<VehicleExShow> returnList = new ArrayList<>();
        for(VehicleEx vehicle:list)
        {
            VehicleExShow vehicleExShow = new VehicleExShow(vehicle);
            vehicleExShow.blur();
            returnList.add(vehicleExShow);
        }

        return new PageImpl<>(returnList,p,vehiclePage.getTotalElements());
    }

    /**
     * 新增车辆信息
     * @param vehicleExShow 车辆信息
     * @return 新增的车辆信息
     */
    public VehicleExShow addVehicle(VehicleExShow vehicleExShow)
    {
        VehicleEx vehicleEx = new VehicleEx(vehicleExShow);
        VehicleEx returnVehicle = vehicleExRepository.save(vehicleEx);
        return new VehicleExShow(returnVehicle);
    }

    /**
     * 修改车辆信息
     * @param vehicleExShow 车辆信息
     * @return 修改后的车辆信息
     */
    public VehicleExShow modifyVehicle(VehicleExShow vehicleExShow)
    {
        VehicleEx vehicleEx = new VehicleEx(vehicleExShow);
        VehicleEx returnVehicle = vehicleExRepository.save(vehicleEx);
        return new VehicleExShow(returnVehicle);
    }

    /**
     * 安防密码校验
     * @param vid 车辆ID
     * @param securityPwd 安防密码
     * @return 校验状态  -2：车辆不存在  -1：未设置安防密码  0：安防密码错误  1：校验成功
     */
    public int verifySecurityPwd(int vid,String securityPwd)
    {
        MD5 md5 = new MD5();
        VehicleExShow vehicle = findById(vid);

        if(vehicle == null)
        {
            return -2;
        }

        String security_pwd = vehicle.getSecurity_pwd();
        if(security_pwd == null)
        {
            return -1;
        }

        String security_salt = vehicle.getSecurity_salt();
        String pwdVerify = md5.getMD5ofStr(securityPwd + security_salt);
        if(security_pwd.equals(pwdVerify))
        {
            return 1;
        }

        return 0;
    }

}
