package com.tools.payhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tools.payhelper.bean.UnionOrderBean;
import com.tools.payhelper.utils.DBManager;
import com.tools.payhelper.utils.LogUtil;
import com.tools.payhelper.utils.PayHelperUtils;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context arg11, Intent arg12) {
        DBManager dbManager = new DBManager(arg11);

        List<UnionOrderBean> unionOrderBeans = dbManager.findAllUnionOrder();
        for (UnionOrderBean unionOrderBean : unionOrderBeans) {
            if (!TextUtils.equals(unionOrderBean.getResult(), "0")) {
                LogUtil.d("重新发送union订单 : " + unionOrderBean.getBillno() + " ,订单状态 : " + unionOrderBean.getResult());
                PayHelperUtils.notifyUnionPayTrade(arg11, unionOrderBean.getBillno(), unionOrderBean.getAmount(), unionOrderBean.getRemark(), unionOrderBean.getPaytime(),
                        unionOrderBean.getPayaccountno(), unionOrderBean.getPayaccountname());
            }
        }
    }
}