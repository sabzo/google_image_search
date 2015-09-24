package com.imagesearch.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.imagesearch.R;
import com.imagesearch.network.GoogleImageRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSearchResults extends android.support.v4.app.Fragment {
    // API URL
    static final String URL = "https://ajax.googleapis.com/ajax/services/search/images";
    // Result View
    GridView gvResults;
    AdapterImage imgAdapter;
    // Global query
    String query = "";
    // Images to be displayed
    ArrayList <ModelImage> images;
    // Required empty public constructor
    public FragmentSearchResults() {}
    public static FragmentSearchResults newInstance(String query) {
      FragmentSearchResults fsr = new FragmentSearchResults();
        Bundle args = new Bundle();
        args.putString("query", query);
        // A fragment can accept Bundle arguments
        fsr.setArguments(args);
      return fsr;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestParams p;
        images = new ArrayList<>();
        // Using the Attaching Activity for the context
        imgAdapter = new AdapterImage(getActivity(), images);

        query = getArguments().getString("query");
        imgAdapter.clear();

        RequestParams params = new RequestParams();
        params.put("q", query);
        params.put("v", 1.0);
        params.put("rsz", 8);
        loadData(URL, params);
    }

    /* Only used for Layout Inflation */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_results, container, false);
        return view;
    }

    /* Ensures fragment's root view isn't null */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        /* Set up Events for Activity */
        gvResults = (GridView) view.findViewById(R.id.gvResults);
        gvResults.setAdapter(imgAdapter);
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
                Intent intent = new Intent(getActivity(), ActivityImageItem.class);
                String url = images.get(i).url;
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        gvResults.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long i) {
                shareImage(view);
                return true;
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
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                }
        );
    }

    // Can be triggered by a view event such as a button press
    public void shareImage(View v) {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) v.findViewById(R.id.ivImage);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Log.i("test", "unable to share image");
            // ...sharing failed, handle error
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            Log.i("test", "break at 195");
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}