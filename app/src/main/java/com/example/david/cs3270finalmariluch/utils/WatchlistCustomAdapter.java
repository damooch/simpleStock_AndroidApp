package com.example.david.cs3270finalmariluch.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.david.cs3270finalmariluch.MainActivity;
import com.example.david.cs3270finalmariluch.R;
import com.example.david.cs3270finalmariluch.models.WatchlistModel;


/**
 * Created by David on 7/24/2017.
 */

public class WatchlistCustomAdapter  extends ArrayAdapter<WatchlistModel> {

    Context mContext;
    int layoutResourceId;
    WatchlistModel data[] = null;
    MainActivity ma;
    private WatchlistModel folder;

    public WatchlistCustomAdapter(MainActivity ma, Context mContext, int layoutResourceId, WatchlistModel[] data) {
        super(mContext, layoutResourceId, data);
        this.ma = ma;
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("test", "WatchlistCustomAdapter.getView(position = "+position+")");

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        TextView tv_wl_ticker = (TextView) listItem.findViewById(R.id.tv_wl_ticker);
        TextView tv_wl_open = (TextView) listItem.findViewById(R.id.tv_wl_open);
        TextView tv_wl_current = (TextView) listItem.findViewById(R.id.tv_wl_current);
        TextView tv_wl_change = (TextView) listItem.findViewById(R.id.tv_wl_change);
        Button wl_removeBtn = (Button) listItem.findViewById(R.id.wl_removeBtn);

        this.folder = data[position];
        Log.d("test", "data["+position+"]");
        Log.d("test", "WatchlistModel.toString()\n\t\t"+ folder.toString());
        tv_wl_ticker.setText(this.folder.ticker);  //null error here
        tv_wl_open.setText("$"+ this.folder.open);
        tv_wl_current.setText("$"+ this.folder.current);
        int sign = getChangeSign(this.folder.change);
        if(sign > 0){
            tv_wl_change.setTextColor(mContext.getResources().getColor(R.color.green));
            tv_wl_change.setText("+"+ this.folder.change+"%");
        }else if (sign < 0){
            tv_wl_change.setTextColor(mContext.getResources().getColor(R.color.red));
            tv_wl_change.setText(this.folder.change+"%");
        }else{
            tv_wl_change.setTextColor(mContext.getResources().getColor(R.color.white));
            tv_wl_change.setText(this.folder.change+"%");
        }

        wl_removeBtn.setTag(position);

        wl_removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                WatchlistModel model = data[pos];
                DatabaseHelper dbh = new DatabaseHelper(ma, "Stock", null, 1);
                dbh.deleteOneWatchlist(model.ticker);
                ma.displayWatchlistFragment();
            }
        });

        final TextView tv_wl_tickerFinal = (TextView) listItem.findViewById(R.id.tv_wl_ticker);
        LinearLayout tickerRow = (LinearLayout) listItem.findViewById(R.id.tickerRow);
        tickerRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast toast = Toast.makeText(getContext(),"You clicked a row" ,Toast.LENGTH_LONG);
                //toast.show();
                //Log.d("test","Clicked Row: " + tv_wl_tickerFinal.getText());
                ma.selectDrawerItem(1, true);
                ma.displaySearchFragment(tv_wl_tickerFinal.getText().toString());
            }
        });

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
