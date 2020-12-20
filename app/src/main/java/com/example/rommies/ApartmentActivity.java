package com.example.rommies;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class ApartmentActivity extends AppCompatActivity {

    private ListView listViewRoomate;
    private ArrayAdapter<String> adapter;
    private TextView aprName;
    private String aprKey;
    private static ArrayList<String> roommates;
    ArrayList<String> withoutmanager;
    private FirebaseAuth mAuth;
    String manager="";
    String uidManager="";
    private DatabaseReference get_roomies;
    private DatabaseReference get_room_name;
    private boolean isManager = false;
    private MenuItem adminBtn;
    private HashMap<String, String> usersMap = new HashMap<>();
    private Button btinf;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment);

        /*handle back button press */

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true )
        {
            @Override
            public void handleOnBackPressed()
            {
                new AlertDialog.Builder(ApartmentActivity.this).setTitle("Exit").
                        setMessage("Do you want to exit from Rommies?").
                        setCancelable(false).
                        setPositiveButton("Yes", (dialog, id)-> finish()).
                        setNegativeButton("No", (dialog, id)-> {dialog.cancel();return;}).
                        create().
                        show();
            }
        });

        if(getIntent().hasExtra("com.example.rommies.aprKey"))
        {
            aprKey = getIntent().getStringExtra("com.example.rommies.aprKey");
            get_room_name=FirebaseDatabase.getInstance().getReference().child("Apartments").child(aprKey);
            get_room_name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("******+++***** "+snapshot.child("Name").getValue().toString());
                    aprName.setText(snapshot.child("Name").getValue().toString());
                    if(mAuth.getUid().equals(snapshot.child("Manager").getValue())){
                        uidManager=snapshot.child("Manager").getValue().toString();
                        adminBtn.setVisible(true);

                    }
                    updateRoommates(aprKey);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        aprName = findViewById(R.id.ApartamenTextView);
        listViewRoomate = findViewById(R.id.listApart);
        roommates=new ArrayList<>();
        adapter= new ArrayAdapter<>(ApartmentActivity.this, android.R.layout.simple_list_item_1, roommates);
        listViewRoomate.setAdapter(adapter);
        mAuth=FirebaseAuth.getInstance();
        btinf=(Button)findViewById(R.id.btninfo);
        btinf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(getApplicationContext(),InfoActivity.class);
                in.putExtra("com.app.java.Info.key",aprKey);

                startActivity(in);

            }
        });

        listViewRoomate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent in=new Intent(getApplicationContext(),MyAccount.class);
                in.putExtra("position_rommie",position);
                in.putExtra("com.app.java.acc.key",aprKey);
                in.putStringArrayListExtra("name_roomie",roommates);
                in.putExtra("com.app.java.acc.users",usersMap);
                startActivity(in);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        adminBtn = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.newPurchase:
            {
                Intent intent = new Intent(this, PurchaseActivity.class);
                intent.putExtra("com.app.java.Purchase.key",aprKey);
                intent.putExtra("com.app.java.Purchase.users",usersMap);
                startActivity(intent);
                break;
            }
            case R.id.adminBtn:
            {
                withoutmanager=new ArrayList<>();
                Intent in=new Intent(ApartmentActivity.this, ManagerActivity.class);
                in.putExtra("keyaprt",aprKey);
                System.out.println(manager+ "--------->>>>>>");
                for(int i=0; i<roommates.size(); i++)
                {

                    if(!manager.equals(roommates.get(i)))
                        withoutmanager.add(roommates.get(i));
                }
                in.putStringArrayListExtra("list",withoutmanager);

                in.putExtra("hash",usersMap);
                startActivityForResult(in,1001);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1001)
        {
            if(resultCode==RESULT_OK)
            {
                String n=data.getStringExtra("change_name_aprt");
                aprName.setText(n);

            }

            if(resultCode==RESULT_FIRST_USER)
            {
                String u=data.getStringExtra("change_manager_aprt");
                if(!u.equals(uidManager))
                {
                    adminBtn.setVisible(false);
                }

            }
        }
    }

    private void updateRoommates(String key)
    {
        get_roomies= FirebaseDatabase.getInstance().getReference().child("Apartments").child(key).child("roommates");
        get_roomies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roommates.clear();
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    roommates.add(snap.getValue().toString());
                    if(!snap.getKey().equals(mAuth.getUid()))
                        usersMap.put(snap.getKey(), (String)snap.getValue());
                    if(snap.getKey().equals(uidManager))
                        manager=snap.getValue().toString();

                }

                System.out.println("******+++***** ");
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


}