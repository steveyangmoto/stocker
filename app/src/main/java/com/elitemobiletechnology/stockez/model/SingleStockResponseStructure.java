package com.elitemobiletechnology.stockez.model;

/**
 * Created by SteveYang on 4/9/15.
 */
public class SingleStockResponseStructure {
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
            private Stock quote;
            public Stock getQuote() {
                return quote;
            }
            public void setQuote(Stock quote) {
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
