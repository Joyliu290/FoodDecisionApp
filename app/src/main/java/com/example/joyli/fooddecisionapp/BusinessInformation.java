package com.example.joyli.fooddecisionapp;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Joyli on 2019-01-13.
 */

public interface BusinessInformation {
    String getBusinessImageURL();
    String getBusinessName();
    String getBusinessRating();
    String getBusinessLocation();
    String getBusinessPriceRange();
}
