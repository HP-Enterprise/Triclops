package com.hp.triclops.entity;

import com.hp.triclops.repository.OrganizationRepository;
import com.hp.triclops.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

/**
 * Created by liz on 2015/10/13.
 */
@Component
public class Org6S {

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization organization;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Org6S() {
    }

    /**
     * 构造有参函数
     * @param oid 组织ID
     */
    public Org6S(int oid){
       this.setOrganization(this.findOrgById(oid));
    }

    /**
     * 组织里面添加一辆车
     * @param v 被添加车辆
     */
    public void addVehicle(Vehicle6S v){
        Set<Vehicle> vehicleSet = this.organization.getVehicleSet();
        vehicleSet.add(v.getVehicle());
        this.organizationRepository.save(this.organization);
    }

    /**
     * 组织里面删除一辆车
     * @param v 被删除车辆
     */
    public void deleteVehicle(Vehicle6S v) throws Exception{
        throw new NotImplementedException();
    }

    /**
     * 根据组织ID查找组织
     * @param oid 组织ID
     * @return
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

    /**
     * 绑定车辆
     * 返回JSONResult
     */

    public boolean addVehicle(Vehicle6S v){
        Vehicle vehicle= v.getVehicle();
        Set<Organization> oset = vehicle.getOrganizationSet();
        if(!oset.contains(this.getOrganization())){
            oset.add(this.getOrganization());
            this.vehicleRepository.save(vehicle);
            return true;
        }
        return false;
    }
}
