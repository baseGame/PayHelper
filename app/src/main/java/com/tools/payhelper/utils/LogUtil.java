package com.tools.payhelper.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogUtil {

    private static final boolean debug = true;

    public static void d(String message) {
        if (debug) {
            Log.e(Constants.TAG, message);
        }
    }

    // 新建或打开日志文件
    public static void writeLogtoFile(String text) {
        try {
            LogUtil.d(text);
            File file = new File(Environment.getExternalStorageDirectory(), "wechat_log.txt");
            if (file.exists()) {
                LogUtil.d("delete success =  " + file.delete());
            }
            boolean create = file.createNewFile();
            if (create) {
                FileWriter filerWriter = new FileWriter(file, true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(text);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            }
        } catch (Exception e) {
            LogUtil.d("writeLogtoFile Exception = " + e.getMessage());
        }
    }

    public static String readLogtoFile() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "1.txt");
            String str;
            InputStream is = new FileInputStream(file);
            InputStreamReader input = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(input);
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
        } catch (Exception e) {
            LogUtil.d("readLogtoFile Exception = " + e.getMessage());
        }
        String result = stringBuilder.toString();
        LogUtil.d("readLogtoFile = " + result);
        return result;
    }
}
