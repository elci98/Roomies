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
    private EditText aprtCode;
    private DatabaseReference dbRef;
    private DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_to_exist_apartment);
        check = (Button) findViewById(R.id.checkCode);
        join = (Button) findViewById(R.id.joinButton);

        aprtCode = (EditText) findViewById(R.id.CODE);


        Intent intentOld = getIntent();
        String UserId = intentOld.getExtras().getString("com.example.roomies.Uid");
        String Name = intentOld.getExtras().getString("com.example.roomies.Name");

        check.setOnClickListener(v->
        {
            String TheCode = aprtCode.getText().toString();

            dbRef = FirebaseDatabase.getInstance().getReference().child("Apartments");

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChild(aprtCode.getText().toString())) {

                        aprtCode.setError("No such apartment");
                    }
                    else{
                        Toast.makeText(JoinAprActivity.this, "Proper code\n" +
                                "Press Join", Toast.LENGTH_SHORT).show();
                        join.setOnClickListener(v ->
                        {

                            play(TheCode,UserId,Name);

                            
                        });

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });


    }

    public void play(String code, String UserId,String Name) {

            dbRef.child(code).child("roommates").child(UserId).setValue(Name);
            reference.child(UserId).child("IDAprt").setValue(code);

            Toast.makeText(JoinAprActivity.this, "successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Apartment.class);
            intent.putExtra("com.example.rommies.aprKey", code);
            startActivity(intent);


    }


}
