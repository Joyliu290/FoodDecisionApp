package com.example.joyli.fooddecisionapp;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joyli on 2017-05-16.
 */

public class RestaurantScraper {
    public static List<String> getPictures(String html){
        Document doc = Jsoup.parse(html);
        Elements images = doc.select("div.photo-box > img");
        List<String>urls = new ArrayList<>();
        String src;
        for(Element image: images) {

            src = image.attr("src");
            if (src != null && !src.equals("")) {
                urls.add(src);
            }
        }
        return urls;
    }
}

