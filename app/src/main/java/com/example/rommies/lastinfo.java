package com.example.rommies;

public class lastinfo {

    private double amount;
    private String Payer;
    private  String Reason;
    private Date date;
    private String partic;

    public lastinfo(){}
    public lastinfo(double amount, String payer, String reason, Date date, String part) {
        this.amount = amount;
        Payer = payer;
        Reason = reason;
        this.date = date;
        this.partic=part;
    }

    public String getPartic() {
        return partic;
    }

    public void setPartic(String partic) {
        this.partic = partic;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayer() {
        return Payer;
    }

    public void setPayer(String payer) {
        Payer = payer;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }



}
