package com.elitemobiletechnology.stockez;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.NumberPicker;

/**
 * Created by SteveYang on 15/11/29.
 */
public class MyApplication extends Application {
    private static final String APP_FIRST_LAUNCH = "app_first_launch";
    private static final String TAG = "MyApplication";
    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();
        StockUpdateAlarm alarm = new StockUpdateAlarm(appContext);
        alarm.startRepeatingAlarm();

    }

    public static Context getAppContext(){
        return appContext;
    }

}
