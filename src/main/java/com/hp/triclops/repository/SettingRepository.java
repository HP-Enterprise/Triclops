package com.hp.triclops.repository;

import com.hp.triclops.entity.Setting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting,Integer> {

    @Query("select s from Setting s where s.type = ?1 and s.code = ?2")
    Setting findByTypeAndCode(String type, String code);

    @Query("select s from Setting s where s.type = ?1")
    List<Setting> findByType(String type);
}
