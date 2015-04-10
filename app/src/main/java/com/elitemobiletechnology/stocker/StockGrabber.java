package com.elitemobiletechnology.stocker;

import android.net.Uri;
import android.util.Log;

import com.elitemobiletechnology.stocker.model.MultiStockResponseStructure;
import com.elitemobiletechnology.stocker.model.SingleStockResponseStructure;
import com.elitemobiletechnology.stocker.model.Stock;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by SteveYang on 4/9/15.
 */
public class StockGrabber {
    private static final String TAG = "StockGrabber";
    private Gson gson = new Gson();
    private static StockGrabber stockGrabber = new StockGrabber();
    private StockGrabber() {
    }

    public static StockGrabber get() {
        return stockGrabber;
    }

    public ArrayList<Stock> getStocks(ArrayList<String> stockSymobols) {
        String symbols = "";
        for (String symbol : stockSymobols) {
            symbols += symbol + ",";
        }
        int stockCount = stockSymobols.size();
        String url = "";
        try {
            Uri.Builder b = Uri.parse("http://query.yahooapis.com/v1/public/yql").buildUpon();
            b.appendQueryParameter("q", "select * from yahoo.finance.quotes where symbol in (\"" + symbols + "\")");
            b.appendQueryParameter("env", "http://datatables.org/alltables.env");
            b.appendQueryParameter("format", "json");
            url = b.build().toString();
        } catch (Exception e) {
        }
        HttpRequest request = HttpRequest.get(url);

        if (request.ok()) {
            String response = request.body();
            try {
                if(stockCount==1){
                    SingleStockResponseStructure responseStructure = gson.fromJson(response,SingleStockResponseStructure.class);
                    SingleStockResponseStructure.QUERY query = responseStructure.getQuery();
                    if (query != null) {
                        SingleStockResponseStructure.QUERY.RESULT results = query.getResults();
                        if (results != null) {
                            ArrayList<Stock> stockList = new ArrayList<Stock>();
                            stockList.add(results.getQuote());
                            return stockList;
                        }
                    }
                }else{
                    MultiStockResponseStructure responseStructure = gson.fromJson(response, MultiStockResponseStructure.class);
                    MultiStockResponseStructure.QUERY query = responseStructure.getQuery();
                    if (query != null) {
                        MultiStockResponseStructure.QUERY.RESULT results = query.getResults();
                        if (results != null) {
                            return results.getQuote();
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return null;
    }

}
