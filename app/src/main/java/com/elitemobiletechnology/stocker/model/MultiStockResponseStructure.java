package com.elitemobiletechnology.stocker.model;

import java.util.ArrayList;

/**
 * Created by SteveYang on 4/9/15.
 */
public class MultiStockResponseStructure {
    private QUERY query;
    public static class QUERY{
        private RESULT results;
        public RESULT getResults() {
            return results;
        }

        public void setResults(RESULT results) {
            this.results = results;
        }

        public static class RESULT{
            private ArrayList<Stock> quote;
            public ArrayList<Stock> getQuote() {
                return quote;
            }
            public void setQuote(ArrayList<Stock> quote) {
                this.quote = quote;
            }
        }
    }

    public QUERY getQuery() {
        return query;
    }

    public void setQuery(QUERY query) {
        this.query = query;
    }
}
