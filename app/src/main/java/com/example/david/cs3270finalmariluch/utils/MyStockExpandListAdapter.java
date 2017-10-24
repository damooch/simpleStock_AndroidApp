package com.example.david.cs3270finalmariluch.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.david.cs3270finalmariluch.MainActivity;
import com.example.david.cs3270finalmariluch.R;
import com.example.david.cs3270finalmariluch.models.MyStockModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 7/26/2017.
 */

public class MyStockExpandListAdapter extends BaseExpandableListAdapter{
    private Context context;
    Context mContext;
    int layoutResourceId;
    MyStockModel data[] = null;
    private MyStockModel folder;
    private MainActivity ma;


    private HashMap<String, List<String>> childDataSource;

    public MyStockExpandListAdapter(MainActivity ma, Context context, MyStockModel[] data) {

        this.context = context;

        this.data = data;

        this.ma = ma;

    }

    @Override

    public int getGroupCount() {

        return this.data.length;

    }

    @Override

    public int getChildrenCount(int groupPosition) {

        return 1;

    }

    @Override

    public Object getGroup(int groupPosition) {

        return data[groupPosition];

    }

    @Override

    public Object getChild(int groupPosition, int childPosition) {

        return data[groupPosition].purchaseTotal;

    }

    @Override

    public long getGroupId(int groupPosition) {

        return groupPosition;

    }

    @Override

    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;

    }

    @Override

    public boolean hasStableIds() {

        return false;

    }

    @Override

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ma.getLayoutInflater();
        View listItem = inflater.inflate(R.layout.list_view_mystock, parent, false);

        TextView tv_ms_ticker = (TextView) listItem.findViewById(R.id.tv_ms_ticker);
        TextView tv_ms_shares = (TextView) listItem.findViewById(R.id.tv_ms_shares);
        TextView tv_ms_stockVal = (TextView) listItem.findViewById(R.id.tv_ms_stockVal);
        TextView tv_ms_change = (TextView) listItem.findViewById(R.id.tv_ms_change);

        this.folder = data[groupPosition];
        if(this.folder != null){
            Log.d("test", "MyStockModel contents : \n"+this.folder.toString());
        }

        tv_ms_ticker.setText(folder.ticker);
        tv_ms_shares.setText(folder.shares);
        tv_ms_stockVal.setText("$"+folder.stockValue);

        int sign = getChangeSign(this.folder.change);
        Log.d("test", "Sign of chg% = " + sign);

        if(sign > 0){
            tv_ms_change.setTextColor(ma.getResources().getColor(R.color.green));
            tv_ms_change.setText("+"+ this.folder.change+"%");
        }else if (sign < 0){
            tv_ms_change.setTextColor(ma.getResources().getColor(R.color.red));
            tv_ms_change.setText(this.folder.change+"%");
        }else{
            tv_ms_change.setTextColor(ma.getResources().getColor(R.color.white));
            tv_ms_change.setText(this.folder.change+"%");
        }

        return listItem;
    }

    @Override

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ma.getLayoutInflater();
        View view = inflater.inflate(R.layout.list_view_mystock_child, parent, false);

        this.folder = data[groupPosition];
        TextView tv_ms_ptot = (TextView) view.findViewById(R.id.tv_ms_ptot);
        Button ms_sellBtn = (Button) view.findViewById(R.id.ms_sellBtn);
        Button ms_buyBtn = (Button) view.findViewById(R.id.ms_buyBtn);

        tv_ms_ptot.setText("$"+folder.purchaseTotal);

        ms_sellBtn.setTag(groupPosition);
        ms_buyBtn.setTag(groupPosition);

        ms_buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                MyStockModel model = data[pos];
                ma.selectDrawerItem(1, true);
                ma.displaySearchFragment(model.ticker);
            }
        });

        ms_sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                MyStockModel model = data[pos];
                ma.displaySellFragment(model.ticker);
            }
        });

        return view;
    }

    @Override

    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;

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
