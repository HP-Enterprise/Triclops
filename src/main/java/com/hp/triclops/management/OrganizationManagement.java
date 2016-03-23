package com.hp.triclops.management;

import com.hp.triclops.entity.OrganizationEx;
import com.hp.triclops.repository.OrganizationExRepository;
import com.hp.triclops.vo.OrganizationShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Teemol on 2016/1/22.
 */
@Component
public class OrganizationManagement {

    @Autowired
    OrganizationExRepository organizationExRepository;

    @Autowired
    OrganizationUserManagement organizationUserManagement;

    @Autowired
    OrganizationVehicleManagement organizationVehicleManagement;

    /**
     * 新增组织信息
     * @param organizationShow 组织信息
     * @return 新增的组织对象
     */
    public OrganizationShow createOrganization(OrganizationShow organizationShow)
    {
        OrganizationEx returnOrg = organizationExRepository.save(new OrganizationEx(organizationShow));
        return new OrganizationShow(returnOrg);
    }

    /**
     * 根据组织ID删除组织
     * @param oid 组织ID
     */
    public void deleteOrganization(int oid)
    {
        organizationExRepository.delete(oid);
    }

    /**
     * 修改组织信息
     * @param org 组组信息类
     * @return 修改后的组织信息
     */
    public OrganizationShow modifyOrganization(OrganizationShow org)
    {
        OrganizationEx organizationEx = new OrganizationEx(org);
        OrganizationEx returnOrg = organizationExRepository.save(organizationEx);
        return new OrganizationShow(returnOrg);
    }

    /**
     * 根据ID查询组织信息
     * @param id 组织ID
     * @return 组织信息
     */
    public OrganizationShow findById(int id)
    {
        OrganizationEx organizationEx = organizationExRepository.findById(id);
        if(organizationEx == null)
            return null;
        return new OrganizationShow(organizationEx);
    }

    public List<OrganizationShow> findByIds(List<Integer> oids)
    {
        if(oids==null || oids.size()==1)
        {
            oids.add(0);
        }
        List<OrganizationEx> organizationExs = organizationExRepository.findByIds(oids);
        List<OrganizationShow> returnList = organizationExs.stream().map(OrganizationShow::new).collect(Collectors.toList());

        return returnList;
    }


    /**
     * 根据组织简码查询
     * @param breCode 组织简码
     * @return 组织信息
     */
    public OrganizationShow findByBreCode(String breCode)
    {
        OrganizationEx organizationEx = organizationExRepository.findByBreCode(breCode);
        if(organizationEx == null)
        {
            return null;
        }
        return new OrganizationShow(organizationEx);
    }

    /**
     * 组织查询
     * @param uid 用户ID
     * @param orgName 组织名称
     * @param breCode 组织简码
     * @param type_key 组织类型
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 组织信息
     */
    public Page<OrganizationEx> selectOrganization(int uid, String orgName, String breCode, Integer type_key, Integer currentPage, Integer pageSize)
    {
        if(orgName!=null) orgName = "%" + orgName + "%";
        if(breCode!=null) breCode = "%" + breCode + "%";
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<OrganizationEx> orgPage = new PageImpl<>(new ArrayList<>(),p,0);
        List<Integer> oids = organizationUserManagement.findOidsByUid(uid);
        if(oids == null || oids.size()==0)
        {
            return orgPage;
        }
        orgPage = organizationExRepository.select(oids,orgName,breCode,type_key,p);
        return orgPage;
    }

    /**
     * 组织查询（超级管理员）
     * @param orgName 组织名称
     * @param breCode 组织简码
     * @param type_key 组织类型
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 组织信息
     */
    public Page<OrganizationEx> selectOrganization(String orgName, String breCode, Integer type_key, Integer currentPage, Integer pageSize)
    {
        if(orgName!=null) orgName = "%" + orgName + "%";
        if(breCode!=null) breCode = "%" + breCode + "%";
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<OrganizationEx> orgPage = organizationExRepository.select(orgName,breCode,type_key,p);
        return orgPage;
    }

    /**
     * 查询组织中的车辆数目
     * @param oid 组织ID
     * @return 车辆数目
     */
    public int getOrgVehicleNum(int oid)
    {
        return organizationVehicleManagement.getOrgVehicleNum(oid);
    }

    /**
     * 查询组织中的用户数目
     * @param oid 组织ID
     * @return 车辆数目
     */
    public int getOrgUserNum(int oid)
    {
        return organizationUserManagement.getOrgUserNum(oid);
    }

    /**
     * 将用户加入组织
     * @param oid 组织ID
     * @param uid 用户ID
     */
    public void addUserToOrg(int oid,int uid)
    {
        organizationUserManagement.addUser(oid,uid);
    }
}
