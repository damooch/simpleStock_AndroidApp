package com.example.david.cs3270finalmariluch.utils;

import android.util.Log;

import com.example.david.cs3270finalmariluch.data.AUTH;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class StockServiceHelper {

    private String symbol;
    private String period;
    private String current;
    private String close;
    private String name;
    private String result;
    private OkHttpClient client;
    private String baseUrl; //the base url of the web service
    //private String googleUrl = "https://www.google.com/finance/info?q=";
    private ArrayList<String> results;
    private JsonElement element;
    private JsonObject datasets;
    private Set<Map.Entry<String, JsonElement>> entries;

    public StockServiceHelper(String ticker, String _period) {
        super();
        Log.d("test", "StockServiceHelper  Constructor");
        this.client = new OkHttpClient();
        this.symbol = ticker;
        this.period = _period;
        this.results = new ArrayList<>();
    }


    public String queryService () throws IOException {
        Log.d("test", "StockServiceHelper.query("+symbol+","+period+")");
        this.baseUrl = null;
        String p = null;
        result = null;
        switch(period){
            case "d":
                baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+symbol+"&apikey="+ AUTH.STOCK_API_KEY;
                p = "Daily";
                break;
            case "w":
                baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol="+symbol+"&apikey="+ AUTH.STOCK_API_KEY;
                p = "Weekly";
                break;
            case "m":
                baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol="+symbol+"&apikey="+ AUTH.STOCK_API_KEY;
                p = "Monthly";
                break;
            case "i":
                baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+symbol+"&interval=15min&apikey="+ AUTH.STOCK_API_KEY;
                p = "15min";
                break;
            default:
                break;
        }
        this.client = new OkHttpClient();
        final String per = p;
        Request request = new Request.Builder()
                .url(baseUrl)
                .build();

        try(Response response = client.newCall(request).execute()){
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            result = response.body().string().toString();
            //Log.d("test", "Full jsonData:");

            JsonParser parser = new JsonParser();
            // The JsonElement is the root node. It can be an object, array, null or java primitive.
            element = parser.parse(result);
            // use the isxxx methods to find out the type of jsonelement.
            if (element.isJsonObject()) {
                JsonObject values = element.getAsJsonObject();
                if(per != "Weekly" && per != "Monthly"){
                    datasets = values.get("Time Series ("+per+")").getAsJsonObject();
                }else{
                    datasets = values.get(""+per+" Time Series").getAsJsonObject();
                }
                entries = datasets.entrySet();//will return members of my object

                return result;
            }else{
                Log.d("test", "Element is not Json Object");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String queryCurrent() throws IOException {
        Log.d("test", "StockServiceHelper.queryCurrent(" + symbol + ")");

        baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&interval=1min&symbol=" + symbol + "&apikey=66MJUFOIG16CNOZL";
        this.client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl)
                .build();

        try(Response response = client.newCall(request).execute()){
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            result = response.body().string().toString();
            //Log.d("test", "Full jsonData:");

            JsonParser parser = new JsonParser();
            // The JsonElement is the root node. It can be an object, array, null or java primitive.
            element = parser.parse(result);
            // use the isxxx methods to find out the type of jsonelement.
            if (element.isJsonObject()) {
                JsonObject values = element.getAsJsonObject();
                datasets = values.get("Time Series (1min)").getAsJsonObject();
                entries = datasets.entrySet();//will return members of my object
                return result;
            }else{
                Log.d("test", "Element is not Json Object");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;

//        String res = null;
//        try (Response response = client.newCall(request).execute()) {
//            res = response.body().string(); // remove later to just put string in parseQueryData param to ensure nulls
//            //res = res.replace("/", "").replace("[", "").replace("]", "");
//
//            String re1="(\"l_cur\" : \").*?(\")";	// Command Seperated Values 1
//            String re2="(\")";
//            String re3=".*?";	// Non-greedy match on filler
//            String re4="(\"pcls_fix\" : \").*?(\")";	// Command Seperated Values 2
//            String cur;
//            String cls;
//            Pattern p = Pattern.compile(re4,Pattern.CASE_INSENSITIVE);
//            Matcher m = p.matcher(res);
//            if (m.find())
//            {
//                cls=m.group(0);
//                cls = cls.replace(" ", "").replace("\"", "");
//                Log.d("test","m = ("+cls.toString()+")");
//                String [] t = cls.split(":");
//                Log.d("test","("+t[1]+")");
//                setClose(t[1]);
//                this.results.add(0, close);
//            }
//            p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//            m = p.matcher(res);
//            if (m.find())
//            {
//                cur=m.group(0);
//                cur = cur.replace(" ", "").replace("\"", "");
//                Log.d("test","m = ("+cur.toString()+")");
//                String [] s = cur.split(":");
//                Log.d("test","("+s[1]+")");
//                setCurrent(s[1]);
//                this.results.add(1, current);
//            }
//
//            //Log.d("test", "StockServiceHelper RESULT:\n\t"+result+"");
//            //parseQueryData(result);
//            return  res;
//        }
//        catch(Exception e){
//            Log.d("test", "EXCEPTION: "+ e.toString());
//            e.printStackTrace();
//            return null;
//        }
    }


    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getCurrent() {
        return current;
    }
    public void setCurrent(String cur) {
        this.current = cur;
    }
    public String getClose() {
        return close;
    }
    public void setClose(String cl) {
        this.close = cl;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<String> getResults(){
        return results;
    }

    public Set<Map.Entry<String, JsonElement>> getEntries(){
        return entries;
    }
}
