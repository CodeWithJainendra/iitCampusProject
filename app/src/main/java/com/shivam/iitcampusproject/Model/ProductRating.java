package com.shivam.iitcampusproject.Model;

public class ProductRating {

    private String userUid,ratings,review,timestamp;

    public ProductRating() {
    }

    public ProductRating(String userUid, String ratings, String review, String timestamp) {
        this.userUid = userUid;
        this.ratings = ratings;
        this.review = review;
        this.timestamp = timestamp;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
