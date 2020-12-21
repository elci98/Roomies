package com.example.rommies;

import java.util.ArrayList;

public class Payment {
    private double amount;
    private String Payer;
    private ArrayList<String> Participants;
    private  String Reason;
    private Date date;

    public Payment(){

    }

    public Payment(String u1, double money , String r, ArrayList<String> uids, Date date){//){
        this.Payer=u1;
        this.amount=money;
        this.Reason=r;
        this.Participants=uids;
        this.date=date;

    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public ArrayList<String> getParticipant() {
        return this.Participants;
    }

    public void setParticipant(ArrayList<String> uids) {
        this.Participants = uids;
    }

    public String getReason() { return this.Reason; }

    public void setReason(String reason) {this.Reason = reason; }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPayer(String Payer) {
        this.Payer = Payer;
    }

    public double getAmount() {
        return amount;
    }

    public String getPayer() {
        return Payer;
    }


}
