package com.hp.triclops.repository;

import com.hp.triclops.entity.WarningMessageData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luj on 2015/10/8.
 */
@Repository
public interface WarningMessageDataRespository extends CrudRepository<WarningMessageData, Long> {


    //select * from t_data_warning_message where vin ='' order by sending_time desc LIMIT 1;
    //List<WarningMessageData> findTop1ByVinOrderBySendingTimeDesc(String vin);

    List<WarningMessageData> findTop1ByVinOrderByReceiveTimeDesc(String vin);
}
