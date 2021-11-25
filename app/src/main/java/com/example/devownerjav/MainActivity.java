package com.example.devownerjav;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.devownerjav.services.DeviceAdminService;
import com.example.devownerjav.services.KIOSKManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /*Device admin*/
    private DevicePolicyManager dpm ;

    /*TAG*/
    public String TAG = "devOwner : ";
    public String TAGE = "devOwnerER : ";

    /*Kiosk*/
    private DevicePolicyManager devicePolicyManager = null;
    private ComponentName adminCompName = null;

    private ListView lmViewVisible;
    private ListView lmViewHidden;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);


        /*ADMIN TEST PROGRAMATICALLY OWN DEVICE*/
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn = (Button) findViewById(R.id.button);


        /*KIOSK ENABLE DISABLE*/
        Button btnActivate = findViewById(R.id.btnActivate);
        Button btnDeactivate = findViewById(R.id.btnDeactivate);
        btnActivate.setOnClickListener(this);
        btnDeactivate.setOnClickListener(this);

        /*APPLICATION LISTING*/
        lmViewVisible = (ListView) findViewById(R.id.visible);
        lmViewHidden = (ListView) findViewById(R.id.hidden);

        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              activateKIOSK(false);
                DenieOwner();

                if(devicePolicyManager.isDeviceOwnerApp(getPackageName())){
                    Log.d(TAG, "is device owner : "+ devicePolicyManager.isDeviceOwnerApp(getPackageName()));
                }else{
                    Log.d(TAG, "not device owner : " +devicePolicyManager.isDeviceOwnerApp(getPackageName()));
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateKIOSK(true);
                GrantOwner();

                if(devicePolicyManager.isDeviceOwnerApp(getPackageName())){
                    Log.d(TAG, "is device owner : "+ devicePolicyManager.isDeviceOwnerApp(getPackageName()));
                }else{
                    Log.d(TAG, "not device owner : " +devicePolicyManager.isDeviceOwnerApp(getPackageName()));
                }
            }
        });

        initLogger();
        initDeviceAdmin();

        installedApps();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Application paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Device Owner Check*/
        if(dpm.isDeviceOwnerApp(getPackageName())){
            activateKIOSK(true);
            Log.d(TAG, "is device owner ");
        }else{
            Log.d(TAG, "not device owner ");
        }
    }
        /*Grant Device Owner Test*/
    private void GrantOwner(){
        try {
            Log.d(TAG, "applicationPolicyGrant: Running application grant policy");
            Runtime.getRuntime().exec("dpm remove-active-admin com.example.devownerjav/.DevAdminReceiver");
        } catch (Exception e) {
            Log.e(TAG, "device owner not set");
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
    /*Cancel Device Owner Test*/
    private void DenieOwner(){
        try{
            Log.d(TAG, this.getPackageName()+".DevAdminReceiver");
//            dpm.clearDeviceOwnerApp(this.getPackageName()+".DevAdminReceiver");
            dpm.clearDeviceOwnerApp(this.getPackageName());
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "DenieOwner: "+e+"" );
        }

    }

    /*Kiosk Mode*/
    /**
     * configuration for logger
     */
    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("KIOSKDemo")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    /**
     * Activates KIOSK mode by calling enableKioskMode() of KIOSKManager class
     *
     * @param status
     */
    private void activateKIOSK(boolean status){
        if(devicePolicyManager.isAdminActive(adminCompName)) {
            KIOSKManager km = new KIOSKManager(this);
            km.enableKioskMode(status);
        }
    }

    /**
     * Initialize device admin privileges
     */
    private void initDeviceAdmin(){
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminCompName = new ComponentName(getApplicationContext(),DevAdminReceiver.class);

        if (devicePolicyManager.isDeviceOwnerApp(getPackageName())) {
            devicePolicyManager.setLockTaskPackages(adminCompName, new String[]{getPackageName()});
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnActivate: { activateKIOSK(true); break;}
            case R.id.btnDeactivate: { activateKIOSK(false); break;}
        }
    }

    /**
     * Called when the window containing this view gains or loses focus and Hide system UIs
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /* APPLICATION LIST*/

    public void installedApps()
    {
        List<String> yArrayList = new ArrayList<String>();
        List<String> xArrayList = new ArrayList<String>();
        List<PackageInfo> packList = this.getPackageManager().getInstalledPackages(0);
        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);
            String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            String packageName = packInfo.applicationInfo.packageName;
            String mainActivity = packInfo.applicationInfo.className ;
//            Log.e("App № " + Integer.toString(i), mainActivity+" : "+appName +":"+packageName+""+" , isHidden :"+dpm.isApplicationHidden(adminCompName,appName));
//            yArrayList.add(packageName);

            if (dpm.isApplicationHidden(adminCompName,appName) == false){
                Log.i("App № " + Integer.toString(i), "false : "+packageName);
                yArrayList.add(packageName);
            }else{
                Log.i("App № " + Integer.toString(i), "true : "+packageName);
                xArrayList.add(packageName);
            }

        }
        ArrayAdapter<String> visibleArray = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                yArrayList);
        ArrayAdapter<String> HiddenArray = new ArrayAdapter<String>(
          this,
          android.R.layout.simple_list_item_1,
          xArrayList);

        lmViewVisible.setAdapter(visibleArray);

        lmViewVisible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    String selectedItem = yArrayList.get(position);
                    hideApp(selectedItem);

                }catch (Exception e){
                    Log.e(TAG, "onItemClick: ",e );
                }
            }
        });

        lmViewHidden.setAdapter(HiddenArray);

        lmViewHidden.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    String selectedItem = xArrayList.get(position);
                    showApp(selectedItem);

                }catch (Exception e){
                    Log.e(TAG, "onItemClick: ",e );
                }
            }
        });
    }

    private void hideApp(String pkgName){
        try{
            boolean isResult;
            isResult = dpm.setApplicationHidden(adminCompName, pkgName,true);
            Log.d(TAG, "Hide Application Resutlt: ["+isResult+"]");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "hideApp:["+e+"], pkg : ["+this.getPackageName()+"]" );
        }
    }

    private void showApp(String pkgName){
        try{
            boolean isResult;
            isResult = dpm.setApplicationHidden(adminCompName, pkgName,false);
            Log.d(TAG, "Show Application Resutlt: ["+isResult+"]");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "showApp:["+e+"], pkg : ["+this.getPackageName()+"]" );
        }
    }

    //    Run application owner with this code
//      adb shell dpm set-device-owner com.example.devownerjav/.DevAdminReceiver
//      adb shell dpm set-active-admin --user current com.example.devownerjav/.DevAdminReceiver
//      adb shell dpm remove-active-admin com.example.devownerjav/.DevAdminReceiver
//      adb shell pm list packages -s
}
