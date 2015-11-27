package com.hp.triclops.repository;


import com.hp.triclops.entity.Area;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2015/11/3.
 */
    @EnableJpaRepositories
    public interface AreaRepository extends CrudRepository<Area,Integer> {

    @Query("select a.city from Area a where a.province=?1")
    List<String> findAreaByProvince(String province);
    @Query("select a.province from Area a ")
    Set<String> findAllProvince();
}
