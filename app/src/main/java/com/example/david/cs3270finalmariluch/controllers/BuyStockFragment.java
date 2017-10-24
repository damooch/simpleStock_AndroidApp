package com.example.david.cs3270finalmariluch.controllers;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.david.cs3270finalmariluch.MainActivity;
import com.example.david.cs3270finalmariluch.R;
import com.example.david.cs3270finalmariluch.utils.DatabaseHelper;

import static com.example.david.cs3270finalmariluch.MainActivity.hideSoftKeyboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyStockFragment extends Fragment {

    private View rootView;
    private MainActivity ma;
    private String ticker;
    private Double price;
    private Double shares;
    private Double purchaseAmount;
    private TextView inputTicker;
    private TextView userMoney;
    private EditText inputShares;
    private EditText inputAmount;
    private Button buyBtn;
    private DatabaseHelper dbh;
    private DatabaseHelper dbh2;
    private int flag;

    public BuyStockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("test", "BuyStockFragment.onCreate()");
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_buy_stock, container, false);
        ma = (MainActivity) getActivity();
        dbh = new DatabaseHelper(ma, "Stock", null, 1);
        dbh2 = new DatabaseHelper(ma, "User", null, 1);
        flag = 0;
        inputTicker = (TextView) rootView.findViewById(R.id.inputTicker);
        inputShares = (EditText) rootView.findViewById(R.id.inputShares);
        inputAmount = (EditText) rootView.findViewById(R.id.inputAmount);
        userMoney = (TextView) rootView.findViewById(R.id.userMoney);
        userMoney.setText(dbh2.getMoneyFromUser());
        buyBtn = (Button) rootView.findViewById(R.id.buyNowBtn);
        initEventListeners();
        Bundle bundle = this.getArguments();
        if(bundle != null){
            ticker = bundle.getString("ticker", null);
            Log.d("test", "\t\tTicker = "+ticker);
            String p = bundle.getString("price", null);
            if(p != null){
                try{
                    price = Double.parseDouble(p);
                    Log.d("test", "\t\tPrice = "+price);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            populateBuyStock();
        }
        return rootView;
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ticker != null){
            Cursor cur = dbh.getOneStock(ticker);
            cur.moveToFirst();
            if(cur.getCount() > 0){
                Log.d("test", "Found stock with ticker="+cur.getString(cur.getColumnIndex("ticker")));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard(ma, getView());
        if(dbh != null){
            dbh.close();
            Log.d("test", "\t\tClosed DBH");
        }
        if(dbh2 != null){
            dbh2.close();
            Log.d("test", "\t\tClosed DBH2");
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void initEventListeners(){
        Log.d("test", "BuyStockFragment.onCreate()");
//        inputTicker.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("test", "BuyStockFragment.inputTicker.onTextChanged()");
//                ticker = inputTicker.getText().toString().toUpperCase();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                inputTicker.setSelection(inputTicker.getText().toString().length());
//            }
//        });
        inputShares.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("test", "BuyStockFragment.inputShares.onTextChanged()");
                if(flag != -1){
                    String shr = inputShares.getText().toString();
                    try{
                        shares = Double.parseDouble(shr);
                        if(shares == null || shares == 0.0){
                            shares = 0.0;
                        }
                        purchaseAmount = shares * price;
                        purchaseAmount = ((Math.round(purchaseAmount * 100.0)) / 100.0);
                        flag = 1;
                        Log.d("test", "\t\tprice = "+price+"\n\t\tpurchaseAmount ="+purchaseAmount+"\n\t\tshares = "+shares+"\n\t\tflag = "+flag);
                        inputAmount.setText(""+purchaseAmount);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                flag = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(inputShares.getText().length() == 0){
                    flag = 1;
                    inputShares.setText("0.0");
                    inputShares.setSelection(inputShares.getText().toString().length());
                }
                flag = 0;
            }
        });
        inputAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("test", "BuyStockFragment.inputAmount.onTextChanged()");
                if(flag != 1){
                    String amt = inputAmount.getText().toString();
                    try{
                        purchaseAmount = Double.parseDouble(amt);
                        if(purchaseAmount == null || purchaseAmount == 0.0){
                            purchaseAmount = 0.0;
                        }
                        shares = (purchaseAmount / price);
                        shares = ((Math.round(shares * 100000.0)) / 100000.0);
                        flag = -1;
                        Log.d("test", "\t\tprice = "+price+"\n\t\tpurchaseAmount ="+purchaseAmount+"\n\t\tshares = "+shares+"\n\t\tflag = "+flag);
                        inputShares.setText(""+shares);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                flag = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(inputAmount.getText().length() == 0){
                    flag = -1;
                    inputAmount.setText("0.0");
                    inputAmount.setSelection(inputAmount.getText().toString().length());
                }
                flag = 0;
            }
        });
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "BuyStockFragment.buyBtn.onClick()");
                // check that users inputs are valid
                if(validInputs()){
                    if(canBuyStock()){
                        // check that user has money to buy stock or show alert dialog
                        if(stockInTable()){
                            Log.d("test", "\tStock found in table");
                            dbh.updateStock(ticker, shares, purchaseAmount);
                        }else{
                            Log.d("test", "\tStock not found in table");
                            dbh.insertStock(ticker, ""+shares, ""+purchaseAmount);
                        }
                        ma.selectDrawerItem(0, false);

                    }else{
                        showAlertDialog();
                    }
                }
            }
        });
        inputTicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    ticker = inputTicker.getText().toString().toUpperCase();
                    if(ticker != null){
                        inputTicker.setText(ticker);
                    }
                    hideSoftKeyboard(ma, v);
                    //queryStock(ticker, "d");
                }else{
                    ticker = inputTicker.getText().toString().toUpperCase();
                    if(ticker != null){
                        inputTicker.setText(ticker);
                    }
                    hideSoftKeyboard(ma, v);
                    //queryStock(ticker, "d");
                }
                return true;
            }
        });
        inputShares.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN))  {
                    hideSoftKeyboard(ma, v);
                    //queryStock(ticker, "d");
                }else{
                    hideSoftKeyboard(ma, v);
                }
                return true;
            }
        });
        inputAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    hideSoftKeyboard(ma, v);
                    //queryStock(ticker, "d");
                }else{
                    hideSoftKeyboard(ma, v);
                }
                return true;
            }
        });
    }

    public boolean stockInTable(){
        return dbh.foundStockTickerInStock(ticker);
    }

    public void populateBuyStock(){
        Log.d("test", "BuyStockFragment.populateBuyStock()");
        if(ticker != null){
            inputTicker.setText(ticker);
        }
    }

    public boolean validInputs(){
        Log.d("test", "BuyStockFragment.validInputs()");
        if(ticker != null && ticker != "" && shares != null && shares != 0 && purchaseAmount != null && purchaseAmount != 0){
            Log.d("test", "\t\tInputs Are Valid");
            return true;
        }
        Log.d("test", "\t\tInputs Are NOT Valid");
        return false;
    }

    private boolean canBuyStock(){
        Log.d("test", "BuyStockFragment.canBuyStock()");
        if(dbh2.checkUserExists()){
            Log.d("test", "USER EXISTS)");
            try{
                double amount = Double.parseDouble(inputAmount.getText().toString());
                amount = amount * 100;
                amount = Math.round(amount);
                amount = amount / 100;
                double money = Double.parseDouble(dbh2.getMoneyFromUser());
                Log.d("test", "\t\t Amount = "+amount+"   Money = "+money+")");
                if(money > 0 && money >= amount){
                    double newMoney = money - amount;
                    newMoney = newMoney * 100;
                    newMoney = Math.round(newMoney);
                    newMoney = newMoney / 100;
                    dbh2.updateUserMoney(""+newMoney);
                    return true;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private void showAlertDialog() {
        String title = "Not Enough Money!";
        String message = "You do not have enough money to make this purchase.\nWould you like to add money to your account?";
        AddMoneyAlertDialogFragment dialogFrag = AddMoneyAlertDialogFragment.newInstance(title, message);
        dialogFrag.setCancelable(false);
        dialogFrag.show(ma.getFragmentManager(), "dialog");
    }

}
