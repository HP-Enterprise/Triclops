package com.hp.triclops.repository;

import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.entity.Vehicle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2015/8/25.
 */

@Configurable
@EnableJpaRepositories
public interface UserVehicleRelativedRepository extends CrudRepository<UserVehicleRelatived, Integer> {
    List<UserVehicleRelatived> findByVin(Vehicle vin);

    UserVehicleRelatived findById(int id);

    List<UserVehicleRelatived> findByUid(User user);

    List<UserVehicleRelatived> findByParentuser(User user);

    @Query("select Uvr from UserVehicleRelatived Uvr where Uvr.uid = ?1 and Uvr.vin = ?2 and Uvr.iflag = ?3 and Uvr.parentuser = ?4")
    UserVehicleRelatived findOneRelative(User userid,Vehicle vin,int iflag,User parentuserid);
}
