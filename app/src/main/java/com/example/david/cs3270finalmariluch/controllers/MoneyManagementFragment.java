package com.example.david.cs3270finalmariluch.controllers;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class MoneyManagementFragment extends Fragment {

    private View rootView;
    private DatabaseHelper dbh;
    private MainActivity ma;
    private EditText inputMoney;
    public TextView liquidMoney;
    public Button addMoneyBtn;

    public MoneyManagementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_money_management, container, false);
        ma = (MainActivity) getActivity();
        dbh = new DatabaseHelper(getActivity(), "User", null, 1);
        inputMoney = (EditText) rootView.findViewById(R.id.inputMoney);
        liquidMoney = (TextView) rootView.findViewById(R.id.liquidMoney);
        addMoneyBtn = (Button) rootView.findViewById(R.id.addMoneyBtn);
        initializeData();
        initEventHandlers();
        return rootView;
    }

    public void initializeData(){
        try{
            String s = dbh.getMoneyFromUser();
            if(s.equals("null")){
                liquidMoney.setText("$0");
            }else{
                liquidMoney.setText("$"+s);
            }
        }catch(Exception e){
            e.printStackTrace();
            liquidMoney.setText("$0");
        }
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard(ma, getView());
        if(dbh != null){
            dbh.close();
            Log.d("test", "\t\tClosed DBH");
        }
        //saveState();
    }

    @Override
    public void onResume() {
        super.onResume();
        //restoreState();
    }

    private void saveState(){
        Log.d("test", "MoneyManagementFragment.saveState()");
        SharedPreferences sharedPref = ma.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("liquidMoney", liquidMoney.getText().toString());
        editor.commit();
    }

    private void restoreState() {
        Log.d("test", "MoneyManagementFragment.restoreState()");
        SharedPreferences sharedPref = ma.getPreferences(Context.MODE_PRIVATE);
        liquidMoney.setText(sharedPref.getString("liquidMoney", "$0"));
    }

    public double formatMoneyInput(){
        double money = 0;
        try{
            money = Double.parseDouble(inputMoney.getText().toString());
            Log.d("test", "inputMoney = "+money);
            money = money * 100;
            money = Math.round(money);
            money = money / 100;
            Log.d("test", "Parsed inputMoney = "+money);
        }catch(Exception e){
            e.printStackTrace();
        }
        inputMoney.setText("");
        return money;
    }

    public void addMoneyToUser(){
        if(dbh.checkUserExists()){
            Log.d("test", "Adding User Money");
            dbh.addUserMoney(formatMoneyInput()+"");
        }else{
            Log.d("test", "Inserting User Money");
            dbh.insertMoney(formatMoneyInput()+"");
        }
        setLiquidMoneyText();
    }

    public void setLiquidMoneyText(){
        liquidMoney.setText("$"+dbh.getMoneyFromUser());
    }

    public void initEventHandlers(){

        addMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoneyToUser();
                ma.selectDrawerItem(4, false);
            }
        });
    }

}
