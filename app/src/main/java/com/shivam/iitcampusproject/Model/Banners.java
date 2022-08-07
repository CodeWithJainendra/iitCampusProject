package com.shivam.iitcampusproject.Model;

public class Banners {

    String Imagelink,timestamp;

    public Banners() {
    }

    public Banners(String imagelink, String timestamp) {
        Imagelink = imagelink;
        this.timestamp = timestamp;
    }

    public String getImagelink() {
        return Imagelink;
    }

    public void setImagelink(String imagelink) {
        Imagelink = imagelink;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
