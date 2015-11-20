package com.hp.triclops.repository;

import com.hp.triclops.entity.Shop4s;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2015/11/18.
 */
@EnableJpaRepositories
public interface FSShopReposittory extends CrudRepository<Shop4s,Long> {
    List<Shop4s> findByCityid(int cityid);
}
