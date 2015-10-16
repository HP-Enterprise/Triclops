package com.hp.triclops.repository;


import com.hp.triclops.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 消息数据处理接口
 */
@Repository
public interface MessageRepository extends CrudRepository<Message,Integer> {
    Message findById(int id);
}
