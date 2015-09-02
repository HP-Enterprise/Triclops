package com.hp.triclops.repository;

import com.hp.triclops.entity.SlotInfo;
import com.hp.triclops.entity.User;
import com.hp.triclops.entity.Vehicle;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2015/8/25.
 */

@EnableJpaRepositories
public interface SlotRepository extends CrudRepository<SlotInfo, Long> {

    Iterable<SlotInfo> findByUid(String uid);

    SlotInfo findByUidAndSlotkey(String uid,String slotkey);

    Iterable<SlotInfo> findByUidAndSlotkeyStartingWith(String uid,String slotkey);
}
