package com.example.rommies;

public class User
{
    private final String email;
    private final String name;
    private String Uid;
    private final String Apartment_key = null;
    public User(String e, String n)
    {
        this.email = e;
        this.name = n;
    }
    public String getEmail(){return email;}
    public String getName(){return name;}
    public String getUid(){return Uid;}
    public void setUid(String uid){this.Uid = uid;}
}
