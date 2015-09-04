package com.elitemobiletechnology.stocker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.elitemobiletechnology.stocker.model.Stock;
import com.elitemobiletechnology.stocker.model.StockPreference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "onCreate";
    ArrayList<Stock> myStocks = new ArrayList<Stock>();
    HashMap<String, StockPreference> userPreferences = new HashMap<String, StockPreference>();
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
                        StockPreference pref = userPreferences.get(aStock.getSymbol());
                        openDialogBox(pref.getStockSymbol(), pref.getNotifyOnPercentChange(), true);
                    }
                } catch (Exception ignore) {
                }
            }
        });

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

    public class AddStockTask extends AsyncTask<StockPreference, Boolean, Boolean> {
        private static final String TAG = "StockDataDownloadTask";

        @Override
        protected Boolean doInBackground(StockPreference... preference) {
            StockPreference stockInfo = preference[0];
            if (!userPreferences.containsKey(stockInfo.getStockSymbol())) {
                Stock stock = StockGrabber.get().getStock(stockInfo.getStockSymbol());
                if (stock != null) {
                    userPreferences.put(stock.getSymbol(), stockInfo);
                    myStocks.add(0, stock);
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean changed) {
            if (changed) {
                gridAdapter.notifyDataSetChanged();
            } else {
                Toast toast = Toast.makeText(MainActivity.this, getString(R.string.fail_to_retrieve_stock), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }


    }

    public class UpdateStockListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> stockSymbols = new ArrayList<String>();
            for (int i = 0; i < myStocks.size(); i++) {
                stockSymbols.add(myStocks.get(i).getSymbol());
            }
            ArrayList<Stock> stocks = StockGrabber.get().getStocks(stockSymbols);
            myStocks = stocks;
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
                        userPreferences.put(stockSymbol, preference);
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
                        userPreferences.remove(stockToDelete);
                        gridAdapter.notifyDataSetChanged();
                        for (Stock stock : myStocks) {
                            if (stock.getSymbol().equals(stockToDelete)) {
                                myStocks.remove(stock);
                                parentDialog.cancel();
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
        TextView messageText = (TextView)confirmationDialog.findViewById(android.R.id.message);
        messageText.setTextSize(13);
        messageText.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

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
                new UpdateStockListTask().execute();
            }
        });
        actionBar.setCustomView(viewActionBar);
    }
}
