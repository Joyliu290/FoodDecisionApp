package com.example.joyli.fooddecisionapp;

import org.json.JSONArray;

/**
 * Created by Joyli on 2019-01-13.
 */

public interface YelpBusinessSearch {
    JSONArray businessInfoBasedOnCategoryAndLocationJSON (String category, float latitude, float longitude);
}
