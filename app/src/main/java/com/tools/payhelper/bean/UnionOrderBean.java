package com.tools.payhelper.bean;

public class UnionOrderBean {

    /**
     * payaccountname : *加原
     * amount : 100.00
     * payaccountno : 工商银行(4657)
     * channeltype : 0
     * remark : 457878
     * paytime : 1546703894094
     * paytype : unionpay
     * billno : 20190105200040011100810063344851
     */

    private String payaccountname;
    private String amount;
    private String payaccountno;
    private int channeltype;
    private String remark;
    private String paytime;
    private String paytype;
    private String billno;
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPayaccountname() {
        return payaccountname;
    }

    public void setPayaccountname(String payaccountname) {
        this.payaccountname = payaccountname;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayaccountno() {
        return payaccountno;
    }

    public void setPayaccountno(String payaccountno) {
        this.payaccountno = payaccountno;
    }

    public int getChanneltype() {
        return 3;
    }

    public void setChanneltype(int channeltype) {
        this.channeltype = channeltype;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPaytime() {
        return paytime;
    }

    public void setPaytime(String paytime) {
        this.paytime = paytime;
    }

    public String getPaytype() {
        return "unionpay";
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    @Override
    public String toString() {
        return "billno = " + billno + " ,paytime = " + paytime + " ,remark = " + remark +
                " ,payaccountno = " + payaccountno + " ,payaccountname = " + payaccountname + " ,amount = " + amount;
    }
}
