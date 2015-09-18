package com.imagesearch.ui;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sabelo on 9/16/15.
 */
public class ModelImage {
    String  tbUrl, title, url, width, height;

    ModelImage(JSONObject image) {
        Log.i("test", image.toString());
        try {
            tbUrl = image.getString("tbUrl");
            title = image.getString("title");
            url = image.getString("url");
            width = image.getString("width");
            height = image.getString("height");
            //Log.i("test", tbUrl);
        } catch(JSONException e){
            Log.i("test", "Error parsing JSON in Image class" + e.toString());
        }
    }

    public static ArrayList<ModelImage> fromJSONArray(JSONArray jsonArray) {
        ArrayList<ModelImage> arrayList = new ArrayList<>();
        for( int i = 0; i < jsonArray.length(); i++ ) {
            try {
                arrayList.add( new ModelImage( jsonArray.getJSONObject(i)));
            } catch( JSONException e) {
                Log.i("Debug", e.toString());
            }
        }
        return arrayList;
    }

}
