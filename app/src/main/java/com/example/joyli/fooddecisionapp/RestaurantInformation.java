package com.example.joyli.fooddecisionapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joyli on 2019-01-13.
 */

public class RestaurantInformation implements BusinessInformation {

    private JSONObject businessJSON;

    RestaurantInformation(JSONObject businessJSON){
        this.businessJSON = businessJSON;
    }

    @Override
    public String getBusinessImageURL() {
        try {
            return this.businessJSON.getString("image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getBusinessName() {
        try {
            return this.businessJSON.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    public String getBusinessRating() {
        try {
            return this.businessJSON.getString("rating");
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    public String getBusinessLocation() {
        try {
            JSONObject locationObject = this.businessJSON.getJSONObject("location");
            return locationObject.getString("address1") + " " + locationObject.getString("city") + ", " + locationObject.getString("state");
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    public String getBusinessPriceRange() {
        try {
            return this.businessJSON.getString("price");
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

}
