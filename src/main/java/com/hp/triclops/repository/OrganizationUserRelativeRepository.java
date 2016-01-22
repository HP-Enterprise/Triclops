package com.hp.triclops.repository;

import com.hp.triclops.entity.OrganizationUserRelative;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@EnableJpaRepositories
public interface OrganizationUserRelativeRepository extends CrudRepository<OrganizationUserRelative, Integer> {

    List<OrganizationUserRelative> findByOid(int oid);

    List<OrganizationUserRelative> findByUid(int uid);
}
