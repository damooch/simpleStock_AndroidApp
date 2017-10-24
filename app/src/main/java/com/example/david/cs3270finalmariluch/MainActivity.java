package com.example.david.cs3270finalmariluch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.david.cs3270finalmariluch.controllers.BuyStockFragment;
import com.example.david.cs3270finalmariluch.controllers.MoneyManagementFragment;
import com.example.david.cs3270finalmariluch.controllers.MyStockFragment;
import com.example.david.cs3270finalmariluch.controllers.SearchFragment;
import com.example.david.cs3270finalmariluch.controllers.WatchlistFragment;
import com.example.david.cs3270finalmariluch.models.DataModel;
import com.example.david.cs3270finalmariluch.utils.DrawerItemCustomAdapter;

import java.util.List;


//implements NavigationView.OnNavigationItemSelectedListener
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 3;
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private FloatingActionButton fab;
    private FrameLayout content_frame;
    private FrameLayout progress_cont;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private boolean isConnected;
    private int drawerPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("test", "MainActivity.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress_cont = (FrameLayout) findViewById(R.id.progress_cont);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        hideAddBtn();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDrawerItem(1, false);
            }
        });

        setupToolbar();

        DataModel[] drawerItem = new DataModel[5];
        drawerItem[0] = new DataModel(R.drawable.chart, "My Stock");
        drawerItem[1] = new DataModel(R.drawable.search, "Search Stock");
        drawerItem[2] = new DataModel(R.drawable.buy, "Buy Stock");
        drawerItem[3] = new DataModel(R.drawable.stock, "Watchlist");
        drawerItem[4] = new DataModel(R.drawable.pie, "Money Management");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET},
                REQUEST_PERMISSIONS);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("test", "DrawerItemClickListener.onItemClick()");
            Log.d("test", "Current selected position: "+position);
            selectDrawerItem(position, false);
        }

    }

    public void selectDrawerItem(int position, boolean hasParams) {
        Log.d("test", "MainActivity.selectDrawerItem("+position+", "+hasParams+")");
        Log.d("test", "Current selected position: "+position);
        drawerPos = position;
        saveState();
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        setTitle(mNavigationDrawerItemTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        if(!hasParams){
            switch (position) {
                case 0:
                    displayMyStocksFragment();
                    break;
                case 1:
                    displaySearchFragment();
                    break;
                case 2:
                    displaySearchFragment();
                    break;
                case 3:
                    displayWatchlistFragment();
                    break;
                case 4:
                    displayMoneyManagementFragment();
                    break;
                default:
                    break;
            }
        }
    }

    public void showIndeterminateProgress(){
        Log.d("test", "MainActivity.showIndeterminateProgress()");
        progress_cont.setVisibility(View.VISIBLE);
    }

    public void hideIndeterminateProgress(){
        Log.d("test", "MainActivity.hideIndeterminateProgress()");
        progress_cont.setVisibility(View.INVISIBLE);
    }

    public void showContentContainer(){
        Log.d("test", "MainActivity.showContentContainer()");
        content_frame.setVisibility(View.VISIBLE);
    }

    public void hideContentContainer(){
        Log.d("test", "MainActivity.hideContentContainer()");
        content_frame.setVisibility(View.INVISIBLE);
    }

    public void displayWatchlistFragment() {
        Log.d("test", "MainActivity.displayWatchlistFragment()");
        showAddBtn();
        clearSearchStateSave();
        hideContentContainer();
        showIndeterminateProgress();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new WatchlistFragment(), "WLF")
                .addToBackStack(null)
                .commit();
    }

    public void displayMoneyManagementFragment() {
        Log.d("test", "MainActivity.displayMoneyManagementFragment()");
        hideAddBtn();
        clearSearchStateSave();
        showContentContainer();
        hideIndeterminateProgress();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MoneyManagementFragment(), "MMF")
                .addToBackStack(null)
                .commit();
    }

    public void displayBuyFragment(String ticker, String price) {
        Log.d("test", "MainActivity.displayBuyFragment("+ticker+", "+price+")");
        hideAddBtn();
        clearSearchStateSave();
        Bundle bundle = new Bundle();
        bundle.putString("ticker", ticker);
        bundle.putString("price", price);
        BuyStockFragment bsf = new BuyStockFragment();
        bsf.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, bsf, "BSF")
                .addToBackStack(null)
                .commit();
    }

    public void displaySellFragment(String ticker) {
        Log.d("test", "MainActivity.displaySellFragment("+ticker+")");
        hideAddBtn();
        clearSearchStateSave();
        Bundle bundle = new Bundle();
        bundle.putString("ticker", ticker);
//        SellStockFragment ssf = new SellStockFragment();
//        ssf.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.content_frame, ssf, "SSF")
//                .addToBackStack(null)
//                .commit();
    }

    public void displayMyStocksFragment() {
        Log.d("test", "MainActivity.displayMyStocksFragment()");
        hideAddBtn();
        clearSearchStateSave();
        hideContentContainer();
        showIndeterminateProgress();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MyStockFragment(), "MSF")
                .addToBackStack(null)
                .commit();
    }

    public void displaySearchFragment() {
        Log.d("test", "MainActivity.displaySearchFragment()");
        hideAddBtn();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SearchFragment(), "SF")
                .addToBackStack(null)
                .commit();
    }

    public void displaySearchFragment(String ticker) {
        Log.d("test", "MainActivity.displaySellFragment("+ticker+")");
        hideAddBtn();
        //clearSearchStateSave();
        Bundle bundle = new Bundle();
        bundle.putString("ticker", ticker);
        SearchFragment sf = new SearchFragment();
        sf.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, sf, "SF")
                .addToBackStack(null)
                .commit();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("test", "MainActivity.onOptionsItemSelected()");

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        Log.d("test", "MainActivity.setTitle()");
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d("test", "MainActivity.onPostCreate()");
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("test", "MainActivity.onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("test", "MainActivity.onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("test", "MainActivity.onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("test", "MainActivity.onPause()");
        // Save what fragment we are on
        saveState();
    }

    @Override
    protected void onResume() {
        Log.d("test", "MainActivity.onResume()");
        super.onResume();
        // Store what fragment we are on then load it
        restoreState();
    }

    private void saveState(){
        Log.d("test", "MainActivity.saveState()");
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("drawerPos", drawerPos);
        Log.d("test", "\t\t DrawerPos = "+drawerPos);
        editor.commit();
    }

    private void restoreState(){
        Log.d("test", "MainActivity.restoreState()");
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        drawerPos = sharedPref.getInt("drawerPos", 0);
        Log.d("test", "\t\t DrawerPos = "+drawerPos);
        selectDrawerItem(drawerPos, false);
    }

    private void clearSearchStateSave(){
        SearchFragment sf = (SearchFragment) getSupportFragmentManager().findFragmentByTag("SF");
        if(sf != null){
            sf.clearStateSave();
        }
    }

    protected void setupToolbar(){
        Log.d("test", "MainActivity.setupToolbar()");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    protected void setupDrawerToggle(){
        Log.d("test", "MainActivity.setupDrawerToggle()");
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("test", "MainActivity.onRequestPermissionsResult()");
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void showAddBtn(){
        Log.d("test", "MainActivity.showAddBtn()");
        fab.show();

    }

    public void hideAddBtn(){
        Log.d("test", "MainActivity.hideAddBtn()");
        fab.hide();
    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        Log.d("test", "MainActivity.hideSoftKeyboard()");
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && view != null){
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }
}
