package com.tools.payhelper.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class SystemUtil {

    public static String getDeviceUUID(Context context) {
        try {
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            if (telephonyManager != null) {
                final String deviceId = telephonyManager.getDeviceId();
                UUID uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                return uuid.toString();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return "";
    }
}
