package com.example.david.cs3270finalmariluch.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by David on 6/26/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public DatabaseHelper(Context ctx, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(ctx, name, factory, version);
    }

    // Opens the db
    public SQLiteDatabase open(){
        db = null;
        try{
            db = getWritableDatabase();
        }catch(SQLiteException e){
            Log.d("test", "Exception:\n");
            e.printStackTrace();
        }
        return db;
    }

    // closes the db
    public void close(){
        if(db != null){
            db.close();
        }
    }

    // insert a stock into Stock table
    public long insertStock(String ticker, String shares, String purchaseTotal){
        Log.d("test", "Inserting Stock: \nTicker: "+ticker+" \nShares: "+shares+" \nPurchaseTotal: "+purchaseTotal);
        long rowID = -1;
        ContentValues newStock = new ContentValues();
        //newStock.put("name", name);
        newStock.put("ticker", ticker);
        newStock.put("shares", shares);
        newStock.put("purchaseTotal", purchaseTotal);
        if(open() != null){
            rowID = db.insert("Stock", null, newStock);
            close();
        }
        return rowID;
    }

    // insert a stock into Stock table
    public long insertWatchlist(String ticker){
        Log.d("test", "Inserting Stock: \nTicker: "+ticker);
        long rowID = -1;
        ContentValues newTicker = new ContentValues();
        //newStock.put("name", name);
        newTicker.put("ticker", ticker);
        if(open() != null){
            rowID = db.insert("Watchlist", null, newTicker);
            close();
        }
        return rowID;
    }

    public long updateStock(long id, String ticker, String shares, String purchaseTotal){
        Log.d("test", "Updating Stock: \nTicker: "+ticker+" \nShares: "+shares+" \nPurchaseTotal: "+purchaseTotal);
        long rowID = -1;
        ContentValues editStock = new ContentValues();
        //editStock.put("name", name);
        editStock.put("ticker", ticker);
        editStock.put("shares", shares);
        editStock.put("purchaseTotal", purchaseTotal);
        if(open() != null){
            rowID = db.update("Stock", editStock,"_id="+id, null);
            close();
        }
        return rowID;
    }

    public long updateStock(String ticker, Double shares, Double purchaseTotal){
        long rowID = -1;
        String[] params = new String[1];
        params[0] = ticker;
        Cursor c = getOneStock(ticker);
        c.moveToFirst();
        double s = Double.parseDouble(c.getString(c.getColumnIndex("shares")));
        double p = Double.parseDouble(c.getString(c.getColumnIndex("purchaseTotal")));
        double totS = s+shares;
        double totP = p+purchaseTotal;
        Log.d("test", "Updating Stock: \nTicker: "+ticker+" \nShares: "+totS+" \nPurchaseTotal: "+totP);
        ContentValues editStock = new ContentValues();
        //editStock.put("name", name);
        //editStock.put("ticker", ticker);
        editStock.put("shares", ""+totS);
        editStock.put("purchaseTotal", ""+totP);
        if(open() != null){
            rowID = db.update("Stock", editStock,"ticker=?", params);
            // Just for log testing
            c = getOneStock(ticker);
            c.moveToFirst();
            Log.d("test", "Updated Stock: \nTicker: "+ticker+" \nShares: "
                    +c.getString(c.getColumnIndex("shares"))+" \nPurchaseTotal: "
                    +c.getString(c.getColumnIndex("purchaseTotal")));
            close();
            c.close();
        }
        return rowID;
    }

    public long deleteOneStock(long id){
        long rowID = -1;
        String[] params = new String[1];
        params[0] = "" + id;
        if(open() != null){
            rowID = db.delete("Stock", "_id=?", params);
            close();
        }
        return rowID;
    }

    public long deleteOneWatchlist(long id){
        long rowID = -1;
        String[] params = new String[1];
        params[0] = "" + id;
        if(open() != null){
            rowID = db.delete("Watchlist", "_id=?", params);
            close();
        }
        return rowID;
    }

    public long deleteOneWatchlist(String ticker){
        long rowID = -1;
        String[] params = new String[1];
        params[0] = ticker;
        if(open() != null){
            rowID = db.delete("Watchlist", "ticker=?", params);
            close();
        }
        return rowID;
    }

    public void deleteAllStock(){
        long rowID = -1;
        if(open() != null){
            rowID = db.delete("Stock", null, null);
            close();
        }
    }

    public void deleteAllWatchlist(){
        long rowID = -1;
        if(open() != null){
            rowID = db.delete("Watchlist", null, null);
            close();
        }
    }

    public Cursor getAllStock(){
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying all stocks");
            cursor = db.rawQuery("SELECT * FROM Stock ORDER BY ticker", null);
            cursor.moveToFirst();
            close();
        }
        return  cursor;  //returns a cursor dataset
    }

    public Cursor getAllWatchlist(){
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying all watchlist");
            cursor = db.rawQuery("SELECT * FROM Watchlist ORDER BY ticker", null);
            cursor.moveToFirst();
            close();
        }
        return  cursor;  //returns a cursor dataset
    }

    public Cursor getOneStock(long id){
        String[] params = new String[1];
        params[0] = "" + id;
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one stock");
            cursor = db.rawQuery("SELECT * FROM Stock WHERE _id=?", params);  //using params helps prevent sql injection
        }
        return  cursor;  //returns a cursor dataset
    }

    public Cursor getOneStock(String ticker){
        String[] params = new String[1];
        params[0] = ticker;
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one stock");
            cursor = db.rawQuery("SELECT * FROM Stock WHERE ticker=?", params);  //using params helps prevent sql injection
        }
        return  cursor;  //returns a cursor dataset
    }

    public int getNextID(){
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying all Stocks");
            cursor = db.rawQuery("SELECT * FROM Stock ORDER BY _id DESC LIMIT 1", null);
            cursor.moveToFirst();  // essential and often missed
        }
        int id = 1;
        try{
            id = cursor.getInt(cursor.getColumnIndex("_id"))+1;
            cursor.close();
        }catch(Exception e){
            Log.d("test", "Exception:\n");
            e.printStackTrace();
            cursor.close();
        }
        close();
        return  id;  //returns highest current index+1
    }

    public String getPurchaseTotalFromStock(long id){
        String[] params = new String[1];
        params[0] = "" + id;
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one stock");
            cursor = db.rawQuery("SELECT * FROM Course WHERE _id=?", params);  //using params helps prevent sql injection
            cursor.moveToFirst();
        }
        String ptotal = null;
        try{
            Log.d("test", "Getting Stock Purchase Total");
            ptotal = cursor.getString(cursor.getColumnIndex("courseID"));
            Log.d("test", "Stock: \nTicker: "+cursor.getString(cursor.getColumnIndex("ticker"))+" \nPurchase Total: "+ptotal+"");
            cursor.close();
        }catch(Exception e){
            Log.d("test", e.getMessage());
            cursor.close();
        }
        close();
        return  ptotal;  //returns courseID
    }

    public String getSharesFromStock(long id){
        String[] params = new String[1];
        params[0] = "" + id;
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one stock");
            cursor = db.rawQuery("SELECT * FROM Stock WHERE _id=?", params);  //using params helps prevent sql injection
            cursor.moveToFirst();
        }
        String shares = null;
        try{
            Log.d("test", "Getting Stock Shares");
            shares = cursor.getString(cursor.getColumnIndex("shares"));
            Log.d("test", "Stock: \nticker: "+cursor.getString(cursor.getColumnIndex("ticker"))+" \nshares: "+shares+"");
        }catch(Exception e){
            Log.d("test", e.getMessage());
            cursor.close();
        }
        close();
        return  shares;  //returns stock shares from Stock
    }

    public String getStockNameFromStock (long id){
        String[] params = new String[1];
        params[0] = "" + id;
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one Stock");
            cursor = db.rawQuery("SELECT * FROM Stock WHERE _id=?", params);  //using params helps prevent sql injection
            cursor.moveToFirst();
        }
        String name = null;
        try{
            Log.d("test", "Getting Stock Name");
            name = cursor.getString(cursor.getColumnIndex("name"));
            Log.d("test", "Stock: \nName: "+ name);
        }catch(Exception e){
            Log.d("test", e.getMessage());
        }
        return  name;  //returns stock name from Stock table
    }


    public boolean foundStockTickerInStock (String tick){
        String[] params = new String[1];
        params[0] = tick;
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one Stock");
            cursor = db.rawQuery("SELECT * FROM Stock WHERE ticker=?", params);  //using params helps prevent sql injection
            cursor.moveToFirst();
        }
        String ticker = null;
        try{
            Log.d("test", "Getting Stock Ticker");
            ticker = cursor.getString(cursor.getColumnIndex("ticker"));
            cursor.close();
            Log.d("test", "Stock: \nTicker: "+ ticker);
        }catch(Exception e){
            Log.d("test", e.getMessage());
            cursor.close();
        }
        if(ticker != null){
            if(ticker.equals(tick)){
                close();
                return true;
            }
        }
        close();
        return false;
    }

    public boolean foundStockTickerInWatchlist (String tick){
        String[] params = new String[1];
        params[0] = tick;
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one Stock");
            cursor = db.rawQuery("SELECT * FROM Watchlist WHERE ticker=?", params);  //using params helps prevent sql injection
            cursor.moveToFirst();
        }
        String ticker = null;
        try{
            Log.d("test", "Getting Stock Ticker");
            ticker = cursor.getString(cursor.getColumnIndex("ticker"));
            cursor.close();
            Log.d("test", "Stock: \nTicker: "+ ticker);
        }catch(Exception e){
            Log.d("test", e.getMessage());
        }
        if(ticker != null){
            if(ticker.equals(tick)){
                close();
                return true;
            }
        }
        close();
        return false;
    }

    public String getStockTickerFromWatchlist (long id){
        String[] params = new String[1];
        params[0] = "" + id;
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one Stock");
            cursor = db.rawQuery("SELECT * FROM Watchlist WHERE _id=?", params);  //using params helps prevent sql injection
            cursor.moveToFirst();
        }
        String ticker = null;
        try{
            Log.d("test", "Getting Stock Ticker");
            ticker = cursor.getString(cursor.getColumnIndex("ticker"));
            Log.d("test", "Stock: \nTicker: "+ ticker);
        }catch(Exception e){
            Log.d("test", e.getMessage());
        }
        close();
        return  ticker;  //returns stock ticker from Watchlist table
    }

    public boolean checkUserExists(){
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying User");
            try{
                cursor = db.rawQuery("SELECT * FROM User", null);
                cursor.moveToFirst();
                int c = cursor.getCount();
                Log.d("test", "User Count = "+c);
                if(c > 0){
                    Log.d("test", "User Count > 0 = TRUE");
                    cursor.close();
                    return true;
                }
            }catch(SQLiteException e){
                e.printStackTrace();
                return false;
            }
        }
        if(cursor != null){
            cursor.close();
        }
        return  false;  //returns a cursor dataset
    }

    public String getMoneyFromUser(){
        Cursor cursor = null;
        if(open() != null){
            Log.d("test", "Querying one User");
            try{
                cursor = db.rawQuery("SELECT * FROM User", null);
                cursor.moveToFirst();
            }catch(SQLiteException e){
                e.printStackTrace();
                return "0";
            }
        }
        String money = null;
        try{
            Log.d("test", "Getting User Money");
            money = cursor.getString(cursor.getColumnIndex("money"));
            Log.d("test", "User: "+cursor.getInt(cursor.getColumnIndex("_id"))+"\nMoney: "+ money);
            cursor.close();
        }catch(Exception e){
            Log.d("test", e.getMessage());
        }
        return money;
    }

    public long addUserMoney(String money){
        int rowID = -1;
        String[] params = new String[1];
        params[0] = "1";
        double current = Double.parseDouble(getMoneyFromUser());
        double added = Double.parseDouble(money);
        double newTot = current + added;
        newTot = newTot * 100;
        newTot = Math.round(newTot);
        newTot = newTot / 100;
        Log.d("test", "Updating User: \nMoney: $"+current+" + $"+money);
        ContentValues editUser = new ContentValues();
        editUser.put("money", ""+newTot);
        if(open() != null){
            rowID = db.update("User", editUser,"_id=?", params);
        }
        return rowID;
    }

    public long updateUserMoney(String money){
        int rowID = -1;
        String[] params = new String[1];
        params[0] = "1";
        Log.d("test", "Updating User: \nMoney: $"+money);
        ContentValues editUser = new ContentValues();
        editUser.put("money", money);
        if(open() != null){
            rowID = db.update("User", editUser,"_id=?", params);
        }
        return rowID;
    }

    // insert a stock into Stock table
    public long insertMoney(String money){
        Log.d("test", "Inserting User: \nMoney: "+money);
        long rowID = -1;
        ContentValues newUser = new ContentValues();
        //newStock.put("name", name);
        newUser.put("money", money);
        if(open() != null){
            rowID = db.insert("User", null, newUser);
            close();
        }
        return rowID;
    }

    // create Stock table and Watchlist table
    @Override
    public void onCreate(SQLiteDatabase db) {
        //query to create a new table named Courses
        String createQuery1 = "CREATE TABLE Stock "+
                "(_id integer primary key autoincrement,"+
                "ticker TEXT, shares TEXT, purchaseTotal TEXT);";
        db.execSQL(createQuery1);  //execute query to create DB
        String createQuery2 = "CREATE TABLE Watchlist "+
                "(_id integer primary key autoincrement,"+
                "ticker TEXT);";
        db.execSQL(createQuery2);  //execute query to create DB
        String createQuery3 = "CREATE TABLE User "+
                "(_id integer primary key autoincrement,"+
                "money TEXT);";
        db.execSQL(createQuery3);  //execute query to create DB
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // for upgrading the app we can update stuff about the database
    }
}
