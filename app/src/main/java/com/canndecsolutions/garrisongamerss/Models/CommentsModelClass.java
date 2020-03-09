package com.canndecsolutions.garrisongamerss.Models;

public class CommentsModelClass {
    private String posted_by, comment, comid;
    private Double timestamp;


    public CommentsModelClass() {
    }

    public CommentsModelClass(String posted_by, String comment, String comid, Double timestamp) {
        this.posted_by = posted_by;
        this.comment = comment;
        this.comid = comid;
        this.timestamp = timestamp;
    }

//    GETTERS

    public String getPosted_by() {
        return posted_by;
    }

    public String getComment() {
        return comment;
    }

    public String getComid() {
        return comid;
    }

    public Double getTimestamp() {
        return timestamp;
    }


//    SETTERS


    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setComid(String comid) {
        this.comid = comid;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }
}
