package com.example.david.cs3270finalmariluch.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.david.cs3270finalmariluch.MainActivity;
import com.example.david.cs3270finalmariluch.R;
import com.example.david.cs3270finalmariluch.utils.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellStockFragment extends Fragment {

    private View rootView;
    private MainActivity ma;
    private String ticker;
    private Double price;
    private Double shares;
    private Double purchaseAmount;
    private EditText inputSellAmount;
    private EditText inputSellShares;
    private TextView userShares;
    private TextView userTicker;
    private Button sellNowBtn;
    private DatabaseHelper dbh;
    private DatabaseHelper dbh2;
    private int flag;

    public SellStockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sell_stock, container, false);
    }

}
