package com.imagesearch.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.imagesearch.ActivitySettings;
import com.imagesearch.R;
import com.loopj.android.http.RequestParams;

/* This Activity uses a Pattern in which the Fragments and activity are used to Display Data */
public class ActivitySearch extends AppCompatActivity {

    private int SEARCH_SETTINGS = 10;

    /* Initialize Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Load the initial screen
        loadFirstFragment();
    }

    @Override
    protected void onResume() {
        /* Whenever this Activity is at front of Activity Stack being interacted
        with ensure the internet is there */
        if(! isNetworkAvailable() ) {
            Toast.makeText(this, "Internet Not Available", Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    /* Loads the first fragment */
    private void loadFirstFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flSearchPlaceHolder, new FragmentSearch());
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        /* Using the support Library get a reference to the searchView */
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        /* Listen for when a query is searched */
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flSearchPlaceHolder, FragmentSearchResults.newInstance(query), "SEARCH_RESULTS");
                ft.commit();
                return true;
            }

            /* This event's signature is required, but don't need to implement anything */
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, ActivitySettings.class);
            startActivityForResult(i, SEARCH_SETTINGS);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == Activity.RESULT_OK && requestCode == SEARCH_SETTINGS ) {
            String imageSize = data.getStringExtra("imageSize");
            String colorFilter = data.getStringExtra("colorFilter");
            String imageType = data.getStringExtra("imageType");
            String siteFilter = data.getStringExtra("siteFilter");
            Log.i("test", imageSize + " " + colorFilter + " " + imageType + " " + siteFilter);
            //imgsz, imgcolor, imgtype, as_sitesearch
            // Caveat: Fragment Search Results may not have been yet created :/
            // Need to use Library such as EventBus to store these search settings results
            // Or use local file storage
            try {
                FragmentSearchResults fsr = (FragmentSearchResults) getSupportFragmentManager().findFragmentByTag("SEARCH_RESULTS");
                    RequestParams params = new RequestParams();
                    params.put("q", fsr.query);
                    params.put("v", 1.0);
                    params.put("rsz", 8);
                    if( imageSize != "any" ) params.put("imgsz", imageSize);
                    if( colorFilter != "any" ) params.put("imgcolor", colorFilter);
                    if( imageType != "any") params.put("imgtype", imageType);
                    if ( siteFilter != "any" ) params.put("as_sitesearch", siteFilter);
                    Log.i("test", "Now searching");
                Toast.makeText(this, "saved settings", Toast.LENGTH_SHORT).show();
                    fsr.loadData(fsr.URL, params);
            } catch (Exception e) {
                Log.i("test",  "FragmentSearchResults not created");
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }


        }


    }

    /* Helper function to check if Network is available */
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
