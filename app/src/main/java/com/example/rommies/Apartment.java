package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Apartment extends AppCompatActivity {

    private ListView listViewRoomate;
    private ArrayAdapter<String> adapter;
    private TextView aprName;
    private static ArrayList<String> array_roomate;

    private FirebaseAuth mAuth;

    private FloatingActionButton plusbtn;
    private DatabaseReference get_key_aprt;
    private DatabaseReference get_roomies;
    private DatabaseReference get_room_name;
    private String idAprt="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment);

        aprName=(TextView)findViewById(R.id.ApartamenTextView);
        listViewRoomate=(ListView)findViewById(R.id.listApart);
        plusbtn=(FloatingActionButton)findViewById(R.id.plusBtn);
        array_roomate=new ArrayList<>();

        mAuth=FirebaseAuth.getInstance();

        adapter=new ArrayAdapter<String>(Apartment.this,android.R.layout.simple_list_item_1,array_roomate);
        listViewRoomate.setAdapter(adapter);

        if(getIntent().hasExtra("com.example.rommies.aprKey"))
        {
            String key=getIntent().getStringExtra("com.example.rommies.aprKey");

            get_room_name=FirebaseDatabase.getInstance().getReference().child("Apartments").child(key).child("Name");
            get_room_name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nameAprt=snapshot.getValue().toString();

                    aprName.setText(nameAprt);
                    updateRoommates(key);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }

//        get_key_aprt = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
//
//
//
//        get_key_aprt.addValueEventListener(new ValueEventListener() {
//
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                for (DataSnapshot snap : snapshot.getChildren()) {
//                    list_roomate.add(snap.getValue().toString());
//
//                }
//                idAprt=list_roomate.get(0);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//        });

        listViewRoomate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent in=new Intent(getApplicationContext(),MyAccount.class);
                in.putExtra("position_rommie",position);
                in.putStringArrayListExtra("name_roomie",array_roomate);
                startActivity(in);
            }
        });

    }


    public void updateRoommates(String key)
    {
        get_roomies= FirebaseDatabase.getInstance().getReference().child("Apartments").child(key).child("roommates");

        get_roomies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                array_roomate.clear();
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    array_roomate.add(snap.getValue().toString());
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}