package com.example.rommies;

import java.util.ArrayList;

public class Payment {
    private final double amount;
    private final String Payer;
    private final ArrayList<String> Participants;
    private final String Reason;
    private final Date date;
    private final String key;

    public Payment(String u1, double money , String r, ArrayList<String> uids, Date date,String k){//){
        this.Payer=u1;
        this.amount=money;
        this.Reason=r;
        this.Participants=uids;
        this.date=date;
        this.key = k;
    }

    public Date getDate() { return date; }

    public String getKey() { return key; }


    public ArrayList<String> getParticipant() {
        return this.Participants;
    }

    public String getReason() { return this.Reason; }

    public double getAmount() {
        return amount;
    }

    public String getPayer() {
        return Payer;
    }


}
