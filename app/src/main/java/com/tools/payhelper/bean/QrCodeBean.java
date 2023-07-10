package com.tools.payhelper.bean;

import java.io.Serializable;

public class QrCodeBean implements Serializable {
    private String dt;
    private String mark;
    private String money;
    private String payurl;
    private static final String serialVersionSTR = "1BQljr6O6WxeHGZ77JU2qQ1eiMQvBdFFl3xTYWEIfW4=";
    private static final long serialVersionUID = 0x7CBEAFB374FC94A7L;
    private String type;

    public QrCodeBean() {
        super();
    }

    public QrCodeBean(String arg1, String arg2, String arg3, String arg4, String arg5) {
        super();
        this.money = arg1;
        this.mark = arg2;
        this.type = arg3;
        this.payurl = arg4;
        this.dt = arg5;
    }

    public String getDt() {
        return this.dt;
    }

    public String getMark() {
        return this.mark;
    }

    public String getMoney() {
        return this.money;
    }

    public String getPayurl() {
        return this.payurl;
    }

    public String getType() {
        return this.type;
    }

    public void setDt(String arg1) {
        this.dt = arg1;
    }

    public void setMark(String arg1) {
        this.mark = arg1;
    }

    public void setMoney(String arg1) {
        this.money = arg1;
    }

    public void setPayurl(String arg1) {
        this.payurl = arg1;
    }

    public void setType(String arg1) {
        this.type = arg1;
    }
}

