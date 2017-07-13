package com.example.joyli.fooddecisionapp;

import java.util.List;

public class Restaurantdb {
    private String name;
    private String mainUrl;
    private String picUrl;
    private Double latitude;
    private Double longitude;
    private String url;
    private List<String> pictures;
    private String rating;
    private String review;
    private String location;
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
    public Double getLatitude() {return latitude;}
    public Double getLongitude() {return longitude;}

    public void setLatitude(Double latitude) {this.latitude = latitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}

    public String getUrl () {return url;}
    public void setUrl (String url) {this.url=url;}

    public String getReviews() {return review; }
    public void setReview(String review) {this.review = review;}

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLocation(){return location;}

    public void setLocation(String location) {
        this.location = location;
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

