package com.example.david.cs3270finalmariluch.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.david.cs3270finalmariluch.R;
import com.example.david.cs3270finalmariluch.models.MyStockModel;

/**
 * Created by David on 7/24/2017.
 */

public class MyStockCustomAdapter extends ArrayAdapter<MyStockModel> {

    Context mContext;
    int layoutResourceId;
    MyStockModel data[] = null;
    private MyStockModel folder;

    public MyStockCustomAdapter(Context mContext, int layoutResourceId, MyStockModel[] data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        TextView tv_ms_ticker = (TextView) listItem.findViewById(R.id.tv_ms_ticker);
        TextView tv_ms_shares = (TextView) listItem.findViewById(R.id.tv_ms_shares);
        TextView tv_ms_ptot = (TextView) listItem.findViewById(R.id.tv_ms_ptot);
        TextView tv_ms_stockVal = (TextView) listItem.findViewById(R.id.tv_ms_stockVal);
        TextView tv_ms_change = (TextView) listItem.findViewById(R.id.tv_ms_change);

        this.folder = data[position];

        int sign = getChangeSign(this.folder.change);
        if(sign > 0){
            tv_ms_change.setTextColor(mContext.getResources().getColor(R.color.green));
            tv_ms_change.setText("+"+ this.folder.change+"%");
        }else if (sign < 0){
            tv_ms_change.setTextColor(mContext.getResources().getColor(R.color.red));
            tv_ms_change.setText(this.folder.change+"%");
        }else{
            tv_ms_change.setTextColor(mContext.getResources().getColor(R.color.white));
            tv_ms_change.setText(this.folder.change+"%");
        }

        tv_ms_ticker.setText(folder.ticker);
        tv_ms_shares.setText(folder.shares);
        tv_ms_ptot.setText(folder.purchaseTotal);
        tv_ms_stockVal.setText(folder.stockValue);
        tv_ms_change.setText(folder.change);

        return listItem;
    }

    private int getChangeSign(String change){
        double chg = 0;
        try{
            chg = Double.parseDouble(change);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(chg > 0){
            return 1;
        }else if (chg < 0){
            return -1;
        }else{
            return 0;
        }
    }
}
