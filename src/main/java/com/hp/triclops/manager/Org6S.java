package com.hp.triclops.manager;

import com.hp.triclops.entity.Organization;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.OrganizationRepository;
import org.springframework.context.ApplicationContext;

import java.util.Set;

/**
 * Created by liz on 2015/10/13.
 */
public class Org6S {

    private OrganizationRepository organizationRepository;

    private Organization organization;

    private int oid;

    public ApplicationContext appContext;

    public Org6S() {
    }

    /**
     * 构造有参函数
     * @param oid 组织ID
     */
    public Org6S(int oid){
        this.setOid(oid);
    }

    /**
     * 手动注入Repository
     * @param appContext ApplicationContext
     */
    public void setAppCtxAndInit(ApplicationContext appContext){
        this.appContext = appContext;
        this.organizationRepository = this.appContext.getBean(OrganizationRepository.class);
        this.setOrganization(this.findOrgById(this.getOid()));
    }


    /**
     * 组织里面添加一辆车
     * @param v 被添加车辆
     * @return  执行完后的Vehicle6S
     */
    public Vehicle6S addVehicle(Vehicle6S v){
        if(!isBinding(v)) {
            Set<Vehicle> vehicleSet = this.organization.getVehicleSet();
            vehicleSet.add(v.getVehicle());
            this.organizationRepository.save(this.organization);
        }
        return v;
    }

    /**
     * 判断车辆是否已经绑定
     * @param  v 添加车辆
     * @return True or False
     */

    public boolean isBinding(Vehicle6S v){
        Set<Vehicle> vehicleSet = this.organization.getVehicleSet();
        if(vehicleSet.contains(v.getVehicle())) return true;//true为已经绑定，fasle为没有绑定
        return false;
    }

    /**
     * 根据组织ID查找组织
     * @param oid 组织ID
     * @return  查找到的组织
     */
    public Organization findOrgById(int oid){
        return this.organizationRepository.findById(oid);
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }


}
