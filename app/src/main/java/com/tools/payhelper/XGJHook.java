package com.tools.payhelper;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tools.payhelper.utils.Constants;
import com.tools.payhelper.utils.LogUtil;
import com.tools.payhelper.utils.SharedUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

class XGJHook {

    private ClassLoader mClassLoader;

    private Context mContext;

    XGJHook(Context context, ClassLoader classLoader) {
        this.mContext = context;
        mClassLoader = classLoader;
    }

    void hook() {
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.newland.satrpos.starposmanager.api.CustomSubscriber", mClassLoader), "onNext", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String response = new Gson().toJson(param.args[0]);
                if (!TextUtils.isEmpty(response) && response.contains("noticeList")) {
                    LogUtil.d("qryNoticeListResult = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.optJSONArray("noticeList");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        String lastMoney = SharedUtil.getString(mContext, SharedUtil.XPOS_MONEY);
                        String lastType = SharedUtil.getString(mContext, SharedUtil.XPOS_TYPE);
                        String lastDt = SharedUtil.getString(mContext, SharedUtil.XPOS_TIME);
                        String lastBusiness = SharedUtil.getString(mContext, SharedUtil.XPOS_BUSINESS);
                        if (TextUtils.isEmpty(lastMoney) || TextUtils.isEmpty(lastType)
                                || TextUtils.isEmpty(lastDt) || TextUtils.isEmpty(lastBusiness)) {
                            JSONObject lastRecordJsonObject = jsonArray.optJSONObject(0);
                            if (lastRecordJsonObject != null) {
                                String money = lastRecordJsonObject.optString("txn_amt");
                                String type = lastRecordJsonObject.optString("paychannel");
                                String dt = lastRecordJsonObject.optString("txn_tm");
                                String business = lastRecordJsonObject.optString("merc_id");
                                SharedUtil.putString(mContext, SharedUtil.XPOS_MONEY, money);
                                SharedUtil.putString(mContext, SharedUtil.XPOS_TYPE, type);
                                SharedUtil.putString(mContext, SharedUtil.XPOS_TIME, dt);
                                SharedUtil.putString(mContext, SharedUtil.XPOS_BUSINESS, business);
                            }
                        } else {
                            int index = -1;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                String money = jsonObject1.optString("txn_amt");
                                String type = jsonObject1.optString("paychannel");
                                String dt = jsonObject1.optString("txn_tm");
                                String business = jsonObject1.optString("merc_id");

                                if (TextUtils.equals(money, lastMoney) && TextUtils.equals(type, lastType)
                                        && TextUtils.equals(dt, lastDt) && TextUtils.equals(business, lastBusiness)) {
                                    index = i;
                                    break;
                                }
                            }

                            if (index > 0) {
                                Toast.makeText(mContext, "有" + index + "个新订单", Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < index; i++) {
                                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                    String money = jsonObject1.optString("txn_amt");
                                    String type = jsonObject1.optString("paychannel");
                                    String dt = jsonObject1.optString("txn_tm");
                                    String business = jsonObject1.optString("merc_id");
                                    if (i == 0) {
                                        SharedUtil.putString(mContext, SharedUtil.XPOS_MONEY, money);
                                        SharedUtil.putString(mContext, SharedUtil.XPOS_TYPE, type);
                                        SharedUtil.putString(mContext, SharedUtil.XPOS_TIME, dt);
                                        SharedUtil.putString(mContext, SharedUtil.XPOS_BUSINESS, business);
                                    }
                                    Intent intent = new Intent();
                                    intent.setAction(Constants.XGJ_GET_ORDER_RESULT_ACTION);
                                    intent.putExtra("money", money);
                                    intent.putExtra("type", type);
                                    intent.putExtra("dt", dt);
                                    intent.putExtra("business", business);
                                    mContext.sendBroadcast(intent);
                                }
                            } else {
                                Toast.makeText(mContext, "没有新订单!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });
    }
}
