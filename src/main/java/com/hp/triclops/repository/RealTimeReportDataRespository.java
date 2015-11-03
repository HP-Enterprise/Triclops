package com.hp.triclops.repository;

import com.hp.triclops.entity.RealTimeReportData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luj on 2015/10/8.
 */
public interface RealTimeReportDataRespository extends CrudRepository<RealTimeReportData, Long> {

    @Query("select rd from RealTimeReportData rd where rd.vin = ?1 ORDER BY rd.sendingTime DESC")
    List<RealTimeReportData> findLatestOneByVin(String vin);
}
