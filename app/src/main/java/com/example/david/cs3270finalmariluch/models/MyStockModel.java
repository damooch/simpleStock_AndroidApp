package com.example.david.cs3270finalmariluch.models;

import android.util.Log;

/**
 * Created by David on 7/24/2017.
 */

public class MyStockModel {
    public String ticker;
    public String shares;
    public String purchaseTotal;
    public String stockValue;
    public String change;

    // Constructor.
    public MyStockModel(String ticker, String shares, String purchaseTot, String stockValue, String chg) {
        Log.d("test", "MyStockModel.MyStockModel() 'Constructor'");
        this.ticker = ticker;
        this.shares = shares;
        this.purchaseTotal = purchaseTot;
        this.stockValue = stockValue;
        this.change = chg;
    }

    @Override
    public String toString() {
        return "Ticker: "+ticker+"\t Shares: "+shares+"\t PurchaseTot: "+purchaseTotal+"\t StockVal: "+stockValue+"\t Change: "+change;
    }
}
