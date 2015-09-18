package com.hp.triclops.repository;

import com.hp.triclops.entity.OrganisationVehicleRelatived;
import com.hp.triclops.entity.Organization;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


@Configurable
@EnableJpaRepositories
public interface OrganisationVehicleRelativedRepository extends CrudRepository<OrganisationVehicleRelatived,Integer>{

    List<OrganisationVehicleRelatived> findByOrg(Organization organization);
}
