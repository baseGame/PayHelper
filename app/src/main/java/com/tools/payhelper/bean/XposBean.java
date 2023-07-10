package com.tools.payhelper.bean;

public class XposBean {

    private String merc_id;
    private String paychannel;
    private String txn_amt;
    private String txn_tm;
    private String result;

    public String getMerc_id() {
        return merc_id;
    }

    public void setMerc_id(String merc_id) {
        this.merc_id = merc_id;
    }

    public String getPaychannel() {
        return paychannel;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setPaychannel(String paychannel) {
        this.paychannel = paychannel;
    }

    public String getTxn_amt() {
        return txn_amt;
    }

    public void setTxn_amt(String txn_amt) {
        this.txn_amt = txn_amt;
    }

    public String getTxn_tm() {
        return txn_tm;
    }

    public void setTxn_tm(String txn_tm) {
        this.txn_tm = txn_tm;
    }
}
