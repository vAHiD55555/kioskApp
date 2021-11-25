package com.example.devownerjav;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;

public class DevAdminReceiver extends DeviceAdminReceiver {
    String TAG = "devOwner : ";
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Log.d(TAG, "onEnabled: ");


    }

//    @Override
//    public CharSequence onDisableRequested(Context context, Intent intent) {
//        return "Admin rights are beeing requested to be disabled for the app called: '" + context.getString(R.string.app_name) + "'.";
//    }

    void showLog(String msg) {
        String status =  msg;
        Logger.d(status);
    }

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), DeviceAdminReceiver.class);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "Do you want to disable device admin?";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showLog("Device Admin Disabled");
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
        showLog("KIOSK mode enabled");
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        showLog("KIOSK mode disabled");
    }

}
