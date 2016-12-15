package com.hayanesh.dsm;

/**
 * Created by Hayanesh on 07-Dec-16.
 */

public class Details {
    int id;
    String name, email,phone,locality,category, type,pass,pin;

    public  Details() {}
    public Details(int id,String name,String email,String pass,String phone,String locality,String pin,String category,String type)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.locality = locality;
        this.category = category;
        this.type = type;
        this.pass = pass;
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
