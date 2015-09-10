package com.hp.triclops.repository;

import com.hp.triclops.entity.SlotInfo;
import com.hp.triclops.entity.User;
import com.hp.triclops.entity.Vehicle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2015/8/25.
 */

@EnableJpaRepositories
public interface SlotRepository extends CrudRepository<SlotInfo, Long> {

    Iterable<SlotInfo> findByUid(int uid);

    SlotInfo findByUidAndSlotkey(int uid,String slotkey);

    @Query("select s.slotkey from SlotInfo s where s.uid=?1 And s.slotkey like ?2")
    List<String> findKeysByUidAndSlotkeyStartingWith(int uid,String slotkey);

    @Query("select s.slotkey from SlotInfo s where s.uid=?1")
    List<String> findKeysByUid(int uid);
}
