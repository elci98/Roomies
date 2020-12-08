package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private Button login;
    private FirebaseDatabase db;
    private FirebaseAuth fAuth;
    private EditText etEmail, etPass;
    private DatabaseReference userRef;
    private User user;
    private DataSnapshot dataSnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userRef = FirebaseDatabase.getInstance().getReference("/Users/");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                dataSnapshot = snapshot;
                System.out.println("1111");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        fAuth = FirebaseAuth.getInstance();
        login = (Button)findViewById(R.id.login);
        etEmail = (EditText)findViewById(R.id.email);
        etPass = (EditText)findViewById(R.id.password);

        login.setOnClickListener((v) ->
        {

            String email = etEmail.getText().toString();
            String password = etPass.getText().toString();
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task)->
            {
                if(!task.isSuccessful())
                {
                    Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(Login.this, "login succeed", Toast.LENGTH_SHORT).show();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    user = (User) ds.getValue(User.class);
                    if(user.getAprKey() == task.getResult().getUser().getUid())
                        break;
                }
                Intent intent = null;
                if(user.getAprKey()==null)
                {
                    intent = new Intent(this, afterRegister.class);
                }
                else
                    {
                  intent = new Intent(this, Apartment.class);
                  intent.putExtra("com.example.rommies.aprKey",user.getAprKey());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });

        });
    }






}