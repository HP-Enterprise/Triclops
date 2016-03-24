package com.hp.triclops.repository;

import com.hp.triclops.entity.TBoxEx;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Incar on 2016/3/24.
 */
public interface TBoxExRepository  extends CrudRepository<TBoxEx, Long> {

    Long deleteById(int id);

    TBoxEx findById(int id);
}
