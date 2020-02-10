package com.example.weatherapp.Models;

import androidx.annotation.NonNull;

public class User {
    String uid;
    String name,email;

    public User(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    @NonNull
    @Override
    public String toString() {
        return "Users{"+
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' + '}';

    }
}
