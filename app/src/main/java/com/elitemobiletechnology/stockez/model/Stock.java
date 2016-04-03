package com.elitemobiletechnology.stockez.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SteveYang on 4/9/15.
 */
public class Stock implements Serializable {
    static final long serialVersionUID = 1L; //assign a long value

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("LastTradePriceOnly")
    private String lastTradePriceOnly;

    @SerializedName("Name")
    private String name;

    @SerializedName("PercentChange")
    private String percentChange;

    private String eliteMobileTechnologyPercentOnNotify;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLastTradePriceOnly() {
        return lastTradePriceOnly;
    }

    public void setLastTradePriceOnly(String lastTradePriceOnly) {
        this.lastTradePriceOnly = lastTradePriceOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    public String getEliteMobileTechnologyPercentOnNotify() {
        return eliteMobileTechnologyPercentOnNotify;
    }

    public void setEliteMobileTechnologyPercentOnNotify(String eliteMobileTechnologyPercentOnNotify) {
        this.eliteMobileTechnologyPercentOnNotify = eliteMobileTechnologyPercentOnNotify;
    }

    public void updateData(Stock newStockInfo) {
        this.symbol = newStockInfo.getSymbol();
        this.lastTradePriceOnly = newStockInfo.getLastTradePriceOnly();
        this.name = newStockInfo.getName();
        this.percentChange = newStockInfo.getPercentChange();
    }

}
