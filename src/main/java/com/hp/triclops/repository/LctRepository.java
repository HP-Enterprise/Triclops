package com.hp.triclops.repository;

import com.hp.triclops.entity.Lct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jackl on 2016/11/15.
 */
@Repository
public interface LctRepository extends CrudRepository<Lct,Long> {
    Lct findByImei(String imei);

    Long deleteById(int id);
}
