package com.elitemobiletechnology.stockez.model;

import java.io.Serializable;

/**
 * Created by SteveYang on 15/9/4.
 */
public class StockPreference implements Serializable{

    private String stockSymbol;
    private String notifyOnPercentChange;

    public StockPreference(String symbol, String percent){
        this.stockSymbol = symbol;
        this.notifyOnPercentChange = percent;
    }
    public String getNotifyOnPercentChange() {
        return notifyOnPercentChange;
    }

    public void setNotifyOnPercentChange(String notifyOnPercentChange) {
        this.notifyOnPercentChange = notifyOnPercentChange;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }
}
