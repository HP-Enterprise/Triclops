package com.hp.triclops.repository;

import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.entity.Vehicle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2015/8/25.
 */

@Configurable
@EnableJpaRepositories
public interface UserVehicleRelativedRepository extends CrudRepository<UserVehicleRelatived, Integer> {
    List<UserVehicleRelatived> findByVid(Vehicle vid);

    @Query("select Uvr from UserVehicleRelatived Uvr where Uvr.iflag=1  and Uvr.vid = ?1")
    List<UserVehicleRelatived> findOwnerByVid(Vehicle vid);

    UserVehicleRelatived findById(int id);

    List<UserVehicleRelatived> findByUid(User user);

    List<UserVehicleRelatived> findByParentuser(User user);

    @Query("select Uvr from UserVehicleRelatived Uvr where Uvr.uid = ?1 and Uvr.vid = ?2 and Uvr.iflag = ?3 and Uvr.parentuser = ?4")
    UserVehicleRelatived findOneReative(User userid, Vehicle vid, int iflag, User parentuserid);

    @Query("select Uvr from UserVehicleRelatived Uvr where Uvr.uid = ?1 and Uvr.vid = ?2 and Uvr.iflag = ?3")
    UserVehicleRelatived findOneReative(User userid, Vehicle vid, int iflag);

    @Query("select Uvr from UserVehicleRelatived Uvr where Uvr.uid = ?1 and Uvr.vid = ?2")
    UserVehicleRelatived findOneReative(User userid, Vehicle vid);

    @Modifying
    @Query("update UserVehicleRelatived uvr set uvr.uid = ?1, uvr.vid = ?2, uvr.vflag = ?3, uvr.iflag = ?4 where uvr.id = ?5")
    void update(User user, Vehicle vehicle, int vflag, int iflag, int id);
}
