package com.hp.triclops.repository;

import com.hp.triclops.entity.UserVehicleRelativeEx;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */
public interface UserVehicleRelativeExRepository extends CrudRepository<UserVehicleRelativeEx,Integer> {

    List<UserVehicleRelativeEx> findByVid(int vid);

    @Query("select uv from UserVehicleRelativeEx uv where uv.uid = ?1 and uv.vid = ?2 and uv.iflag = ?3")
    UserVehicleRelativeEx findOneReative(int uid, int vid, int iflag);

    @Query("select distinct uv.vid from UserVehicleRelativeEx uv where uv.uid = ?1")
    List<Integer> findVidByUid(int uid);

}
