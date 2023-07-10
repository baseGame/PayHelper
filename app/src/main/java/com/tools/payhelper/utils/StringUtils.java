package com.tools.payhelper.utils;

import android.content.Context;
import android.os.Environment;

import com.lidroid.xutils.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {

    public static String getTextCenter(String arg4, String arg5, String arg6) {
        String v2;
        try {
            int v0 = arg4.indexOf(arg5) + arg5.length();
            v2 = arg4.substring(v0, arg4.indexOf(arg6, v0));
        } catch (Exception v1) {
            v1.printStackTrace();
            v2 = "error";
        }
        return v2;
    }

    public static String date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
            return String.valueOf(sdf.parse(date_str).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //yyyy-MM-dd HH:mm
    public static String timeStamp2Date(String timeStamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            Date date = new Date(Long.valueOf(timeStamp));
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String splitString(String str, String str2, String str3) {
        try {
            int indexOf = str.indexOf(str2) + str2.length();

            return str.substring(indexOf, str.indexOf(str3, indexOf));
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    static RequestParams getRequestParams(Context context, String content) {
        RequestParams requestParams = new RequestParams();
        requestParams.setContentType("application/json");
        try {
            String data = EncrptyUtil.encryptBase64(content, SharedUtil.getString(context, SharedUtil.AES_KEY));
            String time = String.valueOf(System.currentTimeMillis());
            String sign = EncrptyUtil.md5(data + time + SharedUtil.getString(context, SharedUtil.MD5_KEY));

            JSONObject uploadJsonObject = new JSONObject();
            uploadJsonObject.put("data", data);
            uploadJsonObject.put("sign", sign);
            uploadJsonObject.put("time", time);
            uploadJsonObject.put("id", SharedUtil.getString(context, SharedUtil.ID));
            requestParams.setBodyEntity(new StringEntity(uploadJsonObject.toString(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestParams;
    }
}

