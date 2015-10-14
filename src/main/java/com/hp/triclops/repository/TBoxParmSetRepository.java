package com.hp.triclops.repository;

import com.hp.triclops.entity.TBoxParmSet;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by luj on 2015/10/14.
 */
public interface TBoxParmSetRepository extends CrudRepository<TBoxParmSet, Long> {

    TBoxParmSet findByVinAndEventId(String vin, Long eventId);
}
