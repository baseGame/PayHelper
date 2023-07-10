package com.tools.payhelper.bean;

public class LoginBean {

    /**
     * code : 0
     * msg : success
     * data : {"md5key":"0a9916cc7fa5f8eb","id":10000029,"aeskey":"098410D1E3FC9B5F"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * md5key : 0a9916cc7fa5f8eb
         * id : 10000029
         * aeskey : 098410D1E3FC9B5F
         */

        private String md5key;
        private int id;
        private String aeskey;

        public String getMd5key() {
            return md5key;
        }

        public void setMd5key(String md5key) {
            this.md5key = md5key;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAeskey() {
            return aeskey;
        }

        public void setAeskey(String aeskey) {
            this.aeskey = aeskey;
        }
    }
}
