package com.hp.triclops.repository;

import com.hp.triclops.entity.OrganizationUserRelative;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@EnableJpaRepositories
public interface OrganizationUserRelativeRepository extends CrudRepository<OrganizationUserRelative, Integer> {

    List<OrganizationUserRelative> findByOid(int oid);

    List<OrganizationUserRelative> findByOidAndUid(int oid,int uid);

    @Query("select distinct ou.oid from OrganizationUserRelative ou where ou.uid = ?1 ")
    List<Integer> findOidByUid(int uid);

    @Query("select distinct ou.uid from OrganizationUserRelative ou where ou.oid in ?1 ")
    List<Integer> findUidByOids(List<Integer> oids);

    @Query("select count(*) from OrganizationUserRelative ou where ou.oid = ?1 ")
    int getOrgUserNum(int oid);
}
