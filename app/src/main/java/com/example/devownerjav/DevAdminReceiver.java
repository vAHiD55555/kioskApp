package com.example.devownerjav;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

public class DevAdminReceiver extends DeviceAdminReceiver {
    String TAG = "devOwner : ";
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Log.d(TAG, "onEnabled: ");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "Admin rights are beeing requested to be disabled for the app called: '" + context.getString(R.string.app_name) + "'.";
    }
}
