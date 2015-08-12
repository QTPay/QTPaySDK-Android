package com.example.qianfangdemo.entity;

import java.io.Serializable;

/**
 * Created by Yang on 15/5/7.
 */
public class SubBank implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SubBank{" +
                "name='" + name + '\'' +
                '}';
    }
}
