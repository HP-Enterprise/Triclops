package com.hp.triclops.repository;

import com.hp.triclops.entity.Organization;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;


@EnableJpaRepositories
public interface OrganizationRepository extends CrudRepository<Organization, Long> {

}