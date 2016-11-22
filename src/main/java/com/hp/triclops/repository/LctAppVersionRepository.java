package com.hp.triclops.repository;

import com.hp.triclops.entity.LctAppVersion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jackl on 2016/11/15.
 */
@Repository
public interface LctAppVersionRepository extends CrudRepository<LctAppVersion,Long> {


  /*  @Query("select av from AppVersion av where av.appId = ?1 and av.version > ?2 order by av.version")
  //  AppVersion findLatestApp(String appId,String version);
*/

    LctAppVersion findTopByAppIdAndVersionGreaterThanOrderByPublishTimeDesc(String appId, String version);



}
