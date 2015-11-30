package com.hp.triclops.repository;

import com.hp.triclops.entity.DiagnosticData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by luj on 2015/10/29.
 */
public interface DiagnosticDataRepository extends CrudRepository<DiagnosticData, Long> {
    DiagnosticData findByVinAndEventId(String vin,long eventId);

    @Query("select  dd.hasAck from DiagnosticData dd where dd.id=?1")
    Short findHasAckById(long Id);
}
