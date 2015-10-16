package com.hp.triclops.repository;


import com.hp.triclops.entity.UserDevice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备信息数据接口
 */
@Repository
public interface DeviceRepository extends CrudRepository<UserDevice,Integer>{
    public List<UserDevice> findByDeviceId(String deviceId);

    @Query("select ud from UserDevice ud where ud.user.id=:userId")
    public List<UserDevice> findByUserId(@Param("userId") int userId);
}
