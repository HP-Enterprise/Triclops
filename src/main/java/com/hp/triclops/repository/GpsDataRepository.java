package com.hp.triclops.repository;
import com.hp.triclops.entity.GpsData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by luj on 2015/9/28.
 */
public interface GpsDataRepository extends CrudRepository<GpsData, Long>{
    List<GpsData> findByVinAndSendingTime(String vin,Date sendingTime);

    @Query("select gd from GpsData gd where gd.vin = ?1 and gd.applicationId = 34 ORDER BY gd.sendingTime DESC")
    List<GpsData> findLatestByVin(String vin);

    //GpsData findTopByVinOrderBySendingTimeDesc(String vin);
}
