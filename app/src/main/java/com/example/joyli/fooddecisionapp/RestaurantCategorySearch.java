package com.example.joyli.fooddecisionapp;

import org.json.JSONArray;

/**
 * Created by Joyli on 2019-01-13.
 */

public class RestaurantCategorySearch implements YelpBusinessSearch {
    private String categroy;
    private float latitude;
    private float longitude;

    RestaurantCategorySearch(String categroy, float latitude, float longitude){
        this.categroy = categroy;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public JSONArray getBusinessInfoBasedOnCategoryAndLocationJSON() {
        return null;
    }
}
