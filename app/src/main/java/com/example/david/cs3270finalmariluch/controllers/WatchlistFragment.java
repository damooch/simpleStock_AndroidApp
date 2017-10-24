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
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.david.cs3270finalmariluch.MainActivity;
import com.example.david.cs3270finalmariluch.R;
import com.example.david.cs3270finalmariluch.models.WatchlistModel;
import com.example.david.cs3270finalmariluch.utils.DatabaseHelper;
import com.example.david.cs3270finalmariluch.utils.StockServiceHelper;
import com.example.david.cs3270finalmariluch.utils.WatchlistCustomAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class WatchlistFragment extends Fragment {

    private View rootView;
    private MainActivity ma;
    private DatabaseHelper dbh;
    protected WatchlistModel[] data;
    private StockServiceHelper mStockServiceHelper;
    private ListView mDrawerList;
    private int asyncCount;
    private int asyncDone;
    private ContactWebservice mContactWebservices;
    private AsyncValidation mAsyncValidation;

    public WatchlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("test", "WatchlistFragment.onCreate()");
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_watchlist, container, false);
        ma = (MainActivity) getActivity();
        dbh = new DatabaseHelper(getActivity(), "Stock", null, 1);
        mDrawerList = (ListView) rootView.findViewById(R.id.lv_watchlist);
        displayWatchistTableToLogcat();
        buildWatchlist();
        return rootView;
    }
    // 1) THIS IS THE ON CLICK LISTENER IM HAVING ISSUES WITH !!!!
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("test", "WatchlistFragment.DrawerItemClickListener.onItemClick()");
            Log.d("test", "Current selected position: "+position);
            //selectDrawerItem(position, false);
            if(data != null){
                ma.selectDrawerItem(1, true);
                ma.displaySearchFragment(data[position].ticker);   // still need to handle when a ticker is passed currently having issues
            }
        }
    }

    private void displayWatchistTableToLogcat() {
        Cursor c = dbh.getAllWatchlist();
        Log.d("test", "Contents of Watchlist DB:\n\t"+ DatabaseUtils.dumpCursorToString(c));
        c.close();
    }

    @Nullable
    @Override
    public View getView() {
        Log.d("test", "WatchlistFragment.getView()");
        return super.getView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("test", "WatchlistFragment.onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("test", "WatchlistFragment.onPause()");
        if(mContactWebservices != null){
            mContactWebservices.cancel(true);
        }
        if(mAsyncValidation != null){
            mAsyncValidation.cancel(true);
        }
        if(dbh != null){
            dbh.close();
            Log.d("test", "\t\tClosed DBH");
        }
    }

    public void displayWaitingView(){

    }

    public void displayWatchlist(){
        Log.d("test", "WatchlistFragment.displayWatchlist()");
        // show containers and hide progress
        if (data != null){
            if(data.length > 0){
                Log.d("test", "\t\t data.length = "+data.length);
                int i = 0;
                int j = 0;
                Log.d("test", "\t\t data[] contents : ");
                for(WatchlistModel w : data){
                    i++;
                    Log.d("test", "\t\t "+i+") "+w.toString()+" \n");
                }
                WatchlistCustomAdapter adapter = new WatchlistCustomAdapter(ma, getActivity(), R.layout.list_view_watchlist, data);
                hideLoadingCont();
                Log.d("test", "---- Created Adapter ----");
                showWatchlistCont();
                mDrawerList.setAdapter(adapter);
                mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
                Log.d("test", "---- Set Adapter To ListView ----");
            }
        }
    }

    public void buildWatchlist(){
        Log.d("test", "WatchlistFragment.buildWatchlist()");
        asyncCount = 0;
        asyncDone = 0;
        ArrayList<String> tickers = null;
        String tic = null;
        Cursor c = dbh.getAllWatchlist();
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
                data = new WatchlistModel[asyncCount];
                Log.d("test", "\t asyncCount = "+asyncCount);
                for(String t: tickers){
                    Log.d("test", "\t Current Ticker = "+t);
                    mStockServiceHelper = new StockServiceHelper(t, "i");
                    mContactWebservices = new ContactWebservice();
                    mContactWebservices.execute(mStockServiceHelper);
                }
                //check that async count = async done in another async
                mAsyncValidation = new AsyncValidation();
                mAsyncValidation.execute();
            }else{
                hideLoadingCont();
            }
        }else{
            hideLoadingCont();
        }
    }

    public void showLoadingCont(){
        ma.showIndeterminateProgress();
    }

    public void hideLoadingCont(){
        Log.d("test", "WatchlistFragment.hideLoadingCont()");
        ma.hideIndeterminateProgress();
    }

    public void showWatchlistCont(){
        Log.d("test", "WatchlistFragment.showWatchlistCont()");
        ma.showContentContainer();
    }

    public void hideWatchlistCont(){
        ma.hideContentContainer();
    }

    // There is an issue with async task error if you rotate while its running
    // though I may have solved it by cancelling the async tasks in onPause
    private class AsyncValidation extends  AsyncTask<Void, Boolean, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("test", "WatchlistFragment.AsyncValidation.doInBackground()");
            while(asyncCount < asyncDone){
                //wait
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("test", "WatchlistFragment.AsyncValidation.onPostExecute()");
            if(result == true){
                Log.d("test", "\nWatchlist Syncronization Done\n");
                displayWatchlist();
            }
        }
    }

    private class ContactWebservice extends AsyncTask<StockServiceHelper, Void, Set<Map.Entry<String, JsonElement>>> {

        private ArrayList<String> items;

        @Override
        protected  Set<Map.Entry<String, JsonElement>> doInBackground(StockServiceHelper...params) {
            Log.d("test", "WatchlistFragment.ContactWebservice.doInBackground()");
            this.items = new ArrayList<>();
            this.items.add(0, params[0].getSymbol());
            try {
                params[0].queryCurrent();
                //params[0].queryService();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return params[0].getEntries();    // this will all need to change so that I get the query data parsed
        }
        @Override
        protected void onPostExecute( Set<Map.Entry<String, JsonElement>> result) {
            Log.d("test", "SearchFragment.ContactWebservice.onPostExecute()");
            // This is stuff that happens when my query is done
            if(result != null && result.size() > 0){
                Log.d("test", "\tRESULT:");
                int i = 0;
                float open = 0;
                float close = 0;
                if(result.size() >= 1) {
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
                        Log.d("test", "\n\t Open = " + open + "\t Close = " + close);
                        break;
                    }
                    this.items.add(1, close+"");
                    this.items.add(2, open+"");
                    try{
                        double opn = close;
                        Log.d("test", "\t\tOpen = "+opn);
                        double cur = open;
                        Log.d("test", "\t\tCurrent = "+cur);
                        double chg = ((cur - opn) / opn);
                        Log.d("test", "\t\tChange before conversion = "+chg);
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
                        Log.d("test", "\t\tChange after conversion = "+chg);
                        this.items.add(3, String.valueOf(chg));
                    }catch (Exception e){
                        Log.d("test", "!!!! Didnt Convert Change Correctly !!!!");
                        e.printStackTrace();
                    }
                }
                Log.d("test", "\t\t ------Query Result Check------ ");
                Log.d("test", "\t\t asyncDone = "+asyncDone);
                Log.d("test", "\t\t ticker = "+this.items.get(0));
                Log.d("test", "\t\t open = "+this.items.get(1));
                Log.d("test", "\t\t current = "+this.items.get(2));
                Log.d("test", "\t\t change = "+this.items.get(3));
                Log.d("test", "\t\t ------End Check------ ");
                data[asyncDone] = new WatchlistModel(this.items.get(0), this.items.get(1), this.items.get(2), this.items.get(3));
                asyncDone++;
                Log.d("test", "\t\t asyncDone = "+asyncDone);

            }else{
                //Toast.makeText(getActivity(), "Couldn't Find Stock \nTicker: "+ ticker +"", Toast.LENGTH_SHORT).show();
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
