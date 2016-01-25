package com.hp.triclops.management;

import com.hp.triclops.entity.VehicleEx;
import com.hp.triclops.repository.VehicleExRepository;
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
    OrganizationUserManagement organizationUserManagement;

    @Autowired
    OrganizationVehicleManagement organizationVehicleManagement;

    @Autowired
    UserVehicleManagement userVehicleManagement;

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
        return new VehicleExShow(vehicle);
    }

    /**
     * 查询用户有全选查看的车辆ID集合
     * @param uid 用户ID
     * @return 车辆ID集合
     */
    public List<Integer> selectVehicleByUid(int uid)
    {
        List<Integer> orgVids = new ArrayList<>();

        List<Integer> oids = organizationUserManagement.findOidByUid(uid);
        if(oids.size()>0)
        {
            orgVids = organizationVehicleManagement.findVidByOids(oids);
        }

        List<Integer> ownerVids = userVehicleManagement.findVidByUid(uid);

        for(Integer vid:ownerVids) {
            if(!orgVids.contains(vid)){
                orgVids.add(vid);
            }
        }

        return orgVids;
    }

    /**
     * 条件查询车辆
     * @param uid 用户ID
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
    public Page<VehicleEx> selectVehicle(int uid, String vin, String tboxsn, String vendor, String model, Date start_date,Date end_date,String license_plate,Integer t_flag, Integer currentPage,Integer pageSize)
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

        Page<VehicleEx> vehiclePage = new PageImpl<>(new ArrayList<>(),p,0);
        List<Integer> vids = selectVehicleByUid(uid);
        if(vids == null || vids.size()==0)
        {
            return vehiclePage;
        }
        vehiclePage = vehicleExRepository.selectVehicle(vids,vin,tboxsn,vendor,model,start_date,end_date,license_plate,t_flag,p);

        return vehiclePage;
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
    public Page<VehicleEx> selectVehicle(String vin, String tboxsn, String vendor, String model, Date start_date,Date end_date,String license_plate,Integer t_flag, Integer currentPage,Integer pageSize)
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

        return vehiclePage;
    }

}
