package com.example.joyli.fooddecisionapp;

import org.json.JSONArray;

/**
 * Created by Joyli on 2019-01-13.
 */

public class RestaurantCategorySearch implements YelpBusinessSearch {
    private String category;
    private float latitude;
    private float longitude;

    RestaurantCategorySearch(String category, float latitude, float longitude){
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public JSONArray getBusinessInfoBasedOnCategoryAndLocationJSON() {

        return null;
    }
}
