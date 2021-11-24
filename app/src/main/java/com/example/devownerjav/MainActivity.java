package com.example.devownerjav;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
    ComponentName adminCompName = null;

    private ListView lmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        /*ADMIN TEST PROGRAMATICALLY OWN DEVICE*/
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn = (Button) findViewById(R.id.button);
        lmView = (ListView) findViewById(R.id.listItems);

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

        Button btnActivate = findViewById(R.id.btnActivate);
        Button btnDeactivate = findViewById(R.id.btnDeactivate);
        lmView = findViewById(R.id.listItems);

        btnActivate.setOnClickListener(this);
        btnDeactivate.setOnClickListener(this);

        initLogger();
        initDeviceAdmin();

//        installedApps();
        IntentApps();

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


        //                activateKIOSK(false);
//                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
////                devicePolicyManager.clearDeviceOwnerApp(getPackageName()+".DevAdminReceiver");
//                devicePolicyManager.clearDeviceOwnerApp(getPackageName()+".DevAdminReceiver");

//                ComponentName cn = new ComponentName(getPackageName(), getPackageName() + ".DevAdminReceiver");
//                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//                devicePolicyManager.removeActiveAdmin(cn);

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
        adminCompName = new ComponentName(this, DevAdminReceiver.class);              // Initializing the component;

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
        List<PackageInfo> packList = this.getPackageManager().getInstalledPackages(0);
        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);
            String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            String packageName = packInfo.applicationInfo.packageName;
            String mainActivity = packInfo.applicationInfo.className ;
//            Log.e("App № " + Integer.toString(i), mainActivity+" : "+appName);



            if (packInfo.applicationInfo.className != null) {
                Log.i("App № " + Integer.toString(i), mainActivity+" : "+packageName);
                yArrayList.add(packageName);
            }

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                yArrayList);

        lmView.setAdapter(arrayAdapter);

        lmView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    String selectedItem = yArrayList.get(position);
                    String gCls = selectedItem+"."+MainActivity.class;
                    Toast.makeText(getApplicationContext(), gCls, Toast.LENGTH_SHORT).show();
                    PackageManager p = getPackageManager();
                    ComponentName componentName = new ComponentName(String.valueOf(this),gCls); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
                    Log.d(TAG, "onItemClick: "+gCls+":"+componentName);
                   p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                }catch (Exception e){
                    Log.e(TAG, "onItemClick: ",e );
                }
            }
        });
    }

    public void IntentApps(){

        List<String> yArrayList = new ArrayList<String>();
        List<String> xArrayList = new ArrayList<String>();

        final PackageManager pm = getPackageManager();

        Intent mIntent = new Intent(Intent.ACTION_MAIN,null);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> appList = pm.queryIntentActivities(mIntent,0);
        Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));

        for (ResolveInfo temp: appList){
            Log.i("AppLogs $ : ", "Package and Activity Name = " + temp.activityInfo.packageName+"    "+temp.activityInfo.name);
            yArrayList.add(temp.activityInfo.name);
            xArrayList.add(temp.activityInfo.packageName);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                yArrayList);

        lmView.setAdapter(arrayAdapter);

        lmView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    String selectedItem = yArrayList.get(position);
//                    PackageManager p = getPackageManager();
//                    ComponentName componentName = new ComponentName("YOUR_PACKAGE_NAME", selectedItem);
//                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    Log.w(TAG, "onItemClick: "+selectedItem );
                }catch (Exception e){
                    Log.e(TAG, "onItemClick: ",e );
                }
            }
        });
    }

    //    Run application owner with this code
//     adb shell dpm set-device-owner com.example.devownerjav/.DevAdminReceiver
//      adb shell dpm remove-active-admin com.example.devownerjav/.DevAdminReceiver
}
