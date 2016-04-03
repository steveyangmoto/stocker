package com.elitemobiletechnology.stockez.model;

import java.util.ArrayList;

/**
 * Created by SteveYang on 16/4/3.
 */
public class MultiItemResult {
    private ArrayList<Stock> quote;
    public ArrayList<Stock> getQuote() {
        return quote;
    }
    public void setQuote(ArrayList<Stock> quote) {
        this.quote = quote;
    }
}