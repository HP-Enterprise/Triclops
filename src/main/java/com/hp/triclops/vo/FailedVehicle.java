package com.hp.triclops.vo;

/**
 * @author chenzhao
 * @Title: FailedVehicle
 * @ProjectName BriAir
 * @Description: 车辆保存失败
 * @date 2019-03-16 09:12
 */
public class FailedVehicle {
    private String vin;
    private String reason;

    public FailedVehicle(String vin, String reason) {
        this.vin = vin;
        this.reason = reason;
    }

    public FailedVehicle() {
    }

    @Override
    public String toString() {
        return "FailedVehicle{" +
                "vin='" + vin + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }

    public String getVin() {
        return vin;
    }

    public FailedVehicle setVin(String vin) {
        this.vin = vin;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public FailedVehicle setReason(String reason) {
        this.reason = reason;
        return this;
    }
}
