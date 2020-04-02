package com.canndecsolutions.garrisongamerss.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Posts {

    private String pid, posted_by, status, post_image, category;
    private double timestamp;
    private int type;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Posts() {
    }


    public Posts(String pid, String posted_by, String status, String post_image, String category, double timestamp, int type) {
        this.pid = pid;
        this.posted_by = posted_by;
        this.status = status;
        this.post_image = post_image;
        this.category = category;
        this.timestamp = timestamp;
        this.type = type;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("pid", pid);
        result.put("timestamp", timestamp);
        result.put("category", category);
        result.put("type", type);
        result.put("post_image", post_image);
        result.put("posted_by", posted_by);
        result.put("status", status);
        result.put("starsCount", starCount);
        result.put("stars", stars);

        return result;


    }


//    GETTERS

    public String getPid() {
        return pid;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public String getStatus() {
        return status;
    }

    public String getPost_image() {
        return post_image;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStarCount() {
        return starCount;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

//    SETTERS


    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }
}

