package com.example.dell.eatitserver;

import java.util.ArrayList;
import java.util.List;


public class modelRequest {

    private  String phone;
    private String address;
    private String total;
    private String name;
    private String status;
    //private List<Order> foods;


    public modelRequest() {
    }

    public modelRequest(String phone, String address, String total, String name) {
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.name = name;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
