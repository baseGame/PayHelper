package com.tools.payhelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedUtil {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String MD5_KEY = "md5_key";
    public static final String AES_KEY = "aes_key";
    public static final String ID = "id";
    public static final String XPOS_MONEY = "xpos_money";
    public static final String XPOS_TYPE = "xpos_type";
    public static final String XPOS_TIME = "xpos_time";
    public static final String XPOS_BUSINESS = "xpos_business";

    public static boolean getBoolean(Context arg2, String arg3, boolean arg4) {
        return SharedUtil.getDefaultSharedPreferences(arg2).getBoolean(arg3, arg4);
    }

    public static SharedPreferences getDefaultSharedPreferences(Context arg2) {
        return arg2.getSharedPreferences("bbplayer", 0);
    }

    public static int getInt(Context arg2, String arg3) {
        return SharedUtil.getDefaultSharedPreferences(arg2).getInt(arg3, 0);
    }

    public static String getString(Context arg2, String arg3) {
        return SharedUtil.getDefaultSharedPreferences(arg2).getString(arg3, "");
    }

    public static void putBoolean(Context arg2, String arg3, boolean arg4) {
        SharedPreferences.Editor v0 = SharedUtil.getDefaultSharedPreferences(arg2).edit();
        v0.putBoolean(arg3, arg4);
        v0.apply();
    }

    public static void putInt(Context arg2, String arg3, int arg4) {
        SharedPreferences.Editor v0 = SharedUtil.getDefaultSharedPreferences(arg2).edit();
        v0.putInt(arg3, arg4);
        v0.apply();
    }

    public static void putString(Context arg2, String arg3, String arg4) {
        SharedPreferences.Editor v0 = SharedUtil.getDefaultSharedPreferences(arg2).edit();
        v0.putString(arg3, arg4);
        v0.apply();
    }
}

