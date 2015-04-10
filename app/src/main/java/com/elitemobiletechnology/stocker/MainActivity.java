package com.elitemobiletechnology.stocker;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elitemobiletechnology.stocker.model.Stock;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "onCreate";
    ArrayList<Stock> myStocks = new ArrayList<Stock>();
    HashMap<String,Boolean> stockMap = new HashMap<String,Boolean>();
    Handler handler = new Handler();
    ActionBar actionBar;
    ImageView buttonAdd;
    GridView gridview;
    TextView lastUpdated;
    long lastUpdateTime;
    final Runnable timerTask = new Runnable() {
        public void run() {
            long timeElapsed = (System.currentTimeMillis()-lastUpdateTime)/1000/60;
            lastUpdated.setText(String.valueOf(timeElapsed));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate");
        lastUpdateTime = System.currentTimeMillis();
        buttonAdd = (ImageView) findViewById(R.id.ivAdd);
        lastUpdated = (TextView) findViewById(R.id.tvTimeUpdated);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogBox();
            }
        });
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setEmptyView(findViewById(R.id.empty_grid_view));
        setActionBar();
    }

    @Override public void onStart(){
        super.onStart();
        handler.postDelayed(timerTask,1);
    }

    @Override public void onStop(){
        handler.removeCallbacks(timerTask);
        super.onStop();
    }

    public class AddStockTask extends AsyncTask<String, Void, ArrayList<Stock>> {
        private static final String TAG = "StockDataDownloadTask";

        @Override
        protected ArrayList<Stock> doInBackground(String... symbols) {
            ArrayList<String> stockSymbols = new ArrayList<String>();
            for (int i = 0; i < symbols.length; i++) {
                stockSymbols.add(symbols[i]);
            }
            ArrayList<Stock> stocks = StockGrabber.get().getStocks(stockSymbols);
            return stocks;
        }

        @Override
        protected void onPostExecute(ArrayList<Stock> stocks) {
            if (stocks != null) {
                for (Stock stock : stocks) {
                    String stockName = stock.getName();
                    String stockSymbol = stock.getSymbol();
                    String stockPrice = stock.getLastTradePriceOnly();
                    if (stockName != null && stockSymbol!=null&& stockPrice != null) {
                        if(stockMap.get(stockSymbol)==null) {
                            myStocks.add(0, stock);
                            stockMap.put(stockSymbol,true);
                        }
                    }
                }
                gridview.setAdapter(new StockGridAdapter(MainActivity.this, myStocks));
            }
        }
    }

    public class UpdateStockListTask extends AddStockTask {
        @Override
        protected void onPostExecute(ArrayList<Stock> stocks) {
            if (stocks != null) {
                myStocks.clear();
                for (Stock stock : stocks) {
                    myStocks.add(stock);
                }
                gridview.setAdapter(new StockGridAdapter(MainActivity.this, myStocks));
                lastUpdated.setText("0");
                lastUpdateTime = System.currentTimeMillis();
            }
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

    private void openDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_stock_dialog, null);
        final EditText etStockSymbol = (EditText) dialogView.findViewById(R.id.etStockSymbol);
        ImageButton cancel_dialog = (ImageButton) dialogView.findViewById(R.id.cancel_button);
        ImageButton add_Stock = (ImageButton) dialogView.findViewById(R.id.add_button);
        ImageButton upArrow = (ImageButton) dialogView.findViewById(R.id.ibUp);
        ImageButton downArrow = (ImageButton) dialogView.findViewById(R.id.ibDown);
        final EditText etPercentChange = (EditText) dialogView.findViewById(R.id.etNotify);


        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int change = 0;
                try {
                    change = Integer.parseInt(etPercentChange.getText().toString()) + 1;
                    etPercentChange.setText(String.valueOf(change));
                } catch (NumberFormatException e) {
                    change = 0;
                    etPercentChange.setText(String.valueOf(change));
                }
            }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int change = 0;
                try {
                    change = Integer.parseInt(etPercentChange.getText().toString()) - 1;
                    etPercentChange.setText(String.valueOf(change));
                } catch (NumberFormatException e) {
                    change = 0;
                    etPercentChange.setText(String.valueOf(change));
                }
            }
        });

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
                if (!stockSymbol.isEmpty()) {
                    new AddStockTask().execute(new String[]{stockSymbol});
                }
                myDialog.cancel();
            }
        });
        myDialog.show();
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
                String[] stockList = new String[myStocks.size()];
                for (int i=0;i<myStocks.size();i++) {
                     stockList[i] = myStocks.get(i).getSymbol();
                }
                new UpdateStockListTask().execute(stockList);
            }
        });
        actionBar.setCustomView(viewActionBar);
    }
}
