package com.hp.triclops.repository;

import com.hp.triclops.entity.DriveBehaviorReport;
import com.hp.triclops.entity.DrivingBehaviorData;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jackl on 2017/01/12.
 */
public interface DriveBehaviorReportRepository extends CrudRepository<DriveBehaviorReport, Long> {

}
