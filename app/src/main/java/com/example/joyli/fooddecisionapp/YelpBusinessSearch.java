package com.example.joyli.fooddecisionapp;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Joyli on 2019-01-13.
 */

public interface YelpBusinessSearch {
    public JSONArray listOfBuinessesBasedOnCategoryAndLatLongLocation(String categoryName, float latitude, float longitude);
    public String businessImageURL(JSONObject business);
}
