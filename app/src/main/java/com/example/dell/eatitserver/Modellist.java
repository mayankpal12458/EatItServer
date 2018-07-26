package com.example.dell.eatitserver;

/**
 * Created by dell on 2/4/2018.
 */

public class Modellist {
    private String Image,Name;

    public Modellist() {
    }

    public String getImage() {

        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Modellist(String image, String name) {

        Image = image;
        Name = name;
    }
}
