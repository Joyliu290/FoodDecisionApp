package com.example.joyli.fooddecisionapp;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Joyli on 2019-01-13.
 */

public class RestaurantUIElements implements BusinessInfoForUIElements {

    private Context context;

    public RestaurantUIElements(Context context){
        this.context = context;
    }

    @Override
    public void businessRatingUI(String businessRating, ImageView businessRatingImage) {
        if (businessRating.equals("0")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_0","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else if (businessRating.equals("1")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_1","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else if (businessRating.equals("1.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_1_half","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else if (businessRating.equals("2")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_2","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else if (businessRating.equals("2.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_2_half","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else if (businessRating.equals("3")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_3","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else if (businessRating.equals("3.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_3_half","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else if (businessRating.equals("4")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_4","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else if (businessRating.equals("4.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_4_half","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }
        else{
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_5","drawable", this.context.getPackageName());
            businessRatingImage.setImageResource(drawableID);
        }

    }

    @Override
    public void businessNameUI(String businessName, TextView businessNameTextview) {
        businessNameTextview.setText(businessName);
    }

    @Override
    public void businessLocationUI(String businessLocation, TextView businessLocationTextview) {
        businessLocationTextview.setText(businessLocation);
    }

    @Override
    public void businessImageUI(String businessImage, ImageView businessImageview) {
        Picasso
                .with(this.context)
                .load(businessImage)
                .into (businessImageview);
    }

}
