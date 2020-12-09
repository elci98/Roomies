package com.example.rommies;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
{
    private DatabaseReference fdb2 = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference fdb = FirebaseDatabase.getInstance().getReference("Apartments");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void register(View v)

    {
        startActivity(new Intent(this, RegisterActivity.class));
    }
    public void login(View v)
    {
        startActivity(new Intent(this, LoginActivity.class));
    }
}