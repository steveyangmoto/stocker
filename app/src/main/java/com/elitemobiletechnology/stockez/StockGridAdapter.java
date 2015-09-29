package com.elitemobiletechnology.stockez;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elitemobiletechnology.stockez.model.Stock;

import java.util.ArrayList;

/**
 * Created by SteveYang on 4/8/15.
 */
public class StockGridAdapter extends BaseAdapter {
    private ArrayList<Stock> stocks;
    private Context context;
    private LayoutInflater inflater;

    public StockGridAdapter(Context c,ArrayList<Stock> stocks) {
        this.stocks = stocks;
        this.context = c;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return stocks.size();
    }

    @Override
    public Object getItem(int position) {
        return stocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stocks.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Stock stock = stocks.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.stock_item_layout, null);
            holder.stockName = (TextView) convertView.findViewById(R.id.tvStockName);
            holder.stockPrice = (TextView) convertView.findViewById(R.id.tvStockPrice);
            holder.stockSymbol = (TextView) convertView.findViewById(R.id.tvStockSymbol);
            holder.stockPriceChange = (TextView)convertView.findViewById(R.id.tvPriceChange);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        String symbol = stock.getSymbol();
        if(symbol!=null){
            symbol=symbol.toUpperCase();
            holder.stockSymbol.setText("["+symbol+"]");
        }
        holder.stockName.setText(stock.getName());
        String price = stock.getLastTradePriceOnly();
        String change = stock.getPercentChange();
        holder.stockPriceChange.setText(change);
        change = change.replace('%',' ');
        Double priceInDouble = 0d;
        Double changeInDouble = 0d;
        try{
            priceInDouble = Double.parseDouble(price);
            holder.stockPrice.setText("$"+String.format("%.2f",priceInDouble));
            changeInDouble = Double.parseDouble(change);
            if(changeInDouble>0){
                holder.stockPriceChange.setTextColor(Color.parseColor("#42be38"));
            }else if(changeInDouble<0){
                holder.stockPriceChange.setTextColor(Color.parseColor("#ff0000"));
            }
        }catch(NumberFormatException ignore){
            holder.stockPrice.setText("$"+price);
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView stockName;
        public TextView stockPrice;
        public TextView stockSymbol;
        public TextView stockPriceChange;
    }
}
