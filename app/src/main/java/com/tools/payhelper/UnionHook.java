package com.tools.payhelper;

import android.content.Context;
import android.text.TextUtils;

import com.tools.payhelper.utils.LogUtil;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class UnionHook {

    private ClassLoader mClassLoader;

    public static boolean AUTO = false;

    UnionHook(ClassLoader classLoader) {
        mClassLoader = classLoader;
    }

    String Dec(String src) {
        try {
            return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.encrypt.IJniInterface", mClassLoader), "decryptMsg", src);
        } catch (Throwable e) {
            LogUtil.d("Dec Exception = " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    String Enc(String src) {
        try {
            return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.encrypt.IJniInterface", mClassLoader), "encryptMsg", src);
        } catch (Throwable e) {
            LogUtil.d("Enc Exception = " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    String getXTid() {
        try {
            Class m_s = XposedHelpers.findClass("com.networkbench.agent.impl.m.s", mClassLoader);
            Object f = XposedHelpers.callStaticMethod(m_s, "f");
            Object h = XposedHelpers.callMethod(f, "H");
            Object i = XposedHelpers.callStaticMethod(m_s, "I");
            return m_s.getDeclaredMethod("a", String.class, int.class).invoke(null, h, i).toString();
        } catch (Throwable e) {
            LogUtil.d("getXTid ：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    String getSid() {
        String sid = "";
        try {
            Object b = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.network.aa", mClassLoader), "b");
            sid = XposedHelpers.callMethod(b, "e").toString();
        } catch (Throwable e) {
            LogUtil.d("getSid ：" + e.getMessage());
            e.printStackTrace();
        }
        return sid;
    }

    String geturid() {
        String Cacheurid = "";
        try {
            Class data_d = XposedHelpers.findClass("com.unionpay.data.d", mClassLoader);
            Object o = XposedHelpers.callStaticMethod(data_d, "a", new Class[]{Context.class}, new Object[1]);
            String v1_2 = XposedHelpers.callMethod(XposedHelpers.callMethod(o, "A"), "getHashUserId").toString();
            if (!TextUtils.isEmpty(v1_2) && v1_2.length() >= 15) {
                Cacheurid = v1_2.substring(v1_2.length() - 15);
            }
        } catch (Throwable e) {
            LogUtil.d("geturid ：" + e.getMessage());
            e.printStackTrace();
        }
        return Cacheurid;
    }

    String getDfpSessionId() {
        String CacheDfpSessionId = "";
        try {
            Object o = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.service.b", mClassLoader), "d");
            CacheDfpSessionId = o.toString();
        } catch (Throwable e) {
            LogUtil.d("getDfpSessionId ：" + e.getMessage());
            e.printStackTrace();
        }
        return CacheDfpSessionId;
    }

    String getcityCd() {
        String CachecityCd = "";
        try {
            CachecityCd = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.location.a", mClassLoader), "i").toString();
        } catch (Throwable e) {
            LogUtil.d("getcityCd ：" + e.getMessage());
            e.printStackTrace();
        }
        return CachecityCd;
    }

    String getgray() {
        String Cachegray = "";
        try {
            Object b = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.network.aa", mClassLoader), "b");
            Cachegray = XposedHelpers.callMethod(b, "d").toString();
        } catch (Throwable e) {
            LogUtil.d("getgray ：" + e.getMessage());
            e.printStackTrace();
        }
        return Cachegray;
    }


    public static Context getContext() {
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);//获取currentActivityThread 对象
            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            Context context = (Context) method2.invoke(currentActivityThread);//获取 Context对象
            XposedBridge.log("Context " + context);
            return context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
