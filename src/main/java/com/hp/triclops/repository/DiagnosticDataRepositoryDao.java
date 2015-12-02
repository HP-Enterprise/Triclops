package com.hp.triclops.repository;

import com.hp.triclops.entity.DiagnosticData;
import org.hibernate.Session;
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
       // em.clear();
        Session session = em.unwrap(org.hibernate.Session.class);
        DiagnosticData resultData= em.find(DiagnosticData.class, id);
        session.evict(resultData);
        return  resultData;
    }
}
