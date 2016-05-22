package com.elitemobiletechnology.stockez;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.elitemobiletechnology.stockez.model.Stock;
import com.elitemobiletechnology.stockez.model.UserDataDAL;

import java.util.ArrayList;

/**
 * Created by SteveYang on 15/10/6.
 */
public class StockUpdateService extends IntentService {
    private static final String TAG = "StockUpdateService";
    public static final String SHOW_NOTIFICATION = "show_notification";

    public StockUpdateService() {
        super("com.elitemobiletechnology.stockez.StockUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(TAG, "onHandleIntent");
        if (StockezUtil.isNetworkAvailable(this.getApplicationContext())) {
            try {
                ArrayList<Stock> myStocks = UserDataDAL.get().getUserStockPreference(this.getApplicationContext());
                if (myStocks != null && myStocks.size() > 0) {
                    ArrayList<String> stockSymbols = new ArrayList<String>();
                    for (Stock stock : myStocks) {
                        stockSymbols.add(stock.getSymbol());
                    }
                    ArrayList<Stock> stocks = StockGrabber.get().getStocks(getApplicationContext(),stockSymbols);
                    if (stocks != null) {
                        for (Stock stock : stocks) {
                            String stockSymbol = stock.getSymbol();
                            for (Stock oldStock : myStocks) {
                                if (oldStock.getSymbol().equals(stockSymbol)) {
                                    oldStock.updateData(stock);
                                    break;
                                }
                            }
                        }
                        UserDataDAL.get().saveUserStockPreference(StockUpdateService.this, myStocks);
                        if (intent.getBooleanExtra(SHOW_NOTIFICATION, false)) {
                            showStockNotification(myStocks);
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            StockezUtil.putPrefLong(MyApplication.getAppContext(),StockConstants.LAST_UPDATE_TIME,System.currentTimeMillis());
        }
    }

    private void showStockNotification(ArrayList<Stock> myStocks) {
        for (Stock stock : myStocks) {
            String stockSymbol = stock.getSymbol();
            String notifyOnChange = stock.getEliteMobileTechnologyPercentOnNotify();
            float stockPercentOnNotify = 0;
            try {
                stockPercentOnNotify = Float.parseFloat(notifyOnChange);
            } catch (NumberFormatException ignore) {
                continue;
            }
            if (stockPercentOnNotify > 0 && stock.getSymbol().equals(stockSymbol)) {
                String stockPercentChange = stock.getPercentChange();
                stockPercentChange = stockPercentChange.replace('%', ' ');
                try {
                    float stockPercentFloat = Float.parseFloat(stockPercentChange);
                    if (Math.abs(stockPercentFloat) >= Math.abs(stockPercentOnNotify)) {
                        if (stockPercentFloat < 0) {
                            StockNotificationHelper.show(this.getApplicationContext(), stock.getSymbol(), false, stock.getLastTradePriceOnly(), stock.getPercentChange());
                        } else {
                            StockNotificationHelper.show(this.getApplicationContext(), stock.getSymbol(), true, stock.getLastTradePriceOnly(), stock.getPercentChange());
                        }
                    }
                } catch (NumberFormatException ignore) {
                }
            }
        }
    }


}
