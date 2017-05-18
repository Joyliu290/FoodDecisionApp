package com.example.joyli.fooddecisionapp;

import java.util.List;

public class Restaurantdb {
    private String name;
    private String mainUrl;
    private String picUrl;
    private List<String> pictures;
    private String rating;
    private int currPic;

    public Restaurantdb(String name, String mainUrl) {
        this.name = name;
        setMainUrl(mainUrl);
        currPic = 0;
    }

    public int getCurrPic() {
        return currPic;
    }

    public void incCurrPic(int currPic) {
        this.currPic++;

    }

    public void decCurrPic(int currPic){
        this.currPic--;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
        this.picUrl=mainUrl.replace("/biz","/biz_photos");
        this.picUrl+="?tab=food";
    }
}
