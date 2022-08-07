package com.shivam.iitcampusproject.Model;

public class ShowShopOrders {

    private String orderId,orderTime,orderStatus,orderCost,OrderBy,orderTo,ShopName,product_Img;

    public ShowShopOrders() {
    }

    public ShowShopOrders(String orderId, String orderTime, String orderStatus, String orderCost, String orderBy, String orderTo, String shopName, String product_Img) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.orderCost = orderCost;
        OrderBy = orderBy;
        this.orderTo = orderTo;
        ShopName = shopName;
        this.product_Img = product_Img;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String orderBy) {
        OrderBy = orderBy;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getProduct_Img() {
        return product_Img;
    }

    public void setProduct_Img(String product_Img) {
        this.product_Img = product_Img;
    }
}
