package com.tools.payhelper.utils;

public class HttpUtil {
    public static final boolean isDebug = false;

    public static int TIME_OUT = 30 * 1000;

    //茂盛本地环境
    public static String MS = "10.10.100.6:9001";
    //JX本地环境.
    public static String JX = "10.10.101.14:8004";

    //线上环境
//    public static String SC1 = "unionpay.1393111.com";
    public static String SC1 = "api.lx859.com";
    public static String SC1_1 = "103.60.110.64";

    //    private String baseURL = "47.52.173.49:3020";
    // 百付来正式环境  pay.blfpay.vip     ws: 47.52.148.166:3020
    // 新宝正式环境   pay.xbaopay.vip     ws: 47.52.253.100:3020
    // 大师正式环境   pay.dashihfu.com    ws: 47.244.157.126:3020
    // 加宝正式环境   pay.jpay8.vip    ws: 47.244.137.54:3020

    private String baseURL = "pay.jpay8.vip";
    private String baseWsURL = "47.244.137.54:3020";

    private static HttpUtil httpUtil;

    private HttpUtil() {
    }

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            httpUtil = new HttpUtil();
        }
        return httpUtil;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getLOGIN() {
        return "http://" + baseURL + "/openApi/login";
    }

    String getCALLBACK() {
        return "http://" + baseURL + "/openApi/callback";
    }

    public String getSOCKET_URL() {
        return "ws://" + baseWsURL + "/ws/websocket";
    }

}
