package com.hp.triclops.repository;

import com.hp.triclops.entity.OrganisationVehicleRelativeEx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@EnableJpaRepositories
public interface OrganisationVehicleRelativeExRepository extends CrudRepository<OrganisationVehicleRelativeEx,Integer> {

    @Query("select ov.vid from OrganisationVehicleRelativeEx ov where ov.oid = ?1 ")
    List<Integer> findVidsByOid(int oid);

    @Query("select ov.vid from OrganisationVehicleRelativeEx ov where ov.oid = ?1 ")
    Page<Integer> findVidsByOid(int oid, Pageable p);

    @Query("select count(*) from OrganisationVehicleRelativeEx ov where ov.oid = ?1 ")
    int getOrgVehicleNum(int oid);

    @Query("select ov from OrganisationVehicleRelativeEx ov where ov.oid = ?1 and ov.vid = ?2 ")
    OrganisationVehicleRelativeEx findByOidAndvid(int oid,int vid);

    Long deleteByOid(int oid);
}
