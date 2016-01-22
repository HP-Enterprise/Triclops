package com.hp.triclops.management;

import com.hp.triclops.entity.OrganizationUserRelative;
import com.hp.triclops.repository.OrganizationUserRelativeRepository;
import com.hp.triclops.vo.OrganizationUserRelativeShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Teemol on 2016/1/22.
 */

@Component
public class OrganizationUserManagement {

    @Autowired
    OrganizationUserRelativeRepository organizationUserRelativeRepository;

    /**
     * 根据oid查询组织用户关系集合
     * @param oid 组织ID
     * @return 组织用户关系列表
     */
    public List<OrganizationUserRelativeShow> findByOid(int oid)
    {
        List<OrganizationUserRelative> list = organizationUserRelativeRepository.findByOid(oid);

        return list.stream().map(OrganizationUserRelativeShow::new).collect(Collectors.toList());
    }

    /**
     * 根据uid查询组织用户关系集合
     * @param uid 用户ID
     * @return 组织用户关系列表
     */
    public List<OrganizationUserRelativeShow> getByUid(int uid)
    {
        List<OrganizationUserRelative> list = organizationUserRelativeRepository.findByUid(uid);

        return list.stream().map(OrganizationUserRelativeShow::new).collect(Collectors.toList());
    }
}
