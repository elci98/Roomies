package com.example.rommies;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity
{

    private Button regbtn;
    private FirebaseAuth fAuth;
    private EditText etEmail, etName, etPass;
    private String Uid = "";
    private ProgressBar pb;
    private User u;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regbtn = (Button)findViewById(R.id.register_button);
        etName = (EditText)findViewById(R.id.Name);
        etEmail = (EditText)findViewById(R.id.Email);
        etPass = (EditText)findViewById(R.id.Password);
        pb = (ProgressBar)findViewById(R.id.PrgrsBar);
        fAuth = FirebaseAuth.getInstance();

        regbtn.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v)
            {
                u = new User(etEmail.getText().toString(), etName.getText().toString());
                pb.setVisibility(View.VISIBLE);
                String password = etPass.getText().toString();
                fAuth.createUserWithEmailAndPassword(u.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (!task.isSuccessful())
                        {
                            pb.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                        Uid = task.getResult().getUser().getUid();
                        u.setUid(Uid);
                        dbRef.child("Users").child(Uid).setValue(u);
                        pb.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "user created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, afterRegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}