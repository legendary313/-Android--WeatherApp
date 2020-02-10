package com.example.weatherapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reminder implements Serializable {
    LocalDate date;
    LocalTime time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Reminder(int hour, int minute, int second, int day, int month, int year)
    {
        date = LocalDate.of(year, month, day);
        time = LocalTime.of(hour, minute, second);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Reminder clone(Reminder other)
    {
        date = other.date;
        time = other.time;
        return this;
    }
}
