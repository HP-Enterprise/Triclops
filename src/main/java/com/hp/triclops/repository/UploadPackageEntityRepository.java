package com.hp.triclops.repository;

import com.hp.triclops.entity.UploadPackageEntity;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

@Configurable
@EnableJpaRepositories
public interface UploadPackageEntityRepository extends CrudRepository<UploadPackageEntity,Integer> {

    UploadPackageEntity findById(int id);

    @Query("select up from UploadPackageEntity up where up.model = ?1 and up.id = ?2")
    UploadPackageEntity findByModelAndId(int model, int id);

    @Query("select up from UploadPackageEntity up where up.model = ?1 and up.version = ?2")
    UploadPackageEntity findByModelAndVersion(int model, String version);

}
