package com.tools.payhelper.bean;


import java.io.Serializable;

/**
 * 消息类型
 * 0：生成二维码
 * 1：二维码已生成通知
 * 2：根据备注查询订单
 * 3：返回根据备注查询订单
 * **其他待约定
 */
public class WMessageBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * type : 0
     * dataobj : {"amount":0.21,"remark":"8377B0DB96A92A29","paytype":"alipay"}
     * createtime : 2018-11-09 15:27:58
     */

    private int type;
    private DataobjBean dataobj;
    private String createtime;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DataobjBean getDataobj() {
        return dataobj;
    }

    public void setDataobj(DataobjBean dataobj) {
        this.dataobj = dataobj;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public static class DataobjBean {
        /**
         * amount : 0.21
         * remark : 8377B0DB96A92A29
         * paytype : alipay
         */

        private double amount;
        private String remark;
        private String paytype;
        private String payurl;
        private String account;
        private String id;
        private String appuserid = "";
        private String apploginid = "";
        private String appusername = "";
        private String balance;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getPaytype() {
            return paytype;
        }

        public void setPaytype(String paytype) {
            this.paytype = paytype;
        }

        public String getPayurl() {
            return payurl;
        }

        public void setPayurl(String payurl) {
            this.payurl = payurl;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAppuserid() {
            return appuserid;
        }

        public void setAppuserid(String appuserid) {
            this.appuserid = appuserid;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getApploginid() {
            return apploginid;
        }

        public void setApploginid(String apploginid) {
            this.apploginid = apploginid;
        }

        public String getAppusername() {
            return appusername;
        }

        public void setAppusername(String appusername) {
            this.appusername = appusername;
        }
    }


}
