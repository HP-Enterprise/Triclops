package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by jackl on 2016/12/12.
 */

@Entity
@Table(name = "t_vehicle_model_config")
public class VehicleModelConfig {
    private int id;
    private Short modelId;
    private String modelName;
    private String function;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Short getModelId() {
        return modelId;
    }

    public void setModelId(Short modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
