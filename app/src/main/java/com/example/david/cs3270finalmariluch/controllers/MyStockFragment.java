package com.example.david.cs3270finalmariluch.controllers;


import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.david.cs3270finalmariluch.MainActivity;
import com.example.david.cs3270finalmariluch.R;
import com.example.david.cs3270finalmariluch.models.MyStockModel;
import com.example.david.cs3270finalmariluch.utils.DatabaseHelper;
import com.example.david.cs3270finalmariluch.utils.MyStockExpandListAdapter;
import com.example.david.cs3270finalmariluch.utils.StockServiceHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyStockFragment extends Fragment {

    private View rootView;
    private MainActivity ma;
    private DatabaseHelper dbh;
    protected MyStockModel[] data;
    private StockServiceHelper mStockServiceHelper;
    private ExpandableListView elv_mystock;
    private int asyncCount;
    private int asyncDone;

    public MyStockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("test", "MyStockFragment.onCreate()");
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_stock, container, false);
        ma = (MainActivity) getActivity();
        dbh = new DatabaseHelper(getActivity(), "Stock", null, 1);
        elv_mystock = (ExpandableListView) rootView.findViewById(R.id.elv_mystock);
        displayStockTableToLogcat();
        buildMyStockList();
        return rootView;
    }

    private void displayStockTableToLogcat() {
        Cursor c = dbh.getAllStock();
        Log.d("test", "Contents of Stock DB:\n\t"+ DatabaseUtils.dumpCursorToString(c));
        c.close();
    }

    @Nullable
    @Override
    public View getView() {
        Log.d("test", "MyStockFragment.getView()");
        return super.getView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("test", "MyStockFragment.onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("test", "MyStockFragment.onPause()");
        if(dbh != null){
            dbh.close();
            Log.d("test", "\t\tClosed DBH");
        }
    }

    public void showLoadingCont(){
        Log.d("test", "MyStockFragment.showLoadingCont()");
        ma.showIndeterminateProgress();
    }

    public void hideLoadingCont(){
        Log.d("test", "MyStockFragment.hideLoadingCont()");
        ma.hideIndeterminateProgress();
    }

    public void showMyStockCont(){
        Log.d("test", "MyStockFragment.showMyStockCont()");
        ma.showContentContainer();
    }

    public void hideMyStockCont(){
        Log.d("test", "MyStockFragment.hideMyStockCont()");
        ma.hideContentContainer();
    }

    public void displayMyStockList(){
        Log.d("test", "MyStockFragment.displayMyStockList()");
        // show containers and hide progress
        if (data != null){
            if(data.length > 0){
                int i = 0;
                int j = 0;
                Log.d("test", "\t\t data[] contents : ");
                for(MyStockModel m : data){
                    i++;
                    if(m != null){
                        Log.d("test", "\t\t "+i+") "+m.toString()+" \n");
                    }
                }
                MyStockExpandListAdapter adapter = new MyStockExpandListAdapter(ma, getActivity(), data);
                elv_mystock.setAdapter(adapter);
                Log.d("test", "---- Created Adapter ----");
                hideLoadingCont();
                showMyStockCont();
                Log.d("test", "---- Set Adapter To ListView ----");
            }
        }
    }

    public void buildMyStockList(){
        Log.d("test", "MyStockFragment.buildMyStockList()");
        showLoadingCont();
        hideMyStockCont();
        asyncCount = 0;
        asyncDone = 0;
        ArrayList<String> tickers = null;
        String tic = null;
        Cursor c = dbh.getAllStock();
        if(c != null){
            Log.d("test", "\tCursor Not Null");
            try{
                tickers = new ArrayList<>();
                c.moveToPosition(-1);   // to start cursor iteration
                while(c.moveToNext()){
                    tic = c.getString(c.getColumnIndex("ticker"));
                    if(tic != null & tic != ""){
                        Log.d("test", "\tTic Not Null = "+tic);
                        tickers.add(tic);
                    }
                }
                c.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                c.close();
            }
        }

        if(tickers != null){
            Log.d("test", "\tTickers Not Null");
            if(tickers.size() > 0){
                Log.d("test", "\tTickers.size() > 0");
                Log.d("test", "\tTickers.size() = "+tickers.size());
                asyncCount = tickers.size();
                data = new MyStockModel[asyncCount];
                Log.d("test", "\t asyncCount = "+asyncCount);
                for(String t: tickers){
                    Log.d("test", "\t Current Ticker = "+t);
                    mStockServiceHelper = new StockServiceHelper(t, "i");
                    new ContactWebservice().execute(mStockServiceHelper);
                }
                //check that async count = async done in another async
                new AsyncValidation().execute();
            }else{
                hideLoadingCont();
            }
        }else{
            hideLoadingCont();
        }
    }


    private class AsyncValidation extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            while(asyncCount < asyncDone){
                //wait
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result == true){
                Log.d("test", "\nMyStockList Syncronization Done\n");
                displayMyStockList();
            }
        }
    }

    // change all this for my MyStockModel
    // need current price * shares to get stock val
    // need stock val - ptot / ptot  to get change

    private class ContactWebservice extends AsyncTask<StockServiceHelper, Void, Set<Map.Entry<String, JsonElement>>> {

        /* items        index       value
                        0           ticker
                        1           shares
                        2           purchaseTotal
                        3           shares value
                        4           change %
        */
        private ArrayList<String> items;

        @Override
        protected Set<Map.Entry<String, JsonElement>> doInBackground(StockServiceHelper...params) {
            this.items = new ArrayList<>();
            this.items.add(0, params[0].getSymbol());
            Cursor c = dbh.getOneStock(this.items.get(0));
            if(c != null){
                Log.d("test", "\tCursor Not Null");
                try{
                    c.moveToFirst();
                    this.items.add(c.getString(c.getColumnIndex("shares")));
                    this.items.add(c.getString(c.getColumnIndex("purchaseTotal")));
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    c.close();
                }
            }
            try {
                params[0].queryCurrent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return params[0].getEntries();    // this will all need to change so that I get the query data parsed
        }
        @Override
        protected void onPostExecute(Set<Map.Entry<String, JsonElement>> result) {
            Log.d("test", "MyStockFragment.ContactWebservice.onPostExecute()");
            // This is stuff that happens when my query is done
            if(result != null && result.size() > 0){
                Log.d("test", "\tRESULT:");
                float open = 0;
                float close = 0;
                if(result.size() >= 1){
                    for (Map.Entry<String, JsonElement> entry : result) {
                        String key = entry.getKey().substring(11, 16);
                        JsonObject subset = entry.getValue().getAsJsonObject();
                        open = subset.get("1. open").getAsFloat();
                        open = open * 1000;
                        open = Math.round(open);
                        open = open / 1000;
                        close = subset.get("4. close").getAsFloat();
                        close = close * 1000;
                        close = Math.round(close);
                        close = close / 1000;
                        Log.d("test", "\n\t Open = "+ open + "\t Close = "+ close);
                        break;
                    }
                    double curPrice = (double) open;
                    try{
                        double shares = Double.parseDouble(this.items.get(1));
                        double ptot = Double.parseDouble(this.items.get(2));
                        double shareVal = curPrice * shares;
                        shareVal = shareVal * 1000;
                        shareVal = Math.round(shareVal);
                        shareVal = shareVal / 1000;
                        double chg = ( (shareVal - ptot) / ptot );
                        Log.d("test", "\t\t ptot = "+ptot);
                        Log.d("test", "\t\t shareVal = "+shareVal);
                        this.items.add(3, String.valueOf(shareVal));
                        Log.d("test", "\t\t Change before conversion = "+chg);
                        if (chg < 0){
                            chg = (-1 * chg);
                            chg =  chg * 1000;
                            chg = Math.round(chg);
                            chg = chg / 1000;
                            chg = (-1 * chg);
                        }else{
                            chg =  chg * 1000;
                            chg = Math.round(chg);
                            chg = chg / 1000;
                        }
                        Log.d("test", "\t\t Change after conversion = "+chg);
                        this.items.add(4, String.valueOf(chg));
                    }catch (Exception e){
                        Log.d("test", "!!!! Didnt Convert Change Correctly !!!!");
                        e.printStackTrace();
                    }
                }
                Log.d("test", "\t\t ------Query Result Check------ ");
                Log.d("test", "\t\t asyncDone = "+asyncDone);
                Log.d("test", "\t\t ticker = "+this.items.get(0));
                Log.d("test", "\t\t shares = "+this.items.get(1));
                Log.d("test", "\t\t purchaseTot = "+this.items.get(2));
                Log.d("test", "\t\t shareVal = "+this.items.get(3));
                Log.d("test", "\t\t change = "+this.items.get(4));
                Log.d("test", "\t\t ------End Check------ ");
                data[asyncDone] = new MyStockModel(this.items.get(0), this.items.get(1), this.items.get(2), this.items.get(3), this.items.get(4));
                asyncDone++;
                Log.d("test", "\t\t asyncDone = "+asyncDone);

            }else{
                //Toast.makeText(getActivity(), "Couldn't Find Stock \nTicker: "+ ticker +"", Toast.LENGTH_SHORT).show();
                hideLoadingCont();
            }
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Void...values) {
        }
    }

}
