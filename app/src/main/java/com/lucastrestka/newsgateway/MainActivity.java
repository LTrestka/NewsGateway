package com.lucastrestka.newsgateway;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BroadcastReceiver receiver1 = new receiver();
    private BroadcastReceiver receiver2 = new receiver();

    private Menu m;

    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<String> items = new ArrayList<>();
    private int index;
    private ArrayList<Sources> sources = new ArrayList<>();
    private ArrayList<Sources> selectSources = new ArrayList<>();
    private ArrayList<Articles> articles = new ArrayList<>();
    private String category;
    private int position;
    private boolean regiterOn = false;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onRestore: fuck you ");
        registerReceivers();

        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!sources.isEmpty()) {
            Log.d(TAG, "onSaveInstanceState: saving sources");
            outState.putSerializable("SOURCES", sources);
            outState.putString("CATEGORY", category);
            outState.putStringArrayList("ITEMS", items);
            if(!articles.isEmpty()) {
                position = pager.getCurrentItem();
                Log.d(TAG, "onSaveInstanceState: saving articles");
                outState.putLong("BASE_ID", pageAdapter.baseId);
                outState.putInt("INDEX", index);
                outState.putSerializable("ARTICLES", articles);
                outState.putInt("POSITION", pager.getCurrentItem());
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("SOURCES")) {
            Log.d(TAG, "onRestoreInstanceState: contains sources");
            sources = (ArrayList<Sources>) savedInstanceState.getSerializable("SOURCES");
            category = savedInstanceState.getString("CATEGORY");
            items = savedInstanceState.getStringArrayList("ITEMS");
            if (savedInstanceState.containsKey("ARTICLES")) {
                index = savedInstanceState.getInt("INDEX");
                articles = (ArrayList<Articles>) savedInstanceState.getSerializable("ARTICLES");
                position = savedInstanceState.getInt("POSITION");
                pageAdapter.baseId = savedInstanceState.getLong("BASE_ID");
                Log.d(TAG, "onRestoreInstanceState: contains articles, " + "index = " + index + ", position = " + position);

            }
        }
    }

    public void registerReceivers(){
        IntentFilter filter = new IntentFilter("SOURCE_REPORT");
        registerReceiver(receiver1, filter);
        IntentFilter articleFilter = new IntentFilter("ARTICLE_REPORT");
        registerReceiver(receiver2, articleFilter);
        Log.d(TAG, "onResume: registered receivers");
        regiterOn = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sources.isEmpty()) {
            Log.d(TAG, "onCreate: sources is empty");

            Intent intent = new Intent(MainActivity.this, services.class);
            intent.setAction("PARSE_SOURCES");
            intent.putExtra("PARSE_SOURCES", "Go");
            startService(intent);

            category = "all";
        }
        else{
            makeAdapter();
            if(!articles.isEmpty()){
                categorySorter(category);
                reDoFragments(index);
                setTitle(items.get(index));
                pager.setBackground(null);

                //fragments.add(MyFragment.newInstance(articles.get(position), position, articles.size()));
                //pageAdapter.notifyChangeInPosition(position);

                pageAdapter.notifyDataSetChanged();
                pager.setCurrentItem(position);

            }
        }
    }

    @Override
    public void onClick(View view){


        int ref = pager.getCurrentItem();
        Log.d(TAG, "onClick: " + ref);

        String url = articles.get(ref).getUrl();


        Intent intent = new Intent();
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
        }
        startActivity(intent);
    }


    private void selectItem(int position) {
        setTitle(items.get(position));
        if (category.equals("all")) {
            for (int i = 0; i < sources.size(); i++) {
                if (sources.get(i).getId().equals(selectSources.get(position).getId())) {
                    getArticles(i);
                    break;
                }
            }
        }
        else {
            getArticles(position);
        }
        pager.setBackground(null);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        try {

            if (mDrawerToggle.onOptionsItemSelected(item)) {
                Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
                return true;
            }
            switch (item.getItemId()) {
                case R.id.all_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.science_and_nature_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.gaming_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.music_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.general_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.politics_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.technology_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.sports_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.entertainment_menu_item:
                    category = item.getTitle().toString();
                    break;
                case R.id.business_menu_item:
                    category = item.getTitle().toString();
                    break;
                default:
                    break;
            }
            categorySorter(category);
        }catch (Exception e){
            Log.d(TAG, "onOptionsItemSelected exception: "+ e);
        }

        //((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        //mDrawerList.setAdapter(new ArrayAdapter<>(this,  R.layout.drawer_item, countryData.get(selection)));

        return super.onOptionsItemSelected(item);
    }

    // Sorts by relevant category and returns new list
    private ArrayList<String> categorySorter(String category){
        selectSources.clear();
        items.clear();
        if (!category.equals("all") && !category.equals("science and nature")) {
            for (int i = 0; i < sources.size(); i++) {
                if (sources.get(i).getCategory().equals(category)) {
                    selectSources.add(sources.get(i));
                    items.add(sources.get(i).getName());
                }
            }
        }
        else if (category.equals("science and nature")){
                for (int i = 0; i < sources.size(); i++) {
                    if (sources.get(i).getCategory().equals("science") || sources.get(i).getCategory().equals("nature")) {
                        selectSources.add(sources.get(i));
                        items.add(sources.get(i).getName());
                    }
                }
        }
        else{
            for (int i = 0; i < sources.size(); i++){
                selectSources.add(sources.get(i));
                items.add(sources.get(i).getName());
            }
        }
        makeAdapter();
        return items;
    }

    public void makeAdapter(){

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, items));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

       pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
         //mDrawerToggle.syncState();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        pager.setCurrentItem(position);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        try {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }catch (Exception e){
            Log.d(TAG, "onConfigurationChanged exception: "+ e);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        m = menu;
        return true;
    }

    private void getArticles(int idx){

        String newsID;
        index = idx;
        if(articles.isEmpty()){
            categorySorter(category);
        }

        if(category.equals("all")){
            newsID = sources.get(idx).getId();
        }
        else {
            newsID = selectSources.get(idx).getId();
        }
        Log.d(TAG, "getArticles: "+newsID);
        Intent intent = new Intent(MainActivity.this, services.class);
        intent.setAction("PARSE_ARTICLES");
        intent.putExtra("NEWS-ID", newsID);
        intent.putExtra("POSITION", idx);
        startService(intent);

    }

    private void reDoFragments(int idx) {


            Log.d(TAG, "reDoFragments: Passed IDX" + idx);


            fragments.clear();

            index = idx;
            int count = articles.size();
            int fragPos;

            for (int i = 0; i < count; i++) {
                fragments.add(MyFragment.newInstance(articles.get(i), i, articles.size()));
            }

            for (int i = 0; i < pageAdapter.getCount(); i++)
                pageAdapter.notifyChangeInPosition(i);

            pageAdapter.notifyDataSetChanged();

            //pager.setCurrentItem(0);
    }



    class receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction()){

                case "SOURCE_REPORT":
                    Log.d(TAG, "onReceive: made it here");
                    sources = (ArrayList<Sources>)intent.getSerializableExtra("SOURCE_LIST");
                    for (int i = 0; i < sources.size(); i++){
                        items.add(sources.get(i).getName());
                    }
                    categorySorter("all");
                    break;
                case "ARTICLE_REPORT":
                    Log.d(TAG, "onReceive: Got articles");
                    articles = (ArrayList<Articles>) intent.getSerializableExtra("ARTICLE_LIST");
                    int position = intent.getIntExtra("POSITION", 0);
                    reDoFragments(position);
                    pager.setCurrentItem(0);
                    Log.d(TAG, "onReceive: " + articles.get(0).getAuthor());
                    break;
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(regiterOn) {
            unregisterReceiver(receiver1);
            unregisterReceiver(receiver2);
        }
        Intent intent = new Intent(MainActivity.this, services.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(MainActivity.this, services.class);
        try {
            stopService(intent);
        }
        catch (Exception e){
            Log.d(TAG, "onDestroy: reciever / services null");
        }
        super.onDestroy();
    }

    /*********************************************************************************************
     *                                                                                           *
     *                               Page Adapter                                                *
     *                                                                                           *
     *********************************************************************************************/


    private class MyPageAdapter extends FragmentPagerAdapter {
        public long baseId = 0;

        public MyPageAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() +n;
            Log.d(TAG, "notifyChangeInPosition: "+ baseId);
        }



    }


}
