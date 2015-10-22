package com.hp.triclops.repository;

import com.hp.triclops.entity.Organization;
import com.hp.triclops.entity.Sysdict;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;


@EnableJpaRepositories
public interface OrganizationRepository extends CrudRepository<Organization, Long> {
    
    Organization findById(int id);

    Set<Organization> findByTypeKey(Sysdict typeKey);

    Organization findByOrgName(String orgName);

    Organization findByBreCode(String breCode);
}