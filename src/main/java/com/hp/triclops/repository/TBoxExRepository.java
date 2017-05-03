package com.hp.triclops.repository;

import com.hp.triclops.entity.TBoxEx;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Incar on 2016/3/24.
 */
public interface TBoxExRepository  extends CrudRepository<TBoxEx, Long> {

    Long deleteById(int id);

    TBoxEx findById(int id);

    @Query("select tb from TBoxEx tb where tb.t_sn = ?1")
    TBoxEx findByT_sn(String t_sn);

    @Query("select tb from TBoxEx tb where tb.mobile = ?1")
    TBoxEx findByMobile(String mobile);

    TBoxEx findByIdAndVid(int id,int vid);
}
