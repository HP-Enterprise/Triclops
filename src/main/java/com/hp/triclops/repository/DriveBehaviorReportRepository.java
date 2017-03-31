package com.hp.triclops.repository;

import com.hp.triclops.entity.DriveBehaviorReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jackl on 2017/01/12.
 */
public interface DriveBehaviorReportRepository extends CrudRepository<DriveBehaviorReport, Long> {

    @Query("select dbr from DriveBehaviorReport dbr where dbr.type=1 and dbr.vin = ?1 and dbr.year =?2 and dbr.dateString=?3")
    DriveBehaviorReport findByVinAndYearAndDateString(String vin,String year,String dataString);

    @Query("select dbr from DriveBehaviorReport dbr where dbr.type=2 and dbr.vin = ?1 and dbr.year =?2 and dbr.week=?3")
    DriveBehaviorReport findByVinAndYearAndWeek(String vin,String year,Short week);

    @Query("select dbr from DriveBehaviorReport dbr where dbr.type=3 and dbr.vin = ?1 and dbr.year =?2 and dbr.month=?3")
    DriveBehaviorReport findByVinAndYearAndMonth(String vin,String year,Short month);

    @Query("select dbr from DriveBehaviorReport dbr where dbr.type=1 and dbr.year =?1 and dbr.dateString=?2")
    List<DriveBehaviorReport> findByYearAndDateString(String year, String dataString);

    @Query("select dbr from DriveBehaviorReport dbr where dbr.type=2 and dbr.year =?1 and dbr.week=?2")
    List<DriveBehaviorReport> findByYearAndWeek(String year, Short week);

    @Query("select dbr from DriveBehaviorReport dbr where dbr.type=3 and dbr.year =?1 and dbr.month=?2")
    List<DriveBehaviorReport> findByYearAndMonth(String year, Short month);

}
