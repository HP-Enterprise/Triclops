package com.hp.triclops.management;

import com.hp.triclops.entity.OrganisationVehicleRelativeEx;
import com.hp.triclops.repository.OrganisationVehicleRelativeExRepository;
import com.hp.triclops.vo.OrganisationVehicleRelativeExShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@Component
public class OrganizationVehicleManagement {

    @Autowired
    OrganisationVehicleRelativeExRepository organisationVehicleRelativeExRepository;

    /**
     * 查询组织集合中的车辆
     * @param oids 组织ID集合
     * @return 车辆ID集合
     */
    public List<Integer> findVidByOids(List<Integer> oids)
    {
        List<Integer> list = new ArrayList<>();
        if(oids==null || oids.size()==0)
        {
            return list;
        }
        list = organisationVehicleRelativeExRepository.findVidByOids(oids);

        return list;
    }

    /**
     * 查询组织中的车辆
     * @param oid 组织ID
     * @return 车辆ID集合
     */
    public List<Integer> findVidByOids(int oid,Integer currentPage,Integer pageSize)
    {
        return organisationVehicleRelativeExRepository.findVidByOid(oid);
    }

    /**
     * 查询组织中的车辆数目
     * @param oid 组织ID
     * @return 车辆数目
     */
    public int getOrgVehicleNum(int oid)
    {
        return organisationVehicleRelativeExRepository.getOrgVehicleNum(oid);
    }

    /**
     * 查询组织车辆关系
     * @param oid 组织ID
     * @param vid 车辆ID
     * @return 组织车辆关系
     */
    public OrganisationVehicleRelativeExShow findByOidAndvid(int oid, int vid)
    {
        OrganisationVehicleRelativeEx relative = organisationVehicleRelativeExRepository.findByOidAndvid(oid,vid);
        if(relative == null)
            return null;
        return new OrganisationVehicleRelativeExShow(relative);
    }

    /**
     * 向组织中增加车辆
     * @param oid 组织ID
     * @param vid 车辆ID
     */
    public void addVehicle(int oid,int vid)
    {
        OrganisationVehicleRelativeEx organisationVehicleRelativeEx = new OrganisationVehicleRelativeEx(oid,vid);
        organisationVehicleRelativeExRepository.save(organisationVehicleRelativeEx);
    }

    /**
     * 从组织移除车辆
     * @param id 组织车辆关系ID
     * @return 移除状态  0：车辆不在组织中  1：移除成功
     */
    public void deleteVehicle(int id)
    {
        organisationVehicleRelativeExRepository.delete(id);
    }
}
