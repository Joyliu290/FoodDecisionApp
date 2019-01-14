package com.example.joyli.fooddecisionapp;

/**
 * Created by Joyli on 2019-01-13.
 */

public interface BusinessInfoForUIElements {
    void businessRatingUI (String rating);
    void businessNameUI (String businessName);
    void businessLocationUI(float businessLatitude, float businessLongitude);
    void businessImageUI (String businessImage);
    void businessPriceRangeUI(String businessPriceRange);
}
