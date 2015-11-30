package com.hp.triclops.repository;

import com.hp.triclops.entity.DiagnosticData;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * Created by yuh on 2015/11/30.
 */
@Repository
public class DiagnosticDataRepositoryDao {
    @PersistenceContext
    EntityManager em;

    public DiagnosticData findByDiaId(Long id){
        em.clear();
        DiagnosticData resultData = em.find(DiagnosticData.class,id);
        return  resultData;
    }
}
