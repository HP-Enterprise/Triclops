package com.hp.triclops.management;

import com.hp.triclops.entity.OrganizationUserRelative;
import com.hp.triclops.repository.OrganizationUserRelativeRepository;
import com.hp.triclops.vo.OrganizationUserRelativeShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
     * 查询组织集合中的成员ID
     * @param oids 组织ID集合
     * @return 用户ID集合
     */
    public List<Integer> findUidByOids(List<Integer> oids)
    {
        List<Integer> list = new ArrayList<>();
        if(oids==null || oids.size()==0)
        {
            return list;
        }
        list = organizationUserRelativeRepository.findUidByOids(oids);

        return list;
    }

    /**
     * 根据uid查询用户所属组织集合
     * @param uid 用户ID
     * @return 组织ID集合
     */
    public List<Integer> findOidsByUid(int uid)
    {
        List<Integer> list = organizationUserRelativeRepository.findOidByUid(uid);

        return list;
    }

    /**
     * 查询组织中的成员数目
     * @param oid 组织ID
     * @return 成员数目
     */
    public int getOrgUserNum(int oid)
    {
        return organizationUserRelativeRepository.getOrgUserNum(oid);
    }

    /**
     * 查询用户与组织的关系
     * @param oid 组织ID
     * @param uid 用户ID
     * @return 组织用户关系集合
     */
    public List<OrganizationUserRelativeShow> findByOidAndUid(int oid,int uid)
    {
        List<OrganizationUserRelative> list = organizationUserRelativeRepository.findByOidAndUid(oid,uid);

        return list.stream().map(OrganizationUserRelativeShow::new).collect(Collectors.toList());
    }
}
