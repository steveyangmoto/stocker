package com.elitemobiletechnology.stockez;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by SteveYang on 15/9/29.
 */
public class StockNotificationHelper {
    private static final String TAG = "StockNotificationHelper";

    public static void show(Context context, String stockSymbol, boolean priceUp, String price, String changePercentage) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.layout_notification);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>");
        stringBuilder.append(stockSymbol);
        stringBuilder.append("</b>");
        stringBuilder.append(" " + context.getString(R.string.has) + " ");
        if (priceUp) {
            stringBuilder.append("<font color='#42be38'>");
            stringBuilder.append(context.getString(R.string.risen));
            stringBuilder.append("</font>");
        } else {
            stringBuilder.append("<font color='#ff0000'>");
            stringBuilder.append(context.getString(R.string.dropped));
            stringBuilder.append("</font>");
        }
        stringBuilder.append(" " + context.getString(R.string.to) + " ");
        stringBuilder.append("<b>$" + price + "</b>");
        stringBuilder.append("(" + changePercentage + ")");
        remoteViews.setTextViewText(R.id.stockInfo, Html.fromHtml(stringBuilder.toString()));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher).setContent(
                remoteViews).setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Log.d(TAG, "visible");
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(stockSymbol.hashCode(), mBuilder.build());
        Log.d(TAG, "stock hashcocde: " + stockSymbol.hashCode());

    }
}
