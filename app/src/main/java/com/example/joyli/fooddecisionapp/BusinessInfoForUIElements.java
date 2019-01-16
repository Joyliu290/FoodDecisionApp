package com.example.joyli.fooddecisionapp;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Joyli on 2019-01-13.
 */

public interface BusinessInfoForUIElements {
    void businessRatingUI (String businessRating, ImageView businessRatingImage);
    void businessNameUI (String businessName, TextView businessNameTextview);
    void businessLocationUI(String businessLocation, TextView businessLocationTextview);
    void businessImageUI (String businessImage, ImageView businessImageview);
}
