package com.hp.triclops.repository;

import com.hp.triclops.entity.UserEx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/26.
 */

@EnableJpaRepositories
public interface UserExRepository extends CrudRepository<UserEx, String> {

    @Query("select ue from UserEx ue " +
            "where ue.id in ?1 " +
            "and (?2 is null or ue.name like ?2) " +
            "and (?3 is null or ue.gender = ?3) " +
            "and (?4 is null or ue.nick like ?4) " +
            "and (?5 is null or ue.phone like ?5) " +
            "and (?6 is null or ue.isVerified = ?6) " )
    Page<UserEx> selectUser(List<Integer> uids, String name, Integer gender, String nick, String phone, Integer isVerified, Pageable p);

    @Query("select ue from UserEx ue " +
            "where (?1 is null or ue.name like ?1) " +
            "and (?2 is null or ue.gender = ?2) " +
            "and (?3 is null or ue.nick like ?3) " +
            "and (?4 is null or ue.phone like ?4) " +
            "and (?5 is null or ue.isVerified = ?5) " )
    Page<UserEx> selectUser(String name, Integer gender, String nick, String phone, Integer isVerified, Pageable p);
}
