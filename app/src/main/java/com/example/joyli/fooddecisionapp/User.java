package com.example.joyli.fooddecisionapp;

/**
 * Created by Joyli on 2017-05-31.
 */

public class User {
    private String ID;
    private String name;

    public User (String Entry, String restaurant) {

        ID = Entry;
        name = restaurant;

    }

    public String getEntry(){
        return ID;

    }

    public void setEntry (String Entry2){
        ID = Entry2;
    }

    public String getname() {
        return name;
    }

    public void setname (String restaurantName ){

        name = restaurantName;
    }
}
