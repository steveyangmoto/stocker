package com.elitemobiletechnology.stocker.model;

/**
 * Created by SteveYang on 4/9/15.
 */
public class Stock {
    private String symbol;
    private String LastTradePriceOnly;
    private String Name;
    private String PercentChange;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLastTradePriceOnly() {
        return LastTradePriceOnly;
    }

    public void setLastTradePriceOnly(String lastTradePriceOnly) {
        LastTradePriceOnly = lastTradePriceOnly;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPercentChange() {
        return PercentChange;
    }

    public void setPercentChange(String percentChange) {
        PercentChange = percentChange;
    }

}
