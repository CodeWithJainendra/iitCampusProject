package com.shivam.iitcampusproject.Model;

public class CartItem {

    String Item_Name,Item_PID,Item_Price,Item_Price_Each,Item_Quantity,SellerUid,productId,timestamp,Item_Image,DeliveryFee,status,TotalPrice;


    public CartItem() {
    }

    public CartItem(String item_Name, String item_PID, String item_Price, String item_Price_Each, String item_Quantity, String sellerUid, String productId, String timestamp, String item_Image, String deliveryFee, String status, String totalPrice) {
        Item_Name = item_Name;
        Item_PID = item_PID;
        Item_Price = item_Price;
        Item_Price_Each = item_Price_Each;
        Item_Quantity = item_Quantity;
        SellerUid = sellerUid;
        this.productId = productId;
        this.timestamp = timestamp;
        Item_Image = item_Image;
        DeliveryFee = deliveryFee;
        this.status = status;
        TotalPrice = totalPrice;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getItem_PID() {
        return Item_PID;
    }

    public void setItem_PID(String item_PID) {
        Item_PID = item_PID;
    }

    public String getItem_Price() {
        return Item_Price;
    }

    public void setItem_Price(String item_Price) {
        Item_Price = item_Price;
    }

    public String getItem_Price_Each() {
        return Item_Price_Each;
    }

    public void setItem_Price_Each(String item_Price_Each) {
        Item_Price_Each = item_Price_Each;
    }

    public String getItem_Quantity() {
        return Item_Quantity;
    }

    public void setItem_Quantity(String item_Quantity) {
        Item_Quantity = item_Quantity;
    }

    public String getSellerUid() {
        return SellerUid;
    }

    public void setSellerUid(String sellerUid) {
        SellerUid = sellerUid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getItem_Image() {
        return Item_Image;
    }

    public void setItem_Image(String item_Image) {
        Item_Image = item_Image;
    }

    public String getDeliveryFee() {
        return DeliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        DeliveryFee = deliveryFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        TotalPrice = totalPrice;
    }
}
