package com.example.weatherapp.Models;

import androidx.annotation.NonNull;

import java.util.Date;

public class Message {
    String message;
    String name;
    String key;
    long messageTime;

    public Message(){}

    public Message(String message, String name) {
        this.message = message;
        this.name = name;
        this.key = key;
        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{"+
                "message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", time='" + messageTime + '\'' + '}';
    }
}
