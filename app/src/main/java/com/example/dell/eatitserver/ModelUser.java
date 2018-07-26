package com.example.dell.eatitserver;

/**
 * Created by dell on 2/3/2018.
 */

public class ModelUser {
    String email,password;

    public ModelUser() {
    }

    public ModelUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
