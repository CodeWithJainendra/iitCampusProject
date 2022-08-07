package com.shivam.iitcampusproject.Model;

public class Favourites {

    String category,discountAvailable,discount_dec,discount_price,original_price,productId,product_dec,product_image,product_name,quantity,timestamp,uid;

    public Favourites() {
    }

    public Favourites(String category, String discountAvailable, String discount_dec, String discount_price, String original_price, String productId, String product_dec, String product_image, String product_name, String quantity, String timestamp, String uid) {
        this.category = category;
        this.discountAvailable = discountAvailable;
        this.discount_dec = discount_dec;
        this.discount_price = discount_price;
        this.original_price = original_price;
        this.productId = productId;
        this.product_dec = product_dec;
        this.product_image = product_image;
        this.product_name = product_name;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getDiscount_dec() {
        return discount_dec;
    }

    public void setDiscount_dec(String discount_dec) {
        this.discount_dec = discount_dec;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(String original_price) {
        this.original_price = original_price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProduct_dec() {
        return product_dec;
    }

    public void setProduct_dec(String product_dec) {
        this.product_dec = product_dec;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
