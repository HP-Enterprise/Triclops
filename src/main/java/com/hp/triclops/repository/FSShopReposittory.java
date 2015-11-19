package com.hp.triclops.repository;

import com.hp.triclops.entity.Shop4s;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2015/11/18.
 */
@EnableJpaRepositories
public interface FSShopReposittory extends CrudRepository<Shop4s,Long> {
    Shop4s findByCityid(int cityid);
}
