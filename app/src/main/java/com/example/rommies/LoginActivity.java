package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private FirebaseAuth fAuth;
    private EditText etEmail, etPass;
    private DatabaseReference userRef;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pb = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        etEmail = findViewById(R.id.email);
        etPass = findViewById(R.id.password);

        login.setOnClickListener((v) ->
        {
            pb.setVisibility(View.VISIBLE);
            String email = etEmail.getText().toString();
            String password = etPass.getText().toString();
            if(password.isEmpty())
            {
                etPass.setError("You must fill in this field");
                pb.setVisibility(View.GONE);
                return;
            }
            if(email.isEmpty())
            {
                etEmail.setError("You must fill in this field");
                pb.setVisibility(View.GONE);
                return;
            }
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task)->
            {
                if(!task.isSuccessful())
                {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(LoginActivity.this, "login succeed", Toast.LENGTH_SHORT).show();

                userRef = FirebaseDatabase.getInstance().getReference("/Users/"+task.getResult().getUser().getUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        Intent intent = null;
                        if(snapshot.hasChild("Apartment_key"))
                        {
                            intent = new Intent(getApplicationContext(), ApartmentActivity.class);
                            intent.putExtra("com.example.rommies.aprKey",(String)snapshot.child("Apartment_key").getValue());
                        }
                        else if(getIntent().hasExtra("com.example.rommies.aprKey"))//handle deep linking
                        {
                            intent = new Intent(getApplicationContext(), JoinAprActivity.class);
                            intent.putExtra("com.example.roomies.Name",(String)snapshot.child("name").getValue());
                            intent.putExtra("com.example.roomies.Uid",fAuth.getUid());
                            intent.putExtra("com.example.rommies.aprKey", getIntent().getStringExtra("com.example.rommies.aprKey"));
                        }
                        else
                            intent = new Intent(getApplicationContext(), afterRegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        pb.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            });
        });
    }
}