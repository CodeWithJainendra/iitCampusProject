package com.shivam.iitcampusproject.Model;

public class AdminCheckSeller {

    String Is_approval,Shop_Status,Uid,address,city,email,mobile,password,shopName,timestamp,userType,username,key;

    public AdminCheckSeller() {
    }

    public AdminCheckSeller(String is_approval, String shop_Status, String uid, String address, String city, String email, String mobile, String password, String shopName, String timestamp, String userType, String username, String key) {
        Is_approval = is_approval;
        Shop_Status = shop_Status;
        Uid = uid;
        this.address = address;
        this.city = city;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.shopName = shopName;
        this.timestamp = timestamp;
        this.userType = userType;
        this.username = username;
        this.key = key;
    }

    public String getIs_approval() {
        return Is_approval;
    }

    public void setIs_approval(String is_approval) {
        Is_approval = is_approval;
    }

    public String getShop_Status() {
        return Shop_Status;
    }

    public void setShop_Status(String shop_Status) {
        Shop_Status = shop_Status;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
