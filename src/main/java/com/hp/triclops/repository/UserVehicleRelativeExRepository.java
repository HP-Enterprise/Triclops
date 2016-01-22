package com.hp.triclops.repository;

import com.hp.triclops.entity.UserVehicleRelativeEx;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */
public interface UserVehicleRelativeExRepository extends CrudRepository<UserVehicleRelativeEx,Integer> {

    @Query("select distinct uv.vid from UserVehicleRelativeEx uv where uv.uid = ?1")
    List<Integer> findVidByUid(int uid);

}
