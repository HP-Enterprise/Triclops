package com.hp.triclops.repository;

import com.hp.triclops.entity.UserVehicleRelativeEx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */
public interface UserVehicleRelativeExRepository extends CrudRepository<UserVehicleRelativeEx,Integer> {

    List<UserVehicleRelativeEx> findByVid(int vid);

    List<UserVehicleRelativeEx> findByUid(int uid);

    @Query("select uv from UserVehicleRelativeEx uv where uv.uid = ?1 and uv.vid = ?2 and uv.iflag = ?3")
    UserVehicleRelativeEx findOneReative(int uid, int vid, int iflag);

    @Query("select uv from UserVehicleRelativeEx uv")
    Page<UserVehicleRelativeEx> findReative(Pageable p);

    @Query("select uv from UserVehicleRelativeEx uv where uv.uid in ?1 and uv.vid in ?2")
    Page<UserVehicleRelativeEx> findReative(List<Integer> uids, List<Integer> vids, Pageable p);

    @Query("select uv from UserVehicleRelativeEx uv where uv.uid in ?1")
    Page<UserVehicleRelativeEx> findReativeByUids(List<Integer> uids, Pageable p);

    @Query("select uv from UserVehicleRelativeEx uv where uv.vid in ?1")
    Page<UserVehicleRelativeEx> findReativeByVids(List<Integer> vids, Pageable p);
}
