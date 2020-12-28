package com.example.rommies;

import androidx.annotation.NonNull;

public class Date {
    private int day;
    private int month;
    private int year;

    public Date(){ }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    @NonNull
    public String toString()
    {
        int d=year%2000;
        return day+"/"+month+"/"+d;
    }
}
