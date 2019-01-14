package com.example.joyli.fooddecisionapp;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Joyli on 2019-01-13.
 */

public interface BusinessInformation {
    String businessImageURL(JSONObject businessJSON);
    String businessName(JSONObject businessJSON);
    String businessRating(JSONObject businessJSON);
    String businessLocation(JSONObject businessJSON);
    String businessPriceRange(JSONObject businessJSON);
}
