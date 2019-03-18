package com.hp.triclops.management;

import com.hp.triclops.entity.OrganisationVehicleRelativeEx;
import com.hp.triclops.repository.OrganisationVehicleRelativeExRepository;
import com.hp.triclops.vo.FailedVehicle;
import com.hp.triclops.vo.OrganisationVehicleRelativeExShow;
import com.hp.triclops.vo.VehicleExShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Teemol on 2016/1/22.
 */

@Component
public class OrganizationVehicleManagement {

    @Autowired
    OrganisationVehicleRelativeExRepository organisationVehicleRelativeExRepository;

    @Autowired
    VehicleManagement vehicleManagement;

    /**
     * 向组织中增加车辆
     *
     * @param oid 组织ID
     * @param vid 车辆ID
     */
    public void addVehicle(int oid, int vid) {
        OrganisationVehicleRelativeEx organisationVehicleRelativeEx = new OrganisationVehicleRelativeEx(oid, vid);
        organisationVehicleRelativeExRepository.save(organisationVehicleRelativeEx);
    }

    /**
     * 从组织移除车辆
     *
     * @param id 组织车辆关系ID
     */
    public void removeVehicle(int id) {
        organisationVehicleRelativeExRepository.delete(id);
    }

    /**
     * 移除组织中所有车辆
     *
     * @param oid 组织ID
     */
    public void removeAllVehicle(int oid) {
        organisationVehicleRelativeExRepository.deleteByOid(oid);
    }

    /**
     * 查询组织中的车辆
     *
     * @param oid 组织ID
     * @return 车辆ID集合
     */
    public List<Integer> findVidsByOid(int oid) {
        List<Integer> list = organisationVehicleRelativeExRepository.findVidsByOid(oid);

        return list;
    }

    /**
     * 分页查询组织中的车辆
     *
     * @param oid         组织ID
     * @param currentPage 当前页
     * @param pageSize    页面大小
     * @return 车辆ID集合
     */
    public Page<Integer> findVidsByOid(int oid, Integer currentPage, Integer pageSize) {
        currentPage = currentPage == null ? 1 : currentPage;
        currentPage = currentPage <= 0 ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;
        pageSize = pageSize <= 0 ? 10 : pageSize;
        Pageable p = new PageRequest(currentPage - 1, pageSize);

        Page<Integer> vidsPage = organisationVehicleRelativeExRepository.findVidsByOid(oid, p);

        return vidsPage;
    }

    /**
     * 查询组织中的车辆数目
     *
     * @param oid 组织ID
     * @return 车辆数目
     */
    public int getOrgVehicleNum(int oid) {
        return organisationVehicleRelativeExRepository.getOrgVehicleNum(oid);
    }

    /**
     * 查询组织车辆关系
     *
     * @param oid 组织ID
     * @param vid 车辆ID
     * @return 组织车辆关系
     */
    public OrganisationVehicleRelativeExShow findByOidAndvid(int oid, int vid) {
        OrganisationVehicleRelativeEx relative = organisationVehicleRelativeExRepository.findByOidAndvid(oid, vid);
        if (relative == null)
            return null;
        return new OrganisationVehicleRelativeExShow(relative);
    }

    /**
     * 向组织批量插入车辆
     *
     * @param vehicleList 车辆列表
     * @param oid         组织ID
     * @return 插入失败列表
     */
    public List<FailedVehicle> addVehicleList(List<String> vehicleList, int oid) {
        List<FailedVehicle> failedList = new ArrayList();
        for (String vin : vehicleList) {
            // 根据vin查询车辆信息
            VehicleExShow vehicleExShow = vehicleManagement.findByVin(vin);

            if (Objects.isNull(vehicleExShow)) {
                failedList.add(new FailedVehicle(vin, "车辆不存在"));
            } else {
                try {
                    this.addVehicle(oid, vehicleExShow.getId());
                } catch (org.hibernate.exception.ConstraintViolationException e) {
                    e.printStackTrace();
                    failedList.add(new FailedVehicle(vin, "车辆已归属该组织"));
                }
            }
        }
        return failedList;
    }
}
