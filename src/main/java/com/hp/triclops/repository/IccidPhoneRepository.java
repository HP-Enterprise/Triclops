package com.hp.triclops.repository;


import com.hp.triclops.entity.IccidPhone;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jackl on 2016/9/9.
 */
@EnableJpaRepositories
public interface IccidPhoneRepository  extends CrudRepository<IccidPhone,Integer> {

    IccidPhone findByIccid(String iccid);

    IccidPhone findByPhone(String phone);

}
