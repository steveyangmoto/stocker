package com.elitemobiletechnology.stockez;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.elitemobiletechnology.stockez.model.Stock;
import com.elitemobiletechnology.stockez.model.StockPreference;
import com.elitemobiletechnology.stockez.model.UserDataDAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "onCreate";
    private final ArrayList<Stock> myStocks = new ArrayList<Stock>();
    private ArrayList<StockPreference> userPreferences = null;
    StockGridAdapter gridAdapter;
    Handler handler = new Handler();
    ActionBar actionBar;
    ImageView buttonAdd;
    GridView gridview;
    TextView lastUpdated;
    long lastUpdateTime;
    final Runnable timerTask = new Runnable() {
        public void run() {
            long timeElapsed = (System.currentTimeMillis() - lastUpdateTime) / 1000 / 60;
            lastUpdated.setText(String.valueOf(timeElapsed));
            handler.postDelayed(this, 1000);
        }
    };

    private void displayNotifcation() {
        for(StockPreference stockPref:userPreferences){
            String stockSymbol = stockPref.getStockSymbol();
            String notifyOnChange = stockPref.getNotifyOnPercentChange();
            float stockPercentOnNotify = 0;
            try {
                Log.d(TAG,"stock percentOnNotify:"+notifyOnChange);
                stockPercentOnNotify = Float.parseFloat(notifyOnChange);
            }catch(NumberFormatException ignore){
                continue;
            }
            if(stockPercentOnNotify!=0) {
                for (Stock stock : myStocks) {
                    if (stock.getSymbol().equals(stockSymbol)) {
                        String stockPercentChange = stock.getPercentChange();
                        stockPercentChange = stockPercentChange.replace('%', ' ');
                        try {
                            float stockPercentFloat = Float.parseFloat(stockPercentChange);
                            Log.d(TAG,stockSymbol+ " stock percent change: "+stockPercentFloat);
                            Log.d(TAG,"stock percent on notify: "+stockPercentOnNotify);
                            if (Math.abs(stockPercentFloat)>=Math.abs(stockPercentOnNotify)) {
                                if(stockPercentFloat<0) {
                                    StockNotificationHelper.show(this.getApplicationContext(), stock.getSymbol(), false, stock.getLastTradePriceOnly(), stock.getPercentChange());
                                }else{
                                    StockNotificationHelper.show(this.getApplicationContext(), stock.getSymbol(), true, stock.getLastTradePriceOnly(), stock.getPercentChange());
                                }
                                break;
                            }
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lastUpdateTime = System.currentTimeMillis();
        buttonAdd = (ImageView) findViewById(R.id.ivAdd);
        lastUpdated = (TextView) findViewById(R.id.tvTimeUpdated);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogBox(null, null, false);
            }
        });
        gridview = (GridView) findViewById(R.id.gridview);
        gridAdapter = new StockGridAdapter(MainActivity.this, myStocks);
        gridview.setEmptyView(findViewById(R.id.empty_grid_view));
        gridview.setAdapter(gridAdapter);
        setActionBar();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Stock aStock = myStocks.get(position);
                    if (aStock != null) {
                        StockPreference pref = findStockPrefBySymbol(aStock.getSymbol());
                        openDialogBox(pref.getStockSymbol(), pref.getNotifyOnPercentChange(), true);
                    }
                } catch (Exception ignore) {
                }
            }
        });
        userPreferences = UserDataDAL.get().getUserStockPreference(MainActivity.this);
        new UpdateStockListTask().execute();
    }

    private StockPreference findStockPrefBySymbol(String symbol){
        for(StockPreference stockPref:userPreferences){
            if(stockPref.getStockSymbol().equals(symbol)){
                return stockPref;
            }
        }
        return null;
    }
    @Override
    public void onStart() {
        super.onStart();
        handler.postDelayed(timerTask, 1);
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(timerTask);
        super.onStop();
    }

    @Override public void onPause(){
        UserDataDAL.get().saveUserStockPreference(MainActivity.this, userPreferences);
        super.onPause();
    }

    public static enum Signal {
        NOT_FOUND, EXIST, SUCCESS
    }

    public class AddStockTask extends AsyncTask<StockPreference, Signal, Signal> {
        private static final String TAG = "StockDataDownloadTask";

        @Override
        protected Signal doInBackground(StockPreference... preference) {
            StockPreference stockInfo = preference[0];
            if (findStockPrefBySymbol(stockInfo.getStockSymbol())==null) {
                Stock stock = StockGrabber.get().getStock(stockInfo.getStockSymbol());
                if (stock != null) {
                    userPreferences.add(0,stockInfo);
                    myStocks.add(0, stock);
                    return Signal.SUCCESS;
                }
            } else {
                return Signal.EXIST;
            }
            return Signal.NOT_FOUND;
        }

        @Override
        protected void onPostExecute(Signal status) {
            if (status == Signal.SUCCESS) {
                gridAdapter.notifyDataSetChanged();
            } else if (status == Signal.NOT_FOUND) {
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.fail_to_retrieve_stock), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.stock_already_exist), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    public class UpdateStockListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(userPreferences.size()>0) {
                ArrayList<String> stockSymbols = new ArrayList<String>();
                for (StockPreference stockPref : userPreferences) {
                    stockSymbols.add(stockPref.getStockSymbol());
                }
                ArrayList<Stock> stocks = StockGrabber.get().getStocks(stockSymbols);
                if (stocks != null) {
                    myStocks.clear();
                    for (Stock stock : stocks) {
                        myStocks.add(stock);
                    }
                }
            }
            lastUpdateTime = System.currentTimeMillis();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            gridAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridview.setNumColumns(2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridview.setNumColumns(1);
        }
    }

    private void openDialogBox(final String stockSymbol, String rateForNotify, final boolean update) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_stock_dialog, null);
        final EditText etStockSymbol = (EditText) dialogView.findViewById(R.id.etStockSymbol);
        final EditText etNotify = (EditText) dialogView.findViewById(R.id.etNotify);
        final ImageButton cancel_dialog = (ImageButton) dialogView.findViewById(R.id.cancel_button);
        final ImageButton add_Stock = (ImageButton) dialogView.findViewById(R.id.add_button);
        final ImageButton trash_can = (ImageButton) dialogView.findViewById(R.id.trash_button);
        final TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvEditStock);
        builder.setView(dialogView);
        final AlertDialog myDialog = builder.create();
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        add_Stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stockSymbol = etStockSymbol.getText().toString();
                String percent = etNotify.getText().toString();
                StockPreference preference = new StockPreference(stockSymbol, percent);
                if (stockSymbol != null && !stockSymbol.isEmpty()) {
                    if (!update) {
                        new AddStockTask().execute(preference);
                    } else {
                        StockPreference stockPref = findStockPrefBySymbol(stockSymbol);
                        if(stockPref!=null) {
                            stockPref.setStockSymbol(preference.getStockSymbol());
                            stockPref.setNotifyOnPercentChange(preference.getNotifyOnPercentChange());
                        }
                    }
                }
                myDialog.cancel();
            }
        });
        if (stockSymbol != null && !stockSymbol.isEmpty()) {
            etStockSymbol.setText(stockSymbol);
        }
        if (rateForNotify != null && !rateForNotify.isEmpty()) {
            etNotify.setText(rateForNotify);
        }
        if (update) {
            add_Stock.setImageResource(R.mipmap.update_button1);
            tvTitle.setText(getString(R.string.edit_stock_text));
            etStockSymbol.setEnabled(false);
            etNotify.requestFocus();
            myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            trash_can.setVisibility(View.VISIBLE);
            trash_can.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popDeleteConfirmationDialog(myDialog, stockSymbol);
                }
            });
        }
        myDialog.show();
        etStockSymbol.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        etNotify.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    private void popDeleteConfirmationDialog(final Dialog parentDialog, final String stockToDelete) {
        String message = getString(R.string.delete_stock) + stockToDelete + "?";
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(MainActivity.this)
                .setMessage(message).setCancelable(false).setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Iterator iterator = userPreferences.iterator();
                        while (iterator.hasNext()) {
                            StockPreference stockPref = (StockPreference)iterator.next();
                            if(stockPref.getStockSymbol().equals(stockToDelete)){
                                iterator.remove();
                                break;
                            }
                        }
                        iterator = myStocks.iterator();
                        while(iterator.hasNext()){
                            Stock stock = (Stock)iterator.next();
                            if (stock.getSymbol().equals(stockToDelete)) {
                                myStocks.remove(stock);
                                parentDialog.cancel();
                                gridAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        AlertDialog confirmationDialog = confirmBuilder.create();
        confirmationDialog.show();
        Button alertYes = confirmationDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button alertNo = confirmationDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        alertYes.setBackgroundColor(getResources().getColor(R.color.stocker_green));
        alertYes.setTextColor(Color.WHITE);
        alertNo.setBackgroundColor(getResources().getColor(R.color.stocker_gray));
        alertNo.setTextColor(Color.WHITE);
        TextView messageText = (TextView) confirmationDialog.findViewById(android.R.id.message);
        messageText.setTextSize(13);
        messageText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

    }

    private void setActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF6CC5BA));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_layout, null);
        ImageView ivRefresh = (ImageView) viewActionBar.findViewById(R.id.ivRefresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNotifcation();
                Toast toast = Toast.makeText(MainActivity.this, "refreshing...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                new UpdateStockListTask().execute();
            }
        });
        actionBar.setCustomView(viewActionBar);
    }

}
