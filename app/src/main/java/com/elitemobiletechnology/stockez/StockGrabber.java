package com.elitemobiletechnology.stockez;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.elitemobiletechnology.stockez.model.MultiStockResponseStructure;
import com.elitemobiletechnology.stockez.model.MultiItemQuery;
import com.elitemobiletechnology.stockez.model.MultiItemResult;
import com.elitemobiletechnology.stockez.model.SingleItemQuery;
import com.elitemobiletechnology.stockez.model.SingleItemResult;
import com.elitemobiletechnology.stockez.model.SingleStockResponseStructure;
import com.elitemobiletechnology.stockez.model.Stock;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.net.URLEncoder;
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

    public Stock getStock(Context context,String symbol) {
        if(!StockezUtil.isNetworkAvailable(context)){
            return null;
        }
        HttpRequest request;
        try {
            Uri.Builder b = Uri.parse("http://query.yahooapis.com/v1/public/yql").buildUpon();
            b.appendQueryParameter("q", "select * from yahoo.finance.quotes where symbol in (\"" + symbol + "\")");
            b.appendQueryParameter("env", "http://datatables.org/alltables.env");
            b.appendQueryParameter("format", "json");


            request = HttpRequest.get(b.build().toString());
        } catch (Exception e) {
            return null;
        }

        if (request.ok()) {
            String response = request.body();
            try {
                SingleStockResponseStructure responseStructure = gson.fromJson(response, SingleStockResponseStructure.class);
                SingleItemQuery query = responseStructure.getQuery();
                if (query != null) {
                    SingleItemResult results = query.getResults();
                    if (results != null) {
                        Stock stock = results.getQuote();
                        if (stock.getSymbol() != null && stock.getName() != null && stock.getLastTradePriceOnly() != null && stock.getPercentChange() != null) {
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

    public ArrayList<Stock> getStocks(Context context,ArrayList<String> stockSymobols) {
        String symbols = "";
        if(stockSymobols.size()<=0||!StockezUtil.isNetworkAvailable(context)){
            return null;
        }
        for (String symbol : stockSymobols) {
            symbols += symbol + ",";
        }
        HttpRequest request=null;
        try {
            Uri.Builder b = Uri.parse("http://query.yahooapis.com/v1/public/yql").buildUpon();
            b.appendQueryParameter("q", "select * from yahoo.finance.quotes where symbol in (\"" + symbols + "\")");
            b.appendQueryParameter("env", "http://datatables.org/alltables.env");
            b.appendQueryParameter("format", "json");

            request = HttpRequest.get(b.build().toString());
        }
        catch (Exception e) {
            return null;
        }

        if (request!=null&&request.ok()) {

            String response = request.body();
            try {
                MultiStockResponseStructure responseStructure = gson.fromJson(response, MultiStockResponseStructure.class);
                MultiItemQuery query = responseStructure.getQuery();
                if (query != null) {
                    MultiItemResult results = query.getResults();
                    if (results != null) {
                        return results.getQuote();
                    }
                }
            } catch (Exception e) {
                try{
                    SingleStockResponseStructure responseStructure = gson.fromJson(response, SingleStockResponseStructure.class);
                    SingleItemQuery query = responseStructure.getQuery();
                    if (query != null) {
                        SingleItemResult result = query.getResults();
                        if (result != null) {
                            ArrayList<Stock> stockList = new ArrayList<>();
                            stockList.add(result.getQuote());
                            return stockList;
                        }
                    }
                }catch(Exception ignore){}
                Log.e(TAG, e.getMessage());
            }
        }
        return null;
    }

}
