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
    private ImageView businessRatingImageView;
    private TextView businessNameTextView;
    private TextView businessLocationTextView;
    private ImageView businessImageView;

    public RestaurantUIElements(Context context, ImageView businessRatingImageView, TextView businessNameTextView, TextView businessLocationTextView, ImageView businessImageView){
        this.context = context;
        this.businessRatingImageView = businessRatingImageView;
        this.businessImageView = businessImageView;
        this.businessLocationTextView = businessLocationTextView;
        this.businessNameTextView = businessNameTextView;
        this.businessLocationTextView = businessLocationTextView;
    }

    @Override
    public void setBusinessRatingUI(String businessRating) {
        if (businessRating.equals("0")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_0","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (businessRating.equals("1")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_1","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (businessRating.equals("1.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_1_half","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (businessRating.equals("2")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_2","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (businessRating.equals("2.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_2_half","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (businessRating.equals("3")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_3","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (businessRating.equals("3.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_3_half","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (businessRating.equals("4")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_4","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else if (businessRating.equals("4.5")){
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_4_half","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }
        else{
            int drawableID= this.context.getResources().getIdentifier ("stars_regular_5","drawable", this.context.getPackageName());
            this.businessRatingImageView.setImageResource(drawableID);
        }

    }

    @Override
    public void setBusinessNameUI(String businessName) {
        this.businessNameTextView.setText(businessName);
    }

    @Override
    public void setBusinessLocationUI(String businessLocation) {
        this.businessLocationTextView.setText(businessLocation);
    }

    @Override
    public void setBusinessImageUI(String businessImage) {
        Picasso
                .with(this.context)
                .load(businessImage)
                .into (this.businessImageView);
    }

}
