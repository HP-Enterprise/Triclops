package com.hp.triclops.repository;

import com.hp.triclops.entity.DriveBehaviorReport;
import com.hp.triclops.entity.DrivingBehaviorData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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







}
