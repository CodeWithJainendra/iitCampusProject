package com.shivam.iitcampusproject.Model;

public class UserOrderItems {

    String pId,name,cost,quantity,product_Img,Price_Each,shopName;

    public UserOrderItems() {
    }

    public UserOrderItems(String pId, String name, String cost, String quantity, String product_Img, String price_Each, String shopName) {
        this.pId = pId;
        this.name = name;
        this.cost = cost;
        this.quantity = quantity;
        this.product_Img = product_Img;
        Price_Each = price_Each;
        this.shopName = shopName;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProduct_Img() {
        return product_Img;
    }

    public void setProduct_Img(String product_Img) {
        this.product_Img = product_Img;
    }

    public String getPrice_Each() {
        return Price_Each;
    }

    public void setPrice_Each(String price_Each) {
        Price_Each = price_Each;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
