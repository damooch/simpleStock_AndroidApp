package com.example.david.cs3270finalmariluch.models;

import android.util.Log;

/**
 * Created by David on 7/24/2017.
 */

public class WatchlistModel {
    public String ticker;
    public String open;
    public String current;
    public String change;

    // Constructor.
    public WatchlistModel(String ticker, String open, String current, String chg) {
        Log.d("test", "WatchlistModel.WatchlistModel() 'Constructor'");
        this.ticker = ticker;
        this.open = open;
        this.current = current;
        this.change = chg;
    }

    @Override
    public String toString() {
        return "Ticker: "+ticker+"\t Open: "+open+"\t Current: "+current+"\t Change: "+change+"";
    }
}
