package com.hp.triclops.repository;

import com.hp.triclops.entity.Vehicle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/8/25.
 */

@EnableJpaRepositories
public interface VehicleRepository extends CrudRepository<Vehicle, String> {

    Vehicle findByVin(String vin);

    Vehicle findById(int id);

    @Query("select Ve from Vehicle Ve where Ve.vin = ?1 and Ve.tboxsn = ?2")
    Vehicle findByVinAndTbox(String vin, String tboxsn);

    @Query("select ve from Vehicle ve where ve.id > ?1 and ve.id <= ?2")
    List<Vehicle> findByStartIdAndEndId(int startId,int endId);

}
