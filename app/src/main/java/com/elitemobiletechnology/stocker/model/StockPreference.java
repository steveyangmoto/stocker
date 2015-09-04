package com.elitemobiletechnology.stocker.model;

/**
 * Created by SteveYang on 15/9/4.
 */
public class StockPreference {

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
