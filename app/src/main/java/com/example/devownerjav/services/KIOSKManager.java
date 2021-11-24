package com.example.devownerjav.services;

import static android.content.Context.DEVICE_POLICY_SERVICE;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.util.Log;

import com.orhanobut.logger.Logger;

public class KIOSKManager {

    Activity activity;
    private DevicePolicyManager devicePolicyManager;        // Device policy manager for activate Device admin

    public KIOSKManager(Activity act) {
        activity = act;
        devicePolicyManager = (DevicePolicyManager) activity.getSystemService(DEVICE_POLICY_SERVICE);    // Initializing device policy manager
    }

    /**
     * enables KIOSK mode
     *
     * @param enabled true/false
     */
    public void enableKioskMode(boolean enabled) {
        try {
            if (enabled) {
                if (devicePolicyManager.isLockTaskPermitted(activity.getPackageName())) {
                    activity.startLockTask();
                } else {
                    Logger.d("Kiosk Mode Error " + "Not permitted");
                }
            } else {
                activity.stopLockTask();
            }
        } catch (Exception e) {
            Logger.e("Kiosk Mode Error" + e.getMessage());
        }
    }

}