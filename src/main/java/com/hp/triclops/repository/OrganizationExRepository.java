package com.hp.triclops.repository;

import com.hp.triclops.entity.OrganizationEx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/28.
 */
public interface OrganizationExRepository extends CrudRepository<OrganizationEx, Integer> {

    @Query("select o from OrganizationEx o " +
            "where (?1 is null or o.orgName like ?1) " +
            "and (?2 is null or o.breCode like ?2) " +
            "and (?3 is null or o.typeKey = ?3) " )
    Page<OrganizationEx> select(String orgName, String breCode, Integer type_key, Pageable p);

    @Query("select o from OrganizationEx o " +
            "where o.id in ?1 " +
            "and (?2 is null or o.orgName like ?2) " +
            "and (?3 is null or o.breCode like ?3) " +
            "and (?4 is null or o.typeKey = ?4) " )
    Page<OrganizationEx> select(List<Integer> oids, String orgName, String breCode, Integer type_key, Pageable p);
}
