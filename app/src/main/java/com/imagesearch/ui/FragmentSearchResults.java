package com.imagesearch.ui;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.imagesearch.R;
import com.imagesearch.network.GoogleImageRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSearchResults extends android.support.v4.app.Fragment {
    // API URL
    static final String URL = "https://ajax.googleapis.com/ajax/services/search/images";
    // Result View
    GridView gvResults;
    AdapterImage imgAdapter;
    // Images to be displayed
    ArrayList <ModelImage> images;
    // Required empty public constructor
    public FragmentSearchResults() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        images = new ArrayList<>();
        // Using the Attaching Activity for the context
        imgAdapter = new AdapterImage(getActivity(), images);

        ///query = q;
        imgAdapter.clear();
        RequestParams params = new RequestParams();
       //params.put("q", query);
        params.put("v", 1.0);
        params.put("rsz", 8);
        loadData(URL, params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        gvResults = (GridView) container.findViewById(R.id.gvResults);
        gvResults.setAdapter(imgAdapter);
        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
            /* Set up Events for Activity */
        setUpEvents();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpEvents() {
        int threshold = 8;
        int start = 0;
        int increment = 8;
        int end = 56;
        gvResults.setOnScrollListener(new EndlessScrollerListener(threshold, start, increment, end) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                RequestParams params = new RequestParams();
                if (totalItemsCount > 0) {
                    params.put("start", page);
                    params.put("v", 1.0);
                    params.put("rsz", 8);
                   // params.put("q", query);
                    Log.i("test", "Page: " + page);
                    //loadData(URL, params);
                }

            }
        });

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ActivityImageItem.class);
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
                        Toast.makeText(getActivity(), "Failed to load images", Toast.LENGTH_LONG);
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                }
        );
    }
}
