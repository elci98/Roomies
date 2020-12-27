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
        regbtn = findViewById(R.id.register_button);
        etName = findViewById(R.id.Name);
        etEmail = findViewById(R.id.Email);
        etPass = findViewById(R.id.Password);
        pb = findViewById(R.id.PrgrsBar);
        fAuth = FirebaseAuth.getInstance();

        regbtn.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v)
            {
                String password = etPass.getText().toString();
                String email = etEmail.getText().toString();
                String name = etName.getText().toString();
                if(password.isEmpty())
                {
                    etPass.setError("You must fill in this field");
                    return;
                }
                if(email.isEmpty())
                {
                    etEmail.setError("You must fill in this field");
                    return;
                }
                if(name.isEmpty())
                {
                    etName.setError("You must fill in this field");
                    return;
                }
                u = new User(email, name);

                pb.setVisibility(View.VISIBLE);
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

                        if(getIntent().hasExtra("com.example.rommies.aprKey"))//handle deep link
                        {
                            Intent intent = new Intent(getApplicationContext(), JoinAprActivity.class);
                            intent.putExtra("com.example.roomies.Name",u.getName());
                            intent.putExtra("com.example.roomies.Uid",u.getUid());
                            intent.putExtra("com.example.rommies.aprKey", getIntent().getStringExtra("com.example.rommies.aprKey"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            return;
                        }
                        Intent intent = new Intent(RegisterActivity.this, afterRegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}