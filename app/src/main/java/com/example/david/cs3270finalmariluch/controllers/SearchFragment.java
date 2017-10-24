package com.example.david.cs3270finalmariluch.controllers;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.XYPlot;
import com.example.david.cs3270finalmariluch.MainActivity;
import com.example.david.cs3270finalmariluch.R;
import com.example.david.cs3270finalmariluch.data.nasdaq;
import com.example.david.cs3270finalmariluch.data.nyse;
import com.example.david.cs3270finalmariluch.utils.DatabaseHelper;
import com.example.david.cs3270finalmariluch.utils.StockServiceHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.david.cs3270finalmariluch.MainActivity.hideSoftKeyboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static String SHOW = "show";
    private static String HIDE = "hide";
    private View rootView;
    private Button searchBtn;
    private Button buyBtn;
    private Button addWatchlistBtn;
    private RadioButton intradayRadioBtn;
    private RadioButton dayRadioBtn;
    private RadioButton weekRadioBtn;
    private RadioButton monthRadioBtn;
    private AutoCompleteTextView searchEditText;
    private TextView tvClose;
    private TextView tvCurrent;
    private MainActivity ma;
    private LinearLayout searchBtnsCont;
    private LinearLayout searchStockStatCont;
    private LinearLayout graph_cont;
    private int radioToggle;
    private int asyncCount;
    private int asyncDone;
    private int searched;
    private String ticker;
    private String currentPrice;
    private StockServiceHelper mStockServiceHelper;
    private ContactWebservice mContactWebservice;
    private ContactWebservice2 mContactWebservice2;
    private AsyncValidation mAsyncValidation;
    private ArrayAdapter<String> autoCompAdapter;
    private DatabaseHelper dbh;
    private FrameLayout progress_cont;
    private XYPlot plot;
    private LineChart chart;
    private boolean found;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("test", "SearchFragment.onCreate()");
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ma = (MainActivity) getActivity();
        ma.hideIndeterminateProgress();
        ma.showContentContainer();
        dbh = new DatabaseHelper(getActivity(), "Stock", null, 1);
        //mStockServiceHelper = new StockServiceHelper();
        searchBtn = (Button) rootView.findViewById(R.id.searchBtn);
        buyBtn = (Button) rootView.findViewById(R.id.buyBtn);
        addWatchlistBtn = (Button) rootView.findViewById(R.id.addWatchlistBtn);
        intradayRadioBtn = (RadioButton) rootView.findViewById(R.id.intradayRadioBtn);
        dayRadioBtn = (RadioButton) rootView.findViewById(R.id.dayRadioBtn);
        weekRadioBtn = (RadioButton) rootView.findViewById(R.id.weekRadioBtn);
        monthRadioBtn = (RadioButton) rootView.findViewById(R.id.monthRadioBtn);
        searchEditText = (AutoCompleteTextView) rootView.findViewById(R.id.autocompleteEditTextView);
        tvClose = (TextView) rootView.findViewById(R.id.tvClosePrice);
        tvCurrent = (TextView) rootView.findViewById(R.id.tvCurrentPrice);
        graph_cont = (LinearLayout) rootView.findViewById(R.id.graph_cont);
        searchBtnsCont = (LinearLayout) rootView.findViewById(R.id.searchBtnsCont);
        searchStockStatCont = (LinearLayout) rootView.findViewById(R.id.searchStockStatCont);
        progress_cont = (FrameLayout) rootView.findViewById(R.id.progress_cont);
        // initialize our XYPlot reference:
        //plot = (XYPlot) rootView.findViewById(R.id.plot);
        chart = (LineChart) rootView.findViewById(R.id.chart);
        // Array for autocomplete adapter
        String[] stocks = concat(nasdaq.NASDAQ_ARRAY, nyse.NYSE_ARRAY);
        autoCompAdapter = new ArrayAdapter<String>
                (ma, android.R.layout.simple_list_item_1, stocks);
        searchEditText.setAdapter(autoCompAdapter);

        initEventListeners();
        initStates();

        return rootView;
    }

    @Nullable
    @Override
    public View getView() {
        Log.d("test", "SearchFragment.getView()");
        return super.getView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("test", "SearchFragment.onResume()");
        restoreState();
        Bundle bundle = this.getArguments();
        if(bundle != null){
            ticker = bundle.getString("ticker", null);
            Log.d("test", "\t\tTicker = "+ticker);
            if(ticker != null && ticker != ""){
                searchEditText.setText(ticker);
                searchAction();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("test", "SearchFragment.onPause()");
        cancelAsyncTasks();
        hideSoftKeyboard(ma, getView());
        saveState(searchEditText.getText().toString());
        if(dbh != null){
            dbh.close();
            Log.d("test", "\t\tClosed DBH");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test", "SearchFragment.onDestroy()");
        //clearStateSave();
    }

    public void clearStateSave(){
        Log.d("test", "SearchFragment.stopStateSave()");
        if(searchEditText != null){
            searchEditText.setText("");
        }
        searched = -1;
        Log.d("test", "\t\tSearched = "+searched);
        saveState("");
    }

    private void saveState(String s){
        Log.d("test", "SearchFragment.saveState()");
        if(s != null){
            Log.d("test", "s != null \t s = "+s+"\t s empty?: "+s.isEmpty());
            if(ma != null){
                SharedPreferences sharedPref = ma.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("ticker", s);
                editor.putInt("searched", searched);
                editor.commit();
            }
        }
    }

    private void restoreState(){
        Log.d("test", "SearchFragment.restoreState()");
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String t = sharedPref.getString("ticker", "");
        if(!t.equals("")){
            ticker = t;
            searchEditText.setText(t);
            searchAction();
        }else{
            searched = -1;
            Log.d("test", "\t\tSearched = "+searched);
            searchEditText.setText(t);
        }
    }

    private void searchAction() {
        Log.d("test", "SearchFragment.searchAction()");
        hideSearchResults();
        showLoadingCont();
        hideSoftKeyboard(ma, getView());
        radioToggle = 2;
        changeRadio(radioToggle);
        queryStock(ticker, "d", true);
        searched = 1;
        Log.d("test", "\t\tSearched = "+searched);
    }

    private void initEventListeners() {
        Log.d("test", "SearchFragment.initEventListeners()");
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "SearchFragment.searchBtn.OnClick()");
                // Stuff that happens when you search a stock
                ticker = searchEditText.getText().toString().toUpperCase();
                if(ticker != null){
                    searchEditText.setText(ticker);
                }
                searchAction();
            }
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "SearchFragment.buyBtn.OnClick()");
                // Stuff that happens when you want to buy current ticker
                if(ticker != null && currentPrice != null){
                    ma.selectDrawerItem(2, true);
                    ma.displayBuyFragment(ticker, currentPrice);
                }
                clearStateSave();
            }
        });

        addWatchlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "SearchFragment.addWatchlistBtn.OnClick()");
                // Stuff that happens when add the current ticker to watchlist
                if(!stockInTableWatchlist()){
                    if(ticker != null && !ticker.equals("")){
                        dbh.insertWatchlist(ticker);
                        clearStateSave();
                        ma.selectDrawerItem(3, false);
                    }
                }else{
                    Toast.makeText(getActivity(), "Ticker: "+ ticker +"\nAlready In Watchlist", Toast.LENGTH_SHORT).show();
                    clearStateSave();
                    ma.selectDrawerItem(3, false);
                }
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("test", "SearchFragment.setOnEditorActionListener.onEditorAction()");
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    ticker = searchEditText.getText().toString().toUpperCase();
                    if(ticker != null){
                        searchEditText.setText(ticker);
                    }
                    searchAction();
                }else{
                    ticker = searchEditText.getText().toString().toUpperCase();
                    if(ticker != null){
                        searchEditText.setText(ticker);
                    }
                    searchAction();
                }
                return true;
            }
        });

        searchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("test", "SearchFragment.searchEditText.onItemClick()");
                // Stuff that happens when you search a stock
                ticker = searchEditText.getText().toString().toUpperCase();
                if(ticker != null){
                    searchEditText.setText(ticker);
                }
                searchAction();
            }
        });

        intradayRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("test", "SearchFragment.intradayRadioBtn.onCheckedChanged()");
                    radioToggle = 1;
                    changeRadio(radioToggle);
                    if(searched == 1){
                        Log.d("test", "Searched =  True");
                        queryStock(ticker, "i", false);
                    }
                }
            }
        });

        dayRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("test", "SearchFragment.dayRadioBtn.onCheckedChanged()");
                    radioToggle = 2;
                    changeRadio(radioToggle);
                    if(searched == 1){
                        Log.d("test", "Searched =  True");
                        queryStock(ticker, "d", false);
                    }
                }
            }
        });

        weekRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("test", "SearchFragment.weekRadioBtn.onCheckedChanged()");
                    radioToggle = 3;
                    changeRadio(radioToggle);
                    if(searched == 1){
                        Log.d("test", "Searched =  True");
                        queryStock(ticker, "w", false);
                    }
                }
            }
        });

        monthRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("test", "SearchFragment.monthRadioBtn.onCheckedChanged()");
                    radioToggle = 4;
                    changeRadio(radioToggle);
                    if(searched == 1){
                        Log.d("test", "Searched =  True");
                        queryStock(ticker, "m", false);
                    }
                }
            }
        });
    }

    private void initStates() {
        Log.d("test", "SearchFragment.initStates()");
        searchEditText.setText("");
        tvCurrent.setText("");
        tvClose.setText("");
        currentPrice = null;
        ticker = null;
        this.searched = -1;
        found = false;
        asyncCount = 0;
        asyncDone = 0;
        hideSearchResults();
        hideLoadingCont();
    }

    public void changeRadio(int radioToggle){
        Log.d("test", "SearchFragment.changeRadio()");
        switch(radioToggle){
            case 1:
                intradayRadioBtn.setChecked(true);
                dayRadioBtn.setChecked(false);
                weekRadioBtn.setChecked(false);
                monthRadioBtn.setChecked(false);
                break;
            case 2:
                intradayRadioBtn.setChecked(false);
                dayRadioBtn.setChecked(true);
                weekRadioBtn.setChecked(false);
                monthRadioBtn.setChecked(false);
                break;
            case 3:
                intradayRadioBtn.setChecked(false);
                dayRadioBtn.setChecked(false);
                weekRadioBtn.setChecked(true);
                monthRadioBtn.setChecked(false);
                break;
            case 4:
                intradayRadioBtn.setChecked(false);
                dayRadioBtn.setChecked(false);
                weekRadioBtn.setChecked(false);
                monthRadioBtn.setChecked(true);
                break;
            default:
                break;
        }
    }

    private void queryStock(String ticker, String period, boolean queryBoth){
        Log.d("test", "SearchFragment.queryStock(" +ticker+", "+period+", "+queryBoth+ ")");
        asyncCount = 0;
        asyncDone = 0;
        this.searched = 1;
        // query a stock if stock is found build the graph from the data and put data in their text views
        // have this return true when a stock is queried
        mStockServiceHelper = new StockServiceHelper(ticker, period); //default query is day    NOTE: I will want to add a query for current to get Stock Current price
        if(queryBoth){
            Log.d("test", "QueryBoth");
            asyncCount = 2;
            mContactWebservice = new ContactWebservice();
            mContactWebservice.execute(mStockServiceHelper); // executes async task using my StockServiceHelper to query stock data
        }else{
            Log.d("test", "QueryOne");
            asyncCount = 1;
        }
        if(mContactWebservice2 != null && !mContactWebservice2.isCancelled()){
            mContactWebservice2.cancel(true);
        }
        mContactWebservice2 = new ContactWebservice2();
        mContactWebservice2.execute(mStockServiceHelper);
        if(mAsyncValidation != null && !mAsyncValidation.isCancelled()){
            mAsyncValidation.cancel(true);
        }
        mAsyncValidation = new AsyncValidation();
        mAsyncValidation.execute();
    }

    private void showSearchResults(String ticker){
        Log.d("test", "SearchFragment.showSearchResults(" + ticker + ")");
        // show the search results containers with their new data
        showHideGraphCont(SHOW);
        showHideSearchBtnsCont(SHOW);
        showHideStockStatCont(SHOW);
    }

    private void hideSearchResults(){
        Log.d("test", "SearchFragment.hideSearchResults()");
        showHideGraphCont(HIDE);
        showHideSearchBtnsCont(HIDE);
        showHideStockStatCont(HIDE);
    }

    private void cancelAsyncTasks(){
        Log.d("test", "SearchFragment.cancelAsyncTasks()");
        if(mContactWebservice != null){
            mContactWebservice.cancel(true);
        }
        if(mContactWebservice2 != null){
            mContactWebservice2.cancel(true);
        }
        if(mAsyncValidation != null){
            mAsyncValidation.cancel(true);
        }
    }

    private void createGraphPlot(Set<Map.Entry<String, JsonElement>> result) {
        Log.d("test", "SearchFragment.createGraphPlot()");
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextSize(10f);
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setDrawAxisLine(true);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setGranularity(1f);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setTextSize(10f);
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setDrawAxisLine(true);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setGranularity(1f);

        Legend legend = chart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12f);
        legend.setXEntrySpace(3f);

        ArrayList<String> domain = new ArrayList<>();
        List<Entry> open = new ArrayList<Entry>();
        List<Entry> high = new ArrayList<Entry>();
        List<Entry> low = new ArrayList<Entry>();
        List<Entry> close = new ArrayList<Entry>();

        float xval = result.size();;
        if(xval>27){
            switch (radioToggle) {
                case 1:
                    xval = 26;
                    break;
                default:
                    xval = 12;
                    break;
            }
        }
        for (Map.Entry<String, JsonElement> entry : result) {
            Log.d("test", "\t\t xval = "+ xval);
            if(xval >= 0){
                xval--;
                Log.d("test", entry.getKey());
                if(radioToggle == 1){
                    String key = entry.getKey().substring(11,16);
                    domain.add(key);
                    if(xval%2 == 1){
                        Log.d("test", entry.getValue().toString());
                        JsonObject subset = entry.getValue().getAsJsonObject();
                        //Log.d("test", "\t\t" + subset.get("1. open").getAsDouble());
                        open.add(new Entry(xval, subset.get("1. open").getAsFloat()));
                        //open.add(subset.get("1. open").getAsDouble());
                        // Log.d("test", "\t\t" + subset.get("2. high").getAsDouble());
                        high.add(new Entry(xval, subset.get("2. high").getAsFloat()));
                        //Log.d("test", "\t\t" + subset.get("3. low").getAsDouble());
                        low.add(new Entry(xval, subset.get("3. low").getAsFloat()));
                        //Log.d("test", "\t\t" + subset.get("4. close").getAsDouble());
                        close.add(new Entry(xval, subset.get("4. close").getAsFloat()));
                        //Log.d("test", "\t\t" + subset.get("5. volume").getAsString());
                    }
                }else{
                    domain.add(entry.getKey());
                    Log.d("test", entry.getValue().toString());
                    JsonObject subset = entry.getValue().getAsJsonObject();
                    //Log.d("test", "\t\t" + subset.get("1. open").getAsDouble());
                    open.add(new Entry(xval, subset.get("1. open").getAsFloat()));
                    //open.add(subset.get("1. open").getAsDouble());
                    // Log.d("test", "\t\t" + subset.get("2. high").getAsDouble());
                    high.add(new Entry(xval, subset.get("2. high").getAsFloat()));
                    //Log.d("test", "\t\t" + subset.get("3. low").getAsDouble());
                    low.add(new Entry(xval, subset.get("3. low").getAsFloat()));
                    //Log.d("test", "\t\t" + subset.get("4. close").getAsDouble());
                    close.add(new Entry(xval, subset.get("4. close").getAsFloat()));
                    //Log.d("test", "\t\t" + subset.get("5. volume").getAsString());
                }
            }else{
                break;
            }
        }
        Collections.reverse(domain);
        Collections.reverse(open);
        Collections.reverse(high);
        Collections.reverse(low);
        Collections.reverse(close);

        Log.d("test", "# Entries = "+domain.size());
        String [] domainLabels = domain.toArray(new String[domain.size()]);
        xAxis.setValueFormatter(new MyXAxisValueFormatter(domainLabels));

        LineDataSet opendataSet = new LineDataSet(open, "Open"); // add entries to dataset
        //opendataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        opendataSet.setColor(Color.CYAN);

        LineDataSet highdataSet = new LineDataSet(high, "High"); // add entries to dataset
        //highdataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        highdataSet.setColor(Color.GREEN);

        LineDataSet lowdataSet = new LineDataSet(low, "Low"); // add entries to dataset
        //lowdataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lowdataSet.setColor(Color.RED);

        LineDataSet closedataSet = new LineDataSet(close, "Close"); // add entries to dataset
        //closedataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        closedataSet.setColor(Color.YELLOW);

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(0, opendataSet);
        dataSets.add(1, highdataSet);
        dataSets.add(2, lowdataSet);
        dataSets.add(3, closedataSet);
        int c = 0;
        for(ILineDataSet ild : dataSets){
            c++;
            //Log.d("test", "Is set#"+c+" visible :"+ild.isVisible());
            switch(c){
                case 1:
                    ild.setValueTextColor(Color.CYAN);
                    break;
                case 2:
                    ild.setValueTextColor(Color.GREEN);
                    break;
                case 3:
                    ild.setValueTextColor(Color.RED);
                    break;
                case 4:
                    ild.setValueTextColor(Color.YELLOW);
                    break;
                default:
                    break;
            }

        }

        chart.setData(new LineData(dataSets));
        chart.invalidate(); // refresh
    }

    public void showHideSearchBtnsCont(String state){
        Log.d("test", "SearchFragment.showHideSearchBtnsCont(" + state + ")");
        switch(state){
            case "show":
                searchBtnsCont.setVisibility(LinearLayout.VISIBLE);
                break;
            case "hide":
                searchBtnsCont.setVisibility(LinearLayout.INVISIBLE);
                break;
            default:
                break;
        }
    }

    public void showHideStockStatCont(String state){
        Log.d("test", "SearchFragment.showHideStockStatCont(" + state + ")");
        switch(state){
            case "show":
                searchStockStatCont.setVisibility(LinearLayout.VISIBLE);
                break;
            case "hide":
                searchStockStatCont.setVisibility(LinearLayout.INVISIBLE);
                break;
            default:
                break;
        }
    }

    public void showHideGraphCont(String state){
        Log.d("test", "SearchFragment.showHideGraphCont(" + state + ")");
        switch(state){
            case "show":
                graph_cont.setVisibility(ScrollView.VISIBLE);
                break;
            case "hide":
                graph_cont.setVisibility(ScrollView.INVISIBLE);
                break;
            default:
                break;
        }
    }

    public void showLoadingCont(){
        Log.d("test", "SearchFragment.showLoadingCont()");
        progress_cont.setVisibility(View.VISIBLE);
    }

    public void hideLoadingCont(){
        Log.d("test", "SearchFragment.hideLoadingCont()");
        progress_cont.setVisibility(View.INVISIBLE);
    }


    public static <T> T[] concat(T[] first, T[] second) {
        Log.d("test", "SearchFragment.concat()");
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public boolean stockInTableWatchlist(){
        Log.d("test", "SearchFragment.stockInTableWatchlist()");
        return dbh.foundStockTickerInWatchlist(ticker);
    }

    private class AsyncValidation extends  AsyncTask<Void, Boolean, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("test", "\nSearchFragment.AsyncValidation.doInBackground()");
            while(asyncDone < asyncCount){
                //wait
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("test", "\nSearchFragment.AsyncValidation.onPostExecute()");
            if(result && found){
                Log.d("test", "\nSearch Query Syncronization Done\n");
                hideLoadingCont();
                showSearchResults(ticker);
            }else{
                Toast.makeText(getActivity(), "Couldn't Find Stock \nTicker: "+ ticker +"", Toast.LENGTH_SHORT).show();
                hideLoadingCont();
            }
        }
    }

    private class ContactWebservice extends AsyncTask<StockServiceHelper, Void, Set<Map.Entry<String, JsonElement>>> {

        @Override
        protected  Set<Map.Entry<String, JsonElement>> doInBackground(StockServiceHelper...params) {
            Log.d("test", "SearchFragment.ContactWebservice.doInBackground()");
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
                    currentPrice = open+"";
                    tvClose.setText("$"+close);
                    tvCurrent.setText("$"+currentPrice);
                }
                asyncDone++;
                found = true;
            }else{
                //Toast.makeText(getActivity(), "Couldn't Find Stock \nTicker: "+ ticker +"", Toast.LENGTH_SHORT).show();
                asyncDone++;
                found = false;
            }
            Log.d("test", "\n\tasyncCount = " +asyncCount +"\tasyncDone = "+asyncDone);
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Void...values) {
        }
    }

    private class ContactWebservice2 extends AsyncTask<StockServiceHelper, Void, Set<Map.Entry<String, JsonElement>>> {

        @Override
        protected Set<Map.Entry<String, JsonElement>> doInBackground(StockServiceHelper...params) {
            Log.d("test", "SearchFragment.ContactWebservice2.doInBackground()");
            try {
                params[0].queryService();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return params[0].getEntries();    // this will all need to change so that I get the query data parsed
        }
        @Override
        protected void onPostExecute(Set<Map.Entry<String, JsonElement>> result) {
            Log.d("test", "SearchFragment.ContactWebservice2.onPostExecute()");
            // This is stuff that happens when my query is done
            if(result != null && result.size() > 0){
                Log.d("test", "\t result.size() > 0 : TRUE");
                createGraphPlot(result);
                asyncDone++;
                found = true;
            }else{
                //Toast.makeText(getActivity(), "Couldn't Find Stock \nTicker: "+ ticker +"", Toast.LENGTH_SHORT).show();
                asyncDone++;
                found = false;
            }
            Log.d("test", "\n\tasyncCount = " +asyncCount +"\tasyncDone = "+asyncDone);
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Void...values) {
        }
    }

    private class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

    }
}
