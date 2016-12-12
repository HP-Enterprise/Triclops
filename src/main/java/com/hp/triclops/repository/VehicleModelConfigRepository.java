package com.hp.triclops.repository;

import com.hp.triclops.entity.VehicleModelConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jackl on 2016/12/12.
 */
public interface VehicleModelConfigRepository extends CrudRepository<VehicleModelConfig,Integer> {

    VehicleModelConfig findByModelId(Short modelId);
    List<VehicleModelConfig> findByModelName(String modelName);
}
