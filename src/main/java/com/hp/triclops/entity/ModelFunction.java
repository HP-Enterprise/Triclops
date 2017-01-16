package com.hp.triclops.entity;

import java.io.Serializable;

/**
 * Created by jackl on 2017/1/16.
 */
public class ModelFunction implements Serializable {
    String model;
    String func;

    public ModelFunction() {
    }

    public ModelFunction(String model, String func) {
        this.model = model;
        this.func = func;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }
}
