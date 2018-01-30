package com.hp.triclops.repository;

import com.hp.triclops.entity.UserAdvice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface UserAdviceRepository extends CrudRepository<UserAdvice,Integer>,JpaSpecificationExecutor<UserAdvice> {

}
