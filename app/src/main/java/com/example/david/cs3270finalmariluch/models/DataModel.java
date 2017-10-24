package com.example.david.cs3270finalmariluch.models;

import android.util.Log;

/**
 * Created by David on 6/21/2017.
 */

public class DataModel {

    public int icon;
    public String name;

    // Constructor.
    public DataModel(int icon, String name) {
        Log.d("test", "DataModel.DataModel() 'Constructor'");
        this.icon = icon;
        this.name = name;
    }

}
