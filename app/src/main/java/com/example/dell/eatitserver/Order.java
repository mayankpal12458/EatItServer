package com.example.dell.eatitserver;

/**
 * Created by dell on 1/26/2018.
 */

public class Order {
    String ProductId,ProductName,Quantitiy,Price,Discount;


    public Order() {
    }

    public Order(String productId, String productName, String quantitiy, String price, String discount) {
        ProductId = productId;
        ProductName = productName;
        Quantitiy = quantitiy;
        Price = price;
        Discount = discount;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantitiy() {
        return Quantitiy;
    }

    public void setQuantitiy(String quantitiy) {
        Quantitiy = quantitiy;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
