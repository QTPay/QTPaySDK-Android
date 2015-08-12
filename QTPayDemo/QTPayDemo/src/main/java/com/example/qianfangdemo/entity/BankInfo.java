package com.example.qianfangdemo.entity;

import java.io.Serializable;

/**
 * Created by Yang on 15/5/7.
 */
public class BankInfo implements Serializable{

    private String headbankid;
    private String headbankname;
    private String iscommon;
    private String csphone;

    public String getHeadbankid() {
        return headbankid;
    }

    public void setHeadbankid(String headbankid) {
        this.headbankid = headbankid;
    }

    public String getHeadbankname() {
        return headbankname;
    }

    public void setHeadbankname(String headbankname) {
        this.headbankname = headbankname;
    }

    public String getIscommon() {
        return iscommon;
    }

    public void setIscommon(String iscommon) {
        this.iscommon = iscommon;
    }

    public String getCsphone() {
        return csphone;
    }

    public void setCsphone(String csphone) {
        this.csphone = csphone;
    }

    @Override
    public String toString() {
        return "BankInfo{" +
                "headbankid='" + headbankid + '\'' +
                ", headbankname='" + headbankname + '\'' +
                ", iscommon='" + iscommon + '\'' +
                ", csphone='" + csphone + '\'' +
                '}';
    }
}
