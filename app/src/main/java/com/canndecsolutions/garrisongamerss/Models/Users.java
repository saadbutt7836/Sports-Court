package com.canndecsolutions.garrisongamerss.Models;

import java.util.ArrayList;

public class Users {
    private String email, name, profile_img, telephone;
    ArrayList<String> interests;

    public Users() {

    }

    public Users(String email, String name, String profile_img, String telephone) {
        this.email = email;
        this.name = name;
        this.profile_img = profile_img;
        this.telephone = telephone;
        this.interests = interests;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }
}
