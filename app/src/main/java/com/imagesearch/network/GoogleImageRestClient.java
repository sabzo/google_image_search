package com.imagesearch.network;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;



/**
 * Created by sabelo on 9/16/15.
 */
public class GoogleImageRestClient {
    public static  final String BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images";
    public static AsyncHttpClient client = new AsyncHttpClient();


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }


}
