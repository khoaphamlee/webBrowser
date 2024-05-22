package com.example.sky.batteryBc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;

public class MyBatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;

            if (batteryPct < 20) {
                SharedPreferences share = context.getSharedPreferences("pref2", 0);
                int searchSP2 = share.getInt("searchSP2", 4);
                SharedPreferences.Editor editor = share.edit();
                if(searchSP2 == 2){
                    editor.putBoolean("nightMode", true);
                }
            }
            else{
                SharedPreferences share = context.getSharedPreferences("pref2", 0);
                int searchSP2 = share.getInt("searchSP2", 4);
                SharedPreferences.Editor editor = share.edit();
                if(searchSP2 == 2){
                    editor.putBoolean("nightMode", false);
                }
            }
        }
    }
}
