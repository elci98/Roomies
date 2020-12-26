package com.example.rommies;

public class Date {
    private int day;
    private int month;
    private int year;
    private String data;

    public Date(){ }
    public Date (String dat){
        this.data=dat;
    }

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

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
    public String toString()
    {
        int d=year%2000;
        return day+"/"+month+"/"+d;
    }
}
