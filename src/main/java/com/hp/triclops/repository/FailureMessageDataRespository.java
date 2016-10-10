package com.hp.triclops.repository;

import com.hp.triclops.entity.FailureMessageData;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by luj on 2015/11/13.
 */
public interface FailureMessageDataRespository extends CrudRepository<FailureMessageData, Long> {
    //FailureMessageData findTopByVinOrderBySendingTimeDesc(String vin);

    FailureMessageData findTopByVinOrderByReceiveTimeDescIdDesc(String vin);
}
