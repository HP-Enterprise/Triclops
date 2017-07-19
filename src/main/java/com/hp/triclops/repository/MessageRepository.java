package com.hp.triclops.repository;


import com.hp.triclops.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 消息数据处理接口
 */
@Repository
public interface MessageRepository extends CrudRepository<Message,Integer> {
    Message findById(int id);

    @Query("select r from Message r where r.id = (select max(rd.id) from Message rd where rd.vin = ?1 and rd.pType = ?2)")
    Message getLatestOneByVin(String vin, Integer pType);
}
