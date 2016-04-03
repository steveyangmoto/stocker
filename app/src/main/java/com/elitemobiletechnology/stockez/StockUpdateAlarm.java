package com.elitemobiletechnology.stockez;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by SteveYang on 15/11/29.
 */
public class StockUpdateAlarm {
    private static long STOCK_UPDATE_INTERVAL = 2*60*1000;
    private static AlarmManager alarm;
    private static PendingIntent updateStockPendingIntent;
    private Context mContext;

    public StockUpdateAlarm(Context context) {
        this.mContext = context;
    }

    public void startRepeatingAlarm() {
        if (mContext != null) {
            Intent updateStockIntent = new Intent(mContext, StockUpdateService.class);
            updateStockIntent.putExtra(StockUpdateService.SHOW_NOTIFICATION, true);
            updateStockPendingIntent = PendingIntent.getService(mContext, 0, updateStockIntent, 0);
            alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+STOCK_UPDATE_INTERVAL, STOCK_UPDATE_INTERVAL, updateStockPendingIntent);
        }
    }

}
