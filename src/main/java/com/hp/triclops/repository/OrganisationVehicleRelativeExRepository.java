package com.hp.triclops.repository;

import com.hp.triclops.entity.OrganisationVehicleRelativeEx;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@EnableJpaRepositories
public interface OrganisationVehicleRelativeExRepository extends CrudRepository<OrganisationVehicleRelativeEx,Integer> {

    @Query("select distinct ov.vid from OrganisationVehicleRelativeEx ov where ov.oid in ?1 ")
    List<Integer> findVidByOids(List<Integer> oids);

    @Query("select ov.vid from OrganisationVehicleRelativeEx ov where ov.oid = ?1 ")
    List<Integer> findVidByOid(int oid);

    @Query("select count(*) from OrganisationVehicleRelativeEx ov where ov.oid = ?1 ")
    int getOrgVehicleNum(int oid);
}
