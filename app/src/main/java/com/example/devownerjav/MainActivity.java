package com.example.devownerjav;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager dpm ;
    private ComponentName cn;
    public String TAG = "devOwner : ";
    public String TAGE = "devOwnerER : ";

    // KIOSK
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_UP,KeyEvent.KEYCODE_VOLUME_DOWN));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DISABLEDS LOCK SCREEN
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//        cn = ComponentName.createRelative(getPackageName(), ".DevAdminReceiver");
        cn = new ComponentName(getPackageName() , String.valueOf(DevAdminReceiver.class));
        try{

        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(dpm.isDeviceOwnerApp(getPackageName())){
            Log.d(TAG, "is device owner ");
        }else{
            Log.d(TAG, "not device owner ");
        }

        Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DenieOwner();
            }
        });
    }



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

    private void DenieOwner(){
        try{

            Log.d(TAG, this.getPackageName()+".DevAdminReceiver");
            dpm.clearDeviceOwnerApp(this.getPackageName()+".DevAdminReceiver");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "DenieOwner: "+e+"" );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // nothing to do here
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(blockedKeys.contains(event.getKeyCharacterMap())){
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus){
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    //    Run application owner with this code
//     adb shell dpm set-device-owner com.example.devownerjav/.DevAdminReceiver
//      adb shell dpm remove-active-admin com.example.devownerjav/.DevAdminReceiver
}
