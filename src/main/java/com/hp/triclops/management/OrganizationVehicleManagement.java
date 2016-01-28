package com.hp.triclops.management;

import com.hp.triclops.repository.OrganisationVehicleRelativeExRepository;
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
    public List<Integer> findVidByOids(int oid)
    {
        return organisationVehicleRelativeExRepository.findVidByOid(oid);
    }
}
