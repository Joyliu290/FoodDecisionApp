package com.example.joyli.fooddecisionapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class Restaurant implements IBusiness {
    private Context context;
    private String restaurantImageURL;
    private String restaurantName;
    private String restaurantRate;
    private String restaurantLocation;
    private String restaurantReviewCount;
    private ImageView businessRatingImageView;
    private TextView businessNameTextView;
    private TextView businessLocationTextView;
    private TextView businessReviewCount;
    private ImageView businessImageView;
    private JSONObject restaurantJson;
    private double restaurantLatitude;
    private double restaurantLongitude;

    Restaurant(JSONObject restaurantJson, Context context, ImageView businessRatingImageView, TextView businessNameTextView, TextView businessLocationTextView, TextView businessReviewCount, ImageView businessImageView){
        this.restaurantJson = restaurantJson;
        this.context = context;
        this.businessNameTextView = businessNameTextView;
        this.businessLocationTextView = businessLocationTextView;
        this.businessImageView = businessImageView;
        this.businessRatingImageView = businessRatingImageView;
        this.businessReviewCount = businessReviewCount;
        this.restaurantImageURL = getRestaurantImageURL();
        this.restaurantLocation = getRestaurantLocation();
        this.restaurantName = getRestaurantName();
        this.restaurantRate = getRestaurantRate();
        this.restaurantReviewCount = getRestaurantReviewCount();
    }

    private String getRestaurantImageURL() {
        try {
            return this.restaurantJson.getString("image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getRestaurantName() {
        try {
            return this.restaurantJson.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private String getRestaurantRate() {
        try {
            return this.restaurantJson.getString("rating");
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private String getRestaurantLocation() {
        try {
            JSONObject locationObject = this.restaurantJson.getJSONObject("location");
            return locationObject.getString("address1") + " " + locationObject.getString("city") + ", " + locationObject.getString("state");
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private String getRestaurantReviewCount() {
        try {
            return this.restaurantReviewCount = this.restaurantJson.getString("review_count");
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private void getRestaurantLatitude(){
        try {
            JSONObject coordinateObject = this.restaurantJson.getJSONObject("coordinates");
            this.restaurantLatitude = coordinateObject.getDouble("latitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRestaurantLongitude(){
        try {
            JSONObject coordinateObject = this.restaurantJson.getJSONObject("coordinates");
            this.restaurantLongitude = coordinateObject.getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setBusinessRatingUI (){
        if (this.restaurantRate.equals("0")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_0","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (this.restaurantRate.equals("1")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_1","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (this.restaurantRate.equals("1.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_1_half","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (this.restaurantRate.equals("2")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_2","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (this.restaurantRate.equals("2.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_2_half","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (this.restaurantRate.equals("3")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_3","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (this.restaurantRate.equals("3.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_3_half","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (this.restaurantRate.equals("4")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_4","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (this.restaurantRate.equals("4.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_4_half","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else{
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_5","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
    }
    public void setBusinessNameUI (){
        this.businessNameTextView.setText(this.restaurantName);
    }

    public void setBusinessLocationUI(){
        getRestaurantLatitude();
        getRestaurantLongitude();
        this.businessLocationTextView.setText(this.restaurantLocation);
        this.businessLocationTextView.setPaintFlags(businessLocationTextView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        this.businessLocationTextView.setOnClickListener((View v)-> {
            Intent openLocationInGoogleMap= new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+ restaurantLatitude+ ", " + restaurantLongitude));
            openLocationInGoogleMap.setPackage("com.google.android.apps.maps");
            this.context.startActivity(openLocationInGoogleMap);});

    }
    public void setBusinessImageUI (){
        Picasso
                .get()
                .load(this.restaurantImageURL)
                .into (this.businessImageView);
    }

    public void setBusinessReviewCountUI(){
        this.businessReviewCount.setText("                          Based on " + this.restaurantReviewCount+ " reviews");
    }
}
