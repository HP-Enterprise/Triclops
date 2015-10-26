package com.hp.triclops.repository;

import com.hp.triclops.entity.TBox;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by luj on 2015/10/9.
 */
public interface TBoxRepository  extends CrudRepository<TBox, Long> {

    @Query("select tb from TBox tb where tb.vin = ?1 and tb.t_sn = ?2")
    TBox findByVinAndT_sn(String vin, String t_sn);

    TBox findById(int id);

    @Query("select tb from TBox tb where tb.t_sn = ?1")
    TBox findByT_sn(String t_sn);

    TBox findByVin(String vin);

}
