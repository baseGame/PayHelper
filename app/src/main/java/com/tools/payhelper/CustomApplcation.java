package com.tools.payhelper;

import android.app.Application;

public class CustomApplcation extends Application {

    public static CustomApplcation mInstance;

    public static CustomApplcation getInstance() {
        return CustomApplcation.mInstance;
    }

    public void onCreate() {
        super.onCreate();
        CustomApplcation.mInstance = this;
    }
}

