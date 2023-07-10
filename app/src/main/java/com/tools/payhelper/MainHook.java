package com.tools.payhelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import com.tools.payhelper.bean.UnionOrderBean;
import com.tools.payhelper.utils.Constants;
import com.tools.payhelper.utils.DBManager;
import com.tools.payhelper.utils.LogUtil;
import com.tools.payhelper.utils.PayHelperUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainHook implements IXposedHookLoadPackage {


    private static boolean UNIONPAY_HOOK = false;

    private static boolean XGJ_ISHOOK = false;

    private ClassLoader mUnionPayLoader;

    private Activity unionPayActivity;

    private UnionHook unionHook;

    private Context context;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(Constants.UNIONPAY_PACKAGE)) {
            if (UNIONPAY_HOOK) {
                return;
            }
            UNIONPAY_HOOK = true;
            XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.thisObject.getClass().toString().contains("com.unionpay.activity.UPActivityMain")) {
                        unionPayActivity = (Activity) param.thisObject;
                        LogUtil.d("云闪付Hook成功，当前云闪付版本:" + PayHelperUtils.getVerName(unionPayActivity));
                        Object mPackageInfo = XposedHelpers.getObjectField(unionPayActivity.getApplication().getBaseContext(), "mPackageInfo");
                        mUnionPayLoader = (ClassLoader) XposedHelpers.getObjectField(mPackageInfo, "mClassLoader");
                        unionHook = new UnionHook(mUnionPayLoader);

                        IntentFilter filter = new IntentFilter();
                        filter.addAction(Constants.UNION_PAY_GET_QRCODE_ACTION);
                        unionPayActivity.registerReceiver(new UnionQrCodeReceiver(), filter);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                //延迟1秒,等待收款助手启动
                                Thread.sleep(1000);
                                while (true) {
                                    try {
                                        int delay = new Random().nextInt(5) + 25;
                                        LogUtil.d("delay = " + delay);
                                        Thread.sleep(delay * 1000);
                                        CheckNewOrder();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            });
        }
        if (Constants.XGJ_PACKAGE.equals(loadPackageParam.packageName)) {
            if (XGJ_ISHOOK) {
                return;
            }
            XGJ_ISHOOK = true;
            XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.thisObject.getClass().toString().contains(".starposmanager.module.main.MainActivity")) {
                        Activity activity = (Activity) param.thisObject;
                        LogUtil.d("星POS hook成功，当前版本:" + PayHelperUtils.getVerName(activity));
                        Object objectField = XposedHelpers.getObjectField(activity.getApplication().getBaseContext(), "mPackageInfo");
                        ClassLoader classLoader = (ClassLoader) XposedHelpers.getObjectField(objectField, "mClassLoader");

                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(Constants.XGJ_GET_ORDER_HTTP_ACTION);
                        activity.registerReceiver(new XPosReceiver(activity, classLoader), intentFilter);
                        new XGJHook(activity, classLoader).hook();
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                //延迟30秒
                                Thread.sleep(30 * 1000);
                                while (true) {
                                    try {
                                        int delay = new Random().nextInt(10) + 20;
                                        Thread.sleep(delay * 1000);
                                        Intent intent = new Intent();
                                        intent.setAction(Constants.XGJ_GET_ORDER_HTTP_ACTION);
                                        activity.sendBroadcast(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                LogUtil.d("睡眠发生异常");
                                e.printStackTrace();
                            }
                        });
                    }
                }
            });
        }
    }

    public class UnionQrCodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.isEmpty(intent.getAction())) {
                if (intent.getAction().equals(Constants.UNION_PAY_GET_QRCODE_ACTION)) {
                    final String money = intent.getStringExtra("money");
                    final String mark = intent.getStringExtra("mark");
                    if (TextUtils.isEmpty(money) && TextUtils.isEmpty(mark)) {
                        return;
                    }
                    PayHelperUtils.sendMsg(getContext(), "开始生成二维码");
                    Class clazz = XposedHelpers.findClass("com.unionpay.activity.UPActivityMain", mUnionPayLoader);
                    Intent intent1 = new Intent(context, clazz);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);

                    getVirtualCardNum(new GetCardNumListener() {
                        @Override
                        public void success(String re) {
                            GenQrCode(money, mark, re);
                        }

                        @Override
                        public void error(String error) {

                        }
                    });
                }
            }
        }
    }

    private void getVirtualCardNum(final GetCardNumListener listener) {
        new Thread(() -> {
            try {
                String str2 = "https://pay.95516.com/pay-web/restlet/qr/p2pPay/getInitInfo?cardNo=&cityCode=" + unionHook.Enc(unionHook.getcityCd());
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(str2)
                        .header("X-Tingyun-Id", unionHook.getXTid())
                        .header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                        .header("sid", unionHook.getSid())
                        .header("urid", unionHook.geturid())
                        .header("cityCd", unionHook.getcityCd())
                        .header("locale", "zh-CN")
                        .header("User-Agent", "Android CHSP")
                        .header("dfpSessionId", unionHook.getDfpSessionId())
                        .header("gray", unionHook.getgray())
                        .header("key_session_id", "")
                        .header("Host", "pay.95516.com")
                        .build();
                Response response = client.newCall(request).execute();
                String Rsp = unionHook.Dec(response.body().string());
                String encvirtualCardNo = unionHook.Enc(new JSONObject(Rsp).getJSONObject("params").getJSONArray("cardList").getJSONObject(0).getString("virtualCardNo"));
                PayHelperUtils.sendMsg(getContext(), "获取卡号：" + encvirtualCardNo);
                if (listener != null) {
                    listener.success(encvirtualCardNo);
                }
            } catch (Throwable e) {
                if (listener != null) {
                    listener.error(e.getMessage() + e.getCause());
                }
            }
        }).start();
    }

    private void GenQrCode(final String money, final String mark, String encvirtualCardNo) {
        new Thread(() -> {
            try {
                String money1 = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", "");
                String str2 = "https://pay.95516.com/pay-web/restlet/qr/p2pPay/applyQrCode?txnAmt=" + unionHook.Enc(money1) + "&cityCode=" +
                        unionHook.Enc(unionHook.getcityCd()) + "&comments=" + unionHook.Enc(mark) + "&virtualCardNo=" + encvirtualCardNo;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(str2)
                        .header("X-Tingyun-Id", unionHook.getXTid())
                        .header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                        .header("sid", unionHook.getSid())
                        .header("urid", unionHook.geturid())
                        .header("cityCd", unionHook.getcityCd())
                        .header("locale", "zh-CN")
                        .header("User-Agent", "Android CHSP")
                        .header("dfpSessionId", unionHook.getDfpSessionId())
                        .header("gray", unionHook.getgray())
                        .header("key_session_id", "")
                        .header("Host", "pay.95516.com")
                        .build();

                Response response = client.newCall(request).execute();
                String Rsp = unionHook.Dec(response.body().string());
                try {
                    JSONObject o = new JSONObject(Rsp);
                    JSONObject payurljson = o.getJSONObject("params");
                    String payurl = "";
                    if (payurljson.has("certificate")) {
                        payurl = payurljson.get("certificate").toString();
                    }
                    Intent intent = new Intent();
                    intent.setAction(Constants.UNION_PAY_GET_QRCODE_RESULT_ACTION);
                    intent.putExtra("money", money);
                    intent.putExtra("remark", mark);
                    intent.putExtra("payurl", payurl);
                    getContext().sendBroadcast(intent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void CheckNewOrder() {
        try {
            String str2 = "https://wallet.95516.com/acc/inApp/bigData/records?currentPage=" + unionHook.Enc("1") + "&month=" + unionHook.Enc("0") +
                    "&orderStatus=" + unionHook.Enc("0") + "&orderType=" + unionHook.Enc("A30000") + "&pageSize=" + unionHook.Enc("10") + "";
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();
            Request request = new Request.Builder()
                    .url(str2)
                    .header("X-Tingyun-Id", unionHook.getXTid()).header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                    .header("sid", unionHook.getSid()).header("urid", unionHook.geturid()).header("cityCd", unionHook.getcityCd())
                    .header("locale", "zh-CN").header("User-Agent", "Android CHSP").header("dfpSessionId", unionHook.getDfpSessionId())
                    .header("gray", unionHook.getgray()).header("Accept", "*/*").header("key_session_id", "")
                    .header("Host", "wallet.95516.com").build();
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String RSP = responseBody.string();
                String DecRsp = unionHook.Dec(RSP);
                LogUtil.d("DecRsp = " + DecRsp);
                JSONArray jsonArray = new JSONObject(DecRsp).getJSONObject("params").getJSONArray("transRecordsList");
                if (jsonArray != null && jsonArray.length() > 0) {
                    List<JSONObject> jsonObjects = new ArrayList<>();
                    //排除freeze状态的订单
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        if (!"99".equals(jsonObject.optString("inOutType"))) {
                            jsonObjects.add(jsonObject);
                        }
                    }

                    for (int i = 0; i < jsonObjects.size(); i++) {
                        JSONObject jsonObject = jsonObjects.get(i);
                        String dbOrderId = jsonObject.optString("key");
                        String money = jsonObject.optString("transAmount");

                        DBManager dbManager = new DBManager(unionPayActivity);
                        if (!dbManager.isUnionPayOrderExist(dbOrderId)) {
                            UnionOrderBean unionOrderBean = new UnionOrderBean();
                            unionOrderBean.setBillno(dbOrderId);
                            unionOrderBean.setAmount(money);

                            if (dbOrderId.contains("-")) {
                                String[] keySplit = dbOrderId.split("-");
                                if (keySplit.length > 4) {
                                    Calendar calendar = Calendar.getInstance(Locale.CHINA);
                                    int year = calendar.get(Calendar.YEAR);
                                    unionOrderBean.setPaytime(year + keySplit[3]);
                                }
                                unionOrderBean.setPaytime(String.valueOf(System.currentTimeMillis()));
                            } else {
                                unionOrderBean.setPaytime("");
                            }
                            dbManager.addUnionPayOrder(unionOrderBean);

                            Intent intent = new Intent(Constants.UNION_PAY_NOTIFICATION_ACTION);
                            intent.putExtra("dbOrderId", dbOrderId);
                            intent.putExtra("money", money);
                            intent.putExtra("remark", "");
                            intent.putExtra("orderTime", "");
                            intent.putExtra("payaccountno", "");
                            intent.putExtra("payaccountname", "");
                            getContext().sendBroadcast(intent);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            LogUtil.d("CheckNewOrder2 throwable " + e.getMessage());
        }
    }

    public Context getContext() {
        if (context == null) {
            try {
                Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
                Method method = ActivityThread.getMethod("currentActivityThread");
                Object currentActivityThread = method.invoke(ActivityThread);
                Method method2 = currentActivityThread.getClass().getMethod("getApplication");
                context = (Context) method2.invoke(currentActivityThread);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return context;
    }

    private class XPosReceiver extends BroadcastReceiver {

        ClassLoader classLoader;

        XPosReceiver(Context context, ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (Constants.XGJ_GET_ORDER_HTTP_ACTION.equals(action)) {
                new Thread(() -> {
                    Class remindPresenterClazz = XposedHelpers.findClass("com.newland.satrpos.starposmanager.module.me.remind.RemindPresenter", classLoader);
                    Object remindPresenterObject = XposedHelpers.newInstance(remindPresenterClazz, new Object[0]);
                    Class iRemindViewClass = XposedHelpers.findClass("com.newland.satrpos.starposmanager.module.me.remind.IRemindView", classLoader);
                    Object iRemindViewObject = Proxy.newProxyInstance(classLoader, new Class[]{iRemindViewClass}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().contains("getQryNoticeListMap")) {
                                return new HashMap<String, String>(16);
                            } else {
                                return null;
                            }
                        }
                    });
                    XposedHelpers.setObjectField(remindPresenterObject, "mView", iRemindViewObject);
                    XposedHelpers.callMethod(remindPresenterObject, "qryNoticeList", new Object[0]);
                }).start();
            }
        }
    }
}
