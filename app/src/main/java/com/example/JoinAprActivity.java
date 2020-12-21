package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinAprActivity extends AppCompatActivity {

    private Button join;
    private Button check;
    private EditText aprCode;
    private DatabaseReference dbRef;
    private DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_to_exist_apartment);
        check = findViewById(R.id.checkCode);
        join = findViewById(R.id.joinButton);
        aprCode = findViewById(R.id.CODE);


        Intent intentOld = getIntent();
        String UserId = intentOld.getExtras().getString("com.example.roomies.Uid");
        String Name = intentOld.getExtras().getString("com.example.roomies.Name");
        if(getIntent().hasExtra("com.example.rommies.aprKey"))
        {
            aprCode.setText(getIntent().getStringExtra("com.example.rommies.aprKey"));
        }
        join.setOnClickListener((v) -> Toast.makeText(JoinAprActivity.this, "Please check your code first", Toast.LENGTH_SHORT).show());
        check.setOnClickListener(v->
        {
            String TheCode = aprCode.getText().toString();
            if(TheCode.isEmpty())
            {
                aprCode.setError("Please enter apartment code");
                return;
            }
            dbRef = FirebaseDatabase.getInstance().getReference().child("Apartments");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChild(aprCode.getText().toString())) {

                        aprCode.setError("No such apartment");
                    }
                    else{
                        Toast.makeText(JoinAprActivity.this, "Proper code\n" + "Press Join", Toast.LENGTH_SHORT).show();
                        join.setOnClickListener(v ->
                        {
                            DatabaseReference aprRef = snapshot.child(TheCode).getRef();
                            aprRef.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    for(DataSnapshot ds : snapshot.child("roommates").getChildren())
                                    {
                                        if(ds.getKey()!=null) {
                                            if (!ds.getKey().equals(UserId)) {
                                                aprRef.child("Balance").child(UserId).child(ds.getKey()).setValue(0);
                                                aprRef.child("Balance").child(ds.getKey()).child(UserId).setValue(0);
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                            dbRef.child(TheCode).child("roommates").child(UserId).setValue(Name);
                            reference.child(UserId).child("Apartment_key").setValue(TheCode);
                            Toast.makeText(JoinAprActivity.this, "successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ApartmentActivity.class);
                            intent.putExtra("com.example.rommies.aprKey", TheCode);
                            startActivity(intent);
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });
    }
}