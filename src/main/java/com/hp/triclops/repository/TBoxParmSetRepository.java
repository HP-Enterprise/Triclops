package com.hp.triclops.repository;

import com.hp.triclops.entity.TBoxParmSet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luj on 2015/10/14.
 */
public interface TBoxParmSetRepository extends CrudRepository<TBoxParmSet, Long> {

    @Query("select  tps from TBoxParmSet tps where tps.vin=?1 And tps.eventId =?2 order by tps.sendingTime DESC")
    List<TBoxParmSet> findByVinAndEventId(String vin, Long eventId);

    @Query("select  tps from TBoxParmSet tps where tps.vin=?1  And tps.status=0 order by tps.sendingTime DESC")
    List<TBoxParmSet> getLatestOneByVin(String vin);
}
