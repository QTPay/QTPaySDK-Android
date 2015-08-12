package com.example.qianfangdemo.entity;

import java.io.Serializable;

/**
 * Created by Yang on 15/5/7.
 *
 * /util/v1/cardsinfo
 */
public class CardInfo extends BankInfo implements Serializable{

    private String headbankid;
    private String headbankname;
    private String cardtype;
    private String csphone;
    private String iscommon;


    public void setHeadbankid(String headbankid) {
        this.headbankid = headbankid;
    }

    public String getHeadbankid() {
        return headbankid;
    }

    public String getHeadbankname() {
        return headbankname;
    }

    public void setHeadbankname(String headbankname) {
        this.headbankname = headbankname;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getCsphone() {
        return csphone;
    }

    public void setCsphone(String csphone) {
        this.csphone = csphone;
    }

    public String getIscommon() {
        return iscommon;
    }

    public void setIscommon(String iscommon) {
        this.iscommon = iscommon;
    }

    @Override
    public String toString() {
        return "CardInfo{" +
                "headbankid='" + headbankid + '\'' +
                ", headbankname='" + headbankname + '\'' +
                ", cardtype='" + cardtype + '\'' +
                ", csphone='" + csphone + '\'' +
                ", iscommon='" + iscommon + '\'' +
                '}';
    }
}
