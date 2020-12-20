package com.example.rommies;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private String aprKey = null;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri uri = getIntent().getData();
        if(uri != null)
        {
            aprKey = uri.getLastPathSegment();
        }
    }
    public void register(View v)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        if(aprKey != null)
            intent.putExtra("com.example.rommies.aprKey",aprKey);
        startActivity(intent);
    }
    public void login(View v)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        if(aprKey != null)
            intent.putExtra("com.example.rommies.aprKey",aprKey);
        startActivity(intent);
    }
}