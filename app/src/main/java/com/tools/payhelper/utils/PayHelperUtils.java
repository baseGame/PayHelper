package com.tools.payhelper.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Base64;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tools.payhelper.MainActivity;
import com.tools.payhelper.bean.ResultBean;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class PayHelperUtils {

    public static String getVerName(Context arg5) {
        String v1 = "";
        try {
            v1 = arg5.getPackageManager().getPackageInfo(arg5.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException v0) {
            PayHelperUtils.sendMsg(arg5, "getVerName异常" + v0.getMessage());
        }
        return v1;
    }

    public static boolean isAppRunning(Context arg6, String arg7) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) arg6.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List v2 = activityManager.getRunningTasks(0x64);
        if (v2.size() > 0) {
            Iterator v4 = v2.iterator();
            do {
                if (v4.hasNext()) {
                    if (((RunningTaskInfo) v4.next()).baseActivity.getPackageName().equals(arg7)) {
                        isRunning = true;
                        break;
                    }
                }
            }
            while (true);
        }
        return isRunning;
    }

    public static void notifyUnionPayTrade(Context context, final String no, String money, String mark, String paytime, String payaccountno, String payaccountname) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("paytype", "unionpay");
            //订单号不需要传
            jsonObject.put("billno", "");
            BigDecimal bigDecimal = new BigDecimal(Long.valueOf(money) / 100f);
            Double amount = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            jsonObject.put("amount", amount);
            jsonObject.put("remark", mark);
            jsonObject.put("paytime", paytime);
            jsonObject.put("channeltype", 3);
            jsonObject.put("payaccountno", payaccountno); //付款人账号
            jsonObject.put("payaccountname", payaccountname); //付款人姓名

            LogUtil.d("jsonObject = " + jsonObject.toString());
//            String data = EncrptyUtil.encryptBase64(jsonObject.toString(), SharedUtil.getString(context, SharedUtil.AES_KEY));
            String data = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
            String time = String.valueOf(System.currentTimeMillis());
            String sign = EncrptyUtil.md5(data + time + SharedUtil.getString(context, SharedUtil.MD5_KEY));

            RequestParams requestParams = new RequestParams();
            requestParams.addBodyParameter("data", data);
            requestParams.addBodyParameter("sign", sign);
            requestParams.addBodyParameter("time", time);
            requestParams.addBodyParameter("id", SharedUtil.getString(context, "id"));

            HttpUtils httpUtils = new HttpUtils(HttpUtil.TIME_OUT);
            httpUtils.send(HttpMethod.POST, HttpUtil.getInstance().getCALLBACK(), requestParams, new RequestCallBack<String>() {
                public void onFailure(HttpException arg3, String arg4) {
                    LogUtil.d("发送Union订单失败 : " + arg4);
                    sendMsg(context, "发送Union订单失败 ： " + arg4);
                }

                public void onSuccess(ResponseInfo arg4) {
                    String result = (String) arg4.result;
                    Gson gson = new Gson();
                    ResultBean resultBean = gson.fromJson(result, ResultBean.class);
                    if (resultBean != null) {
                        if (resultBean.getCode() == 0 || resultBean.getCode() == 501) {
                            sendMsg(context, "云闪付订单回调成功: " + result);
                            DBManager dbManager = new DBManager(context);
                            dbManager.updateUnionPayOrderStatus("0", no);
                        } else {
                            sendMsg(context, "云闪付订单回调失败: " + result);
                        }
                    } else {
                        sendMsg(context, "云闪付订单回调失败: " + result);
                    }
                }
            });
        } catch (Exception v1) {
            sendMsg(context, "云闪付订单回调异常：" + v1.getMessage());
        }
    }

    public static void notifyXPosTrade(Context context, String money, final String type, String dt) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", money);
            jsonObject.put("paytype", type);
            jsonObject.put("orderchannel", type);
            jsonObject.put("remark", "");
            jsonObject.put("paytime", dt);
            String uploadContent = jsonObject.toString();
            LogUtil.d("jsonObject = " + uploadContent);

//            String data = EncrptyUtil.encryptBase64(jsonObject.toString(), SharedUtil.getString(context, SharedUtil.AES_KEY));
            String data = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
            String time = String.valueOf(System.currentTimeMillis());
            String sign = EncrptyUtil.md5(data + time + SharedUtil.getString(context, SharedUtil.MD5_KEY));

            RequestParams requestParams = new RequestParams();
            requestParams.addBodyParameter("data", data);
            requestParams.addBodyParameter("sign", sign);
            requestParams.addBodyParameter("time", time);
            requestParams.addBodyParameter("id", SharedUtil.getString(context, "id"));
            HttpUtils httpUtils = new HttpUtils(HttpUtil.TIME_OUT);
            httpUtils.send(HttpMethod.POST, HttpUtil.getInstance().getCALLBACK(), requestParams, new RequestCallBack<String>() {
                public void onFailure(HttpException arg3, String arg4) {
                    LogUtil.d("发送订单失败 : " + arg4);
                    sendMsg(context, "发送订单失败 ： " + arg4);
                }

                public void onSuccess(ResponseInfo arg4) {
                    String result = (String) arg4.result;
                    Gson gson = new Gson();
                    ResultBean resultBean = gson.fromJson(result, ResultBean.class);
                    if (resultBean != null) {
                        if (resultBean.getCode() == 0 || resultBean.getCode() == 501) {
                            sendMsg(context, "订单时间:" + dt + "订单金额:" + money + ",回调成功: " + result);
                        } else {
                            sendMsg(context, "订单时间:" + dt + "订单金额:" + money + "回调失败: " + result);
                        }
                    } else {
                        sendMsg(context, "订单时间:" + dt + "订单金额:" + money + "回调失败: " + result);
                    }
                }
            });
        } catch (Exception v1) {
            sendMsg(context, "订单时间:" + dt + "订单金额:" + money + v1.getMessage());
        }
    }

    public static void sendAppMsg(String money, String mark, String appuserid, String type, Context context) {
        LogUtil.d("sendAppMsg " + money + " " + mark + " " + type);
        Intent intent = new Intent();
        switch (type) {
            case "unionpay":
                intent.setAction(Constants.UNION_PAY_GET_QRCODE_ACTION);
                break;
        }
        intent.putExtra("mark", mark);
        intent.putExtra("money", money);
        context.sendBroadcast(intent);
    }

    public static void sendMsg(Context arg2, String arg3) {
        Intent v0 = new Intent();
        v0.putExtra("msg", arg3);
        v0.setAction(Constants.MSGRECEIVED_ACTION);
        arg2.sendBroadcast(v0);
    }

    public static void startAPP(Context context) {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            context.startActivity(intent);
        } catch (Exception v1) {
            v1.printStackTrace();
            LogUtil.d("startAPP exception " + v1.getMessage());
        }
    }

    public static void startAPP(Context arg4, String arg5) {
        try {
            arg4.startActivity(arg4.getPackageManager().getLaunchIntentForPackage(arg5));
        } catch (Exception v0) {
            PayHelperUtils.sendMsg(arg4, "startAPP异常" + v0.getMessage());
        }
    }
}

