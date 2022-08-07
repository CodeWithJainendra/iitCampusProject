package com.shivam.iitcampusproject.Model;

public class Questions {

    String timestamp,username,Uid,question,answer,productId,productImage,userImage,Selleruid;

    public Questions() {
    }

    public Questions(String timestamp, String username, String uid, String question, String answer, String productId, String productImage, String userImage, String selleruid) {
        this.timestamp = timestamp;
        this.username = username;
        Uid = uid;
        this.question = question;
        this.answer = answer;
        this.productId = productId;
        this.productImage = productImage;
        this.userImage = userImage;
        Selleruid = selleruid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getSelleruid() {
        return Selleruid;
    }

    public void setSelleruid(String selleruid) {
        Selleruid = selleruid;
    }
}
