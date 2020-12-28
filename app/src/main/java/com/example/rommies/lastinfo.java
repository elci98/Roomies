package com.example.rommies;

public class lastinfo {

    private double amount;
    private String Payer;
    private  String Reason;
    private Date date;
    private String partic;
    private String key;

    public lastinfo(){}

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

    public void setKey(String k) { this.key = k; }

    public String getKey(){ return key; }
}
