package com.imagesearch.ui;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.imagesearch.R;
import com.imagesearch.network.GoogleImageRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivitySearch extends AppCompatActivity {
    GridView gvResults;
    AdapterImage imgAdapter;
    static final String URL = "https://ajax.googleapis.com/ajax/services/search/images";
    ArrayList <ModelImage> images;
    String query;
    /* Initialize Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpViews();
        setUpEvents();
    }

    /* Initialize reference to Views */
    private void setUpViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        images = new ArrayList<>();
        imgAdapter = new AdapterImage(this, images);
        gvResults.setAdapter(imgAdapter);
    }

    /* Set up Events for Activity */
    private void setUpEvents() {
        int threshold = 2;
        int start = 0;
        int increment = 8;
        int end = 56;
        gvResults.setOnScrollListener(new EndlessScrollerListener(threshold, start, increment, end) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                RequestParams params = new RequestParams();
                if( totalItemsCount > 0) {
                    params.put("start", page);
                    params.put("v", 1.0);
                    params.put("rsz", 8);
                    params.put("q", query);
                    Log.i("test", "Page: " + page);
                    loadData(URL, params);
                }

            }
        });

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ActivityImageItem.class);
                String url = images.get(i).url;
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }


    /* Load Data */
    private void loadData(String URL, RequestParams params){
        GoogleImageRestClient.get(URL, params,
                //callback
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("Debug", response.toString());
                        JSONArray jsonImages;
                        try {
                            jsonImages = response.getJSONObject("responseData").getJSONArray("results");
                            imgAdapter.addAll(ModelImage.fromJSONArray(jsonImages));
                            imgAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.i("test", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String
                            responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Failed to load images", Toast.LENGTH_LONG);
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                query = q;
                imgAdapter.clear();
                RequestParams params = new RequestParams();
                params.put("q", query);
                params.put("v", 1.0);
                params.put("rsz", 8);
                loadData(URL, params);
                return true;
            }

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
