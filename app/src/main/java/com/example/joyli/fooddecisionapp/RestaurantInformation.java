package com.example.joyli.fooddecisionapp;

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
        return null;
    }

    @Override
    public String getBusinessName() {
        return null;
    }

    @Override
    public String getBusinessRating() {
        return null;
    }

    @Override
    public String getBusinessLocation() {
        return null;
    }

    @Override
    public String getBusinessPriceRange() {
        return null;
    }
}
