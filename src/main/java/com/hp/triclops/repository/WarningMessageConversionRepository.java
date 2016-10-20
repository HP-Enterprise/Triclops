package com.hp.triclops.repository;

import com.hp.triclops.entity.TBoxParmSet;
import com.hp.triclops.entity.WarningMessageConversion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by luj on 2015/10/20.
 */
public interface WarningMessageConversionRepository extends CrudRepository<WarningMessageConversion, Long> {

    @Query("select wmc from WarningMessageConversion wmc")
    List<WarningMessageConversion> findAll();

}
