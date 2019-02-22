package com.example.joyli.fooddecisionapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Joyli on 2019-01-13.
 */

public class RestaurantCategorySearch implements YelpBusinessSearch {
    private String category;
    private double latitude;
    private double longitude;
    private Context context;

    RestaurantCategorySearch(Context context, String category, double latitude, double longitude){
        this.context = context;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void getBusinessesInfo() {
        String apiKey = this.context.getString(R.string.yelp_api_key);
        // Creating GET request objects to make network calls to Yelp API to retrieve list of businesses under the category
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder getRequestBuilder =
                HttpUrl.parse("https://api.yelp.com/v3/businesses/search").newBuilder();
        getRequestBuilder.addQueryParameter("latitude", Double.toString(this.latitude));
        getRequestBuilder.addQueryParameter("longitude", Double.toString(this.longitude));
        getRequestBuilder.addQueryParameter("categories", this.category);
        String getUrl = getRequestBuilder.build().toString();

        Request getRequest = new Request.Builder()
                .header("Authorization", "Bearer "+apiKey)
                .url(getUrl)
                .build();
        // Sending and receiving network calls
        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()){
                    throw new IOException("GET REQUEST FAILED" + response);
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
