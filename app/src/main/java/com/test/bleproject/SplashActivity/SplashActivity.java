package com.test.bleproject.SplashActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.test.bleproject.R;
import com.test.bleproject.UI.MainActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    public static final String PREFS_NAME = "DevicePrefs";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if(isDeviceInfoStored()){
            //Data is already stored, proceed to main activity
            Log.d(TAG, "goToMainActivity data is already stored in SharedPreferences");
            goToMainActivity();
        }else{
            Log.d(TAG,"Data is missing, collect it and then proceed to main activity");
            collectAndStoreDeviceInfo();
        }

    }


    private boolean isDeviceInfoStored(){
        Log.d(TAG, "check if all device information is already stored in SharedPreferences");
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains("AndroidID") &&
                sharedPreferences.contains("DeviceModel") &&
                sharedPreferences.contains("BuildNumber") ;
    }

    private void collectAndStoreDeviceInfo(){
        Log.d(TAG, "Collecting device information...");

        String androidId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        String deviceModel = Build.MODEL;
        String buildNumber = Build.DISPLAY;
        String deviceBrand   = Build.BRAND;

        storeDeviceInfo(androidId,deviceModel,buildNumber, deviceBrand);
        goToMainActivity();
    }

    private void storeDeviceInfo(String androidId, String deviceModel, String buildNumber, String deviceBrand){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("AndroidID",androidId);
        editor.putString("DeviceModel",deviceModel);
        editor.putString("BuildNumber",buildNumber);
        editor.putString("DeviceBrand",deviceBrand);
        editor.apply();

        Log.d(TAG, "Device information saved in SharedPreferences");
    }

    private void goToMainActivity(){
        Log.d(TAG, "Delay for 1 second to simulate loading screen");

        new Handler(Looper.getMainLooper()).postDelayed(()->{
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1000); //1000 ms =  1 second delay
    }
}