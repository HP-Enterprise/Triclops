package com.hp.triclops.repository;

import com.hp.triclops.entity.VehicleEx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@EnableJpaRepositories
public interface VehicleExRepository extends CrudRepository<VehicleEx, String> {

    VehicleEx findById(int id);

    VehicleEx findByVin(String vin);

    VehicleEx findByTboxsn(String tboxsn);

    @Query("select Ve from VehicleEx Ve where Ve.vin = ?1 and Ve.tboxsn = ?2")
    VehicleEx findByVinAndTbox(String vin, String tboxsn);

    @Query("select Ve from VehicleEx Ve where Ve.id in ?1" )
    Page<VehicleEx> findByVids(List<Integer> vids,Pageable p);

    @Query("select Ve from VehicleEx Ve " +
           "where Ve.id in ?1 " +
           "and (?2 is null or Ve.vin like ?2) " +
           "and (?3 is null or Ve.tboxsn like ?3) " +
           "and (?4 is null or Ve.vendor like ?4) " +
           "and (?5 is null or Ve.model like ?5) " +
           "and (?6 is null or Ve.product_date >= ?6) " +
           "and (?7 is null or Ve.product_date <= ?7) " +
           "and (?8 is null or Ve.license_plate like ?8) " +
           "and (?9 is null or Ve.t_flag=?9) " )
    List<VehicleEx> selectVehicle(List<Integer> vids,String vin,String tboxsn,String vendor,String model,Date start_date,Date end_date,String license_plate,Integer t_flag);

    @Query("select Ve from VehicleEx Ve " +
            "where Ve.id not in ?1 " +
            "and (?2 is null or Ve.vin like ?2) " +
            "and (?3 is null or Ve.tboxsn like ?3) " +
            "and (?4 is null or Ve.vendor like ?4) " +
            "and (?5 is null or Ve.model like ?5) " +
            "and (?6 is null or Ve.product_date >= ?6) " +
            "and (?7 is null or Ve.product_date <= ?7) " +
            "and (?8 is null or Ve.license_plate like ?8) " +
            "and (?9 is null or Ve.t_flag=?9) " )
    Page<VehicleEx> selectVehicleAbsent(List<Integer> vids,String vin,String tboxsn,String vendor,String model,Date start_date,Date end_date,String license_plate,Integer t_flag,Pageable p);


    @Query("select Ve from VehicleEx Ve " +
            "where (?1 is null or Ve.vin like ?1) " +
            "and (?2 is null or Ve.tboxsn like ?2) " +
            "and (?3 is null or Ve.vendor like ?3) " +
            "and (?4 is null or Ve.model like ?4) " +
            "and (?5 is null or Ve.product_date >= ?5) " +
            "and (?6 is null or Ve.product_date <= ?6) " +
            "and (?7 is null or Ve.license_plate like ?7) " +
            "and (?8 is null or Ve.t_flag=?8) " )
    Page<VehicleEx> selectVehicle(String vin, String tboxsn, String vendor, String model, Date start_date, Date end_date, String license_plate, Integer t_flag, Pageable p);
}