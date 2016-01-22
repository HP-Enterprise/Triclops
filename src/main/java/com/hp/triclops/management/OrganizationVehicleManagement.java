package com.hp.triclops.management;

import com.hp.triclops.repository.OrganisationVehicleRelativeExRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        List<Integer> list = organisationVehicleRelativeExRepository.findVidByOids(oids);

        return list;
    }
}
