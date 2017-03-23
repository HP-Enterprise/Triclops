package com.hp.triclops.repository;

import java.util.List;

import com.hp.triclops.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@EnableJpaRepositories
public interface UserRepository extends CrudRepository<User, Long> {

    User findByName(String name);

    User findById(int id);

    User findByPhone(String phone);

    List<User> findByIdAndPhone(int id,String phone);

    @Modifying
    @Query("update User u set u.name=?1,u.gender=?2,u.nick=?3,u.phone=?4,u.isVerified=?5,u.contacts=?6,u.contactsPhone=?7 where u.id=?8")
    User update(String name, Integer gender, String nick, String phone,int isVerified,String contacts,String contactsPhone,int id);

    @Modifying
    @Transactional
    @Query("update User u set u.lastDeviceId=?1 where u.id=?2")
    void updateDeviceid(String deviceid, int id);
}