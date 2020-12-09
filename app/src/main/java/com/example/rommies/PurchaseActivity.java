 package com.example.rommies;

 import android.content.Intent;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

 public class PurchaseActivity extends AppCompatActivity {
    private static  final String TAG ="PurActivity";



    private FirebaseDatabase mFD;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef,aprRef;

    private Spinner spinner;
    private LinearLayout linear;
    private static final String[] paths = {"other","food"};


    private String userID;
    private EditText etAmount;
    private String key_ap;
    private Map<String, String> users = new HashMap<>();
    private Button btfinish;
    private int Amount;
    private EditText Money;
    private String spin;
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrname;
    private ArrayList<String> arruser;
    private ListView listname;
    private HashMap<String, String> checkedNames = new HashMap<>();
    private HashMap<String,Double> status= new HashMap<>();
    private Payment pay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pur);
        key_ap = getIntent().getExtras().getString("com.app.java.Purchase.key");
        users = (Map<String, String>)getIntent().getExtras().get("com.app.java.Purchase.users");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        Money = findViewById(R.id.amount);
        btfinish = findViewById(R.id.finish1);
        mRef = FirebaseDatabase.getInstance().getReference("/Apartments/"+key_ap).child("Balance").child(userID);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String t= ""+ ds.getValue();
                    status.put(ds.getKey(),Double.parseDouble(t));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        linear=findViewById(R.id.linearl);
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PurchaseActivity.this, android.R.layout.simple_spinner_item,paths);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        arruser=new ArrayList<>();
        arrname=new ArrayList<>();
        listname=(ListView)findViewById(R.id.listname);
        adapter=new ArrayAdapter<>(this, android.R.layout.select_dialog_multichoice,arrname);

        listname.setAdapter(adapter);

            for(Map.Entry<String,String> user1: users.entrySet() )
            {
                arrname.add(user1.getValue());
                arruser.add(user1.getKey());
            }
             adapter.notifyDataSetChanged();
        listname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id)
          {
            SparseBooleanArray spb = listname.getCheckedItemPositions();
            String name = arrname.get(position);
            String uid=arruser.get(position);
            if(spb.get(position) && !checkedNames.containsKey(uid))
            {
                checkedNames.put(uid, name);
            }

            else if(checkedNames.containsKey(uid))
            {
                checkedNames.remove(uid);
            }


          }
      });
        btfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(Money.getText().toString()))
                {
                    Money.setError("Why like this");
                    return;
                }
                if(checkedNames.isEmpty())
                {
                    btfinish.setError("please choose someone to charge");
                    System.out.println("checkedNames is empty");
                    return;
                }
                Amount=Integer.parseInt(Money.getText().toString());
                spin=spinner.getSelectedItem().toString();
                double finalAmount=Amount/(checkedNames.size()+1);
                ArrayList<String> uid=new ArrayList<>();
                aprRef = FirebaseDatabase.getInstance().getReference("Apartments");
                pay = new Payment(userID,finalAmount, spin);
                aprRef.child(key_ap).child("Payment").child(mRef.push().getKey()).setValue(pay);
                for(Map.Entry<String, String> entry:checkedNames.entrySet())
                {
                    uid.add(entry.getKey());
                    double money=(Double)status.get(entry.getKey());
                    mRef.child(entry.getKey()).setValue(money + finalAmount);
                    if(money>0){
                        aprRef.child(key_ap).child("Balance").child(entry.getKey()).child(userID).setValue( money - finalAmount);
                    }else{
                        aprRef.child(key_ap).child("Balance").child(entry.getKey()).child(userID).setValue( (-money) - finalAmount);
                    }
                }

                finish();
            }
        });
    }
}