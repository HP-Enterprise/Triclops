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

    Vehicle findByTboxsn(String tboxsn);

    @Query("select Ve from Vehicle Ve where Ve.vin = ?1 and Ve.tboxsn = ?2")
    Vehicle findByVinAndTbox(String vin, String tboxsn);

    @Query("select ve from Vehicle ve where ve.id > ?1 and ve.id <= ?2")
    List<Vehicle> findByStartIdAndEndId(int startId,int endId);
//
//    @Query("select Ve from Vehicle Ve " +
//            "where (?1 is null or Ve.vin like %?1) " +
//            "and (?2 is null or Ve.tboxsn like %?2) " +
//            "and (?3 is null or Ve.vendor like %?3) " +
//            "and (?4 is null or Ve.model like %?4) " +
//            "and (?5 is null or Ve.product_date>=?5) " +
//            "and (?6 is null or Ve.product_date<=?6) " +
//            "and (?7 is null or Ve.license_plate like %?7) " +
//            "and (?8 is null or Ve.t_flag=?8) " +
//            "order by (?9 is null or ?9)")
//    List<Vehicle> selectVehicle(String vin, String tboxsn, String vendor, String model, Date start_date, Date end_date, String license_plate, int t_flag, String orderByProperty, Pageable p);
//
}
