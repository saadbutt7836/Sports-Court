package com.canndecsolutions.garrisongamerss.Models;

import java.util.List;

public class SlideShowModelClass {

    private String about;
    private List<String> images;


    public SlideShowModelClass() {

    }

    public SlideShowModelClass(String about, List<String> images) {
        this.about = about;
        this.images = images;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
