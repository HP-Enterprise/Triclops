package com.hp.triclops.repository;

import com.hp.triclops.entity.FailureMessageData;
import com.hp.triclops.entity.RemoteControl;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by luj on 2015/10/12.
 */
public interface RemoteControlRepository extends CrudRepository<RemoteControl, Long> {

    RemoteControl findTopByVinAndSessionIdOrderBySendingTimeDesc(String vin,String sessionId);

    RemoteControl findByRefId(long refId);
}
