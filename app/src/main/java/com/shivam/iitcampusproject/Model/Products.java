package com.shivam.iitcampusproject.Model;

public class Products {

    String productId;
    String product_name;
    String product_desc;
    String category;
    String quantity;
    String price;
    String discountPrice;
    String discount_desc;
    String product_image;
    String timestamp;
    String uid;
    String discount_available;

    public Products() {
    }

    public Products(String productId, String product_name, String product_desc, String category, String quantity, String price, String discountPrice, String discount_desc, String product_image, String timestamp, String uid, String discount_available) {
        this.productId = productId;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.discountPrice = discountPrice;
        this.discount_desc = discount_desc;
        this.product_image = product_image;
        this.timestamp = timestamp;
        this.uid = uid;
        this.discount_available = discount_available;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscount_desc() {
        return discount_desc;
    }

    public void setDiscount_desc(String discount_desc) {
        this.discount_desc = discount_desc;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
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

    public String getDiscount_available() {
        return discount_available;
    }

    public void setDiscount_available(String discount_available) {
        this.discount_available = discount_available;
    }
}
