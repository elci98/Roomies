package com.example.rommies;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.PasswordAuthentication;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ApartmentActivity extends AppCompatActivity {

    //private ArrayAdapter<String> adapter;
    private TextView aprName;
    private String aprKey;
    private static ArrayList<String> roommates;
    private FirebaseAuth mAuth;
    private String manager="";
    private String uidManager="";
    private String CurrentName="";
    private String CurrentEmail="";
    private  String keyforchangename="";
    private MenuItem adminBtn;
    private final HashMap<String, String> usersMap = new HashMap<>();
    private final int MANAGER_ACTIVITY_CODE = 1001;
    private boolean paymentsExists = true;
    private TextView total_balance;
    private TextView total_price;
    private BottomNavigationView btn_nagativ;
    private ScrollView scrollbalance;
    private ScrollView scrollaccount;
    private MyListAdapter listAdapter;
    private ListView listViewRoomate;
    private DatabaseReference balance;
    private double number=0;
    private double totalBalance=0;
    private ImageButton namebtn;
    private ImageButton passbtn;
    private ImageButton emailbtn;
    private TextView name;
    private TextView email;
    private TextView password;
    private EditText namechange;
    private EditText emailchange;
    private EditText oldpassword;
    private EditText newpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment);
        scrollbalance=(ScrollView)findViewById(R.id.linearLayoutScroll);
        scrollaccount=(ScrollView)findViewById(R.id.linearLayoutScroll2);

        btn_nagativ=(BottomNavigationView)findViewById(R.id.bottomNavigationView);
        btn_nagativ.setOnNavigationItemSelectedListener(listener);
        /*handle back button press */
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true )
        {
            @Override
            public void handleOnBackPressed()
            {
                new AlertDialog.Builder(ApartmentActivity.this).setTitle("Exit")
                        .setMessage("Do you want to exit from Rommies?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id)-> finish())
                        .setNegativeButton("No", (dialog, id)-> dialog.cancel())
                        .create()
                        .show();
            }
        });

        if(getIntent().hasExtra("com.example.rommies.aprKey"))
        {
            aprKey = getIntent().getStringExtra("com.example.rommies.aprKey");
            DatabaseReference get_room_name = FirebaseDatabase.getInstance().getReference().child("Apartments").child(aprKey);
            get_room_name.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    aprName.setText(snapshot.child("Name").getValue().toString());
                    if(mAuth.getUid().equals(snapshot.child("Manager").getValue())){
                        uidManager=snapshot.child("Manager").getValue().toString();
                        adminBtn.setVisible(true);

                    }
                    updateRoommates(aprKey);
                    paymentsExists = snapshot.hasChild("Payment");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        aprName = findViewById(R.id.ApartamenTextView);
//        ListView listViewRoomate = findViewById(R.id.listApart);
        listViewRoomate = findViewById(R.id.listApart);
        roommates=new ArrayList<>();
 //       adapter= new ArrayAdapter<>(ApartmentActivity.this, android.R.layout.simple_list_item_1, roommates);
//      listViewRoomate.setAdapter(adapter);
        mAuth=FirebaseAuth.getInstance();

        listViewRoomate.setOnItemClickListener((parent, view, position, id) -> {
            Intent in=new Intent(getApplicationContext(),MyAccount.class);
            in.putExtra("position_rommie",position);
            in.putExtra("com.app.java.acc.key",aprKey);
            in.putStringArrayListExtra("name_roomie",roommates);
            in.putExtra("com.app.java.acc.users",usersMap);
            startActivity(in);
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        adminBtn = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener listener=new BottomNavigationView.OnNavigationItemSelectedListener(){

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.friends_btn:
                {
                    scrollbalance.setVisibility(View.GONE);
                    scrollaccount.setVisibility(View.GONE);
                    listViewRoomate.setVisibility(View.VISIBLE);
                    break;
                }

                case R.id.balance_btn:
                {
                    listViewRoomate.setVisibility(View.GONE);
                    scrollaccount.setVisibility(View.GONE);
                    scrollbalance.setVisibility(View.VISIBLE);
                    break;
                }

                case R.id.account_btn:
                {
                    Button save=(Button)findViewById(R.id.savechange);
                    listViewRoomate.setVisibility(View.GONE);
                    scrollbalance.setVisibility(View.GONE);
                    scrollaccount.setVisibility(View.VISIBLE);
                    namebtn=(ImageButton)findViewById(R.id.imageEdit1);
                    emailbtn=(ImageButton)findViewById(R.id.imageEdit2);
                    passbtn=(ImageButton)findViewById(R.id.imageEdit3);
                    name=(TextView)findViewById(R.id.TextMyCurrentName);
                    email=(TextView)findViewById(R.id.TextMyCurrentEmail);
                    password=(TextView)findViewById(R.id.TextMyCurrentPass);
                    namechange=(EditText)findViewById(R.id.Editfullname);
                    emailchange=(EditText)findViewById(R.id.EditEmailadress);
                    oldpassword=(EditText)findViewById(R.id.editPasswordold);
                    newpassword=(EditText)findViewById(R.id.editPasswordnew);
                    name.setText(CurrentName);
                    email.setText(CurrentEmail);
                    password.setText("change password");

                    namebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            name.setVisibility(View.GONE);
                            namechange.setVisibility(View.VISIBLE);
                            namechange.setText(CurrentName);
                        }
                    });

                    emailbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            email.setVisibility(View.GONE);
                            emailchange.setVisibility(View.VISIBLE);
                            emailchange.setText(CurrentEmail);
                        }
                    });

                    passbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            password.setVisibility(View.GONE);
                            oldpassword.setVisibility(View.VISIBLE);
                            newpassword.setVisibility(View.VISIBLE);
                            oldpassword.setHint("enter your old password");
                            newpassword.setHint("enter your new password");

                        }
                    });



                    save.setOnClickListener((v)->{
                        String newname=namechange.getText().toString().trim();
                        String newemail=emailchange.getText().toString().trim();
                        String oldpass=oldpassword.getText().toString().trim();
                        String newpass=newpassword.getText().toString().trim();

                        if(!oldpass.isEmpty() && !newpass.isEmpty())
                        {
                            FirebaseUser cUser=mAuth.getCurrentUser();
                            oldpassword.setVisibility(View.GONE);
                            newpassword.setVisibility(View.GONE);
                            password.setVisibility(View.VISIBLE);
                            AuthCredential authCredentialpass= EmailAuthProvider.getCredential(cUser.getEmail(),oldpass);
                            cUser.reauthenticate(authCredentialpass)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            cUser.updatePassword(newpass)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Toast.makeText(getApplicationContext(),"Password Update...",Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });

                        }

                        if(!newemail.isEmpty() && !newemail.equals(CurrentEmail))
                        {
                            System.out.println("11111222244444 "+newemail);
                            FirebaseUser cUser=mAuth.getCurrentUser();

                            if(cUser.getEmail().equals(CurrentEmail))
                            {
                                emailchange.setVisibility(View.GONE);
                                email.setText(newemail);
                                System.out.println("111112222333 "+CurrentEmail);
                                cUser.updateEmail(newemail);
                                setEmailAdress(newemail);
                            }
                        }

                        if(!newname.isEmpty() && !newname.equals(CurrentName))
                        {
                            System.out.println("999999 "+newname);
                            System.out.println("55555555 "+CurrentName);
                            namechange.setVisibility(View.GONE);
                            name.setText(newname);
                            setNewName(newname);
                        }

                        if(newname.isEmpty())
                            namechange.setVisibility(View.GONE);

                        if(newemail.isEmpty())
                            emailchange.setVisibility(View.GONE);

                        if(oldpass.isEmpty() || newpass.isEmpty())
                        {
                            oldpassword.setVisibility(View.GONE);
                            newpassword.setVisibility(View.GONE);
                        }

                        if(newemail.equals(CurrentEmail))
                            emailchange.setVisibility(View.GONE);

                        if(newname.equals(CurrentName))
                            namechange.setVisibility(View.GONE);


                        password.setVisibility(View.VISIBLE);
                        name.setVisibility(View.VISIBLE);
                        email.setVisibility(View.VISIBLE);

                    });


                    oldpassword.setVisibility(View.GONE);
                    newpassword.setVisibility(View.GONE);
                    emailchange.setVisibility(View.GONE);
                    namechange.setVisibility(View.GONE);
                    password.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    break;
                }


            }

            return true;
        }
    };
    @SuppressLint("NonConstantResourceId")
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
                ArrayList<String> withoutManager = new ArrayList<>();
                Intent in=new Intent(ApartmentActivity.this, managerPage.class);
                in.putExtra("keyaprt",aprKey);
                for(int i=0; i<roommates.size(); i++)
                {

                    if(!manager.equals(roommates.get(i)))
                        withoutManager.add(roommates.get(i));
                }
                in.putStringArrayListExtra("list", withoutManager);

                in.putExtra("hash",usersMap);
                startActivityForResult(in,MANAGER_ACTIVITY_CODE);
                break;
            }
            case R.id.info:
            {
                Intent in=new Intent(getApplicationContext(),InfoActivity.class);
                in.putExtra("com.app.java.Info.key",aprKey);
                if(!paymentsExists)
                {
                    Toast.makeText(ApartmentActivity.this,"There are no payments",Toast.LENGTH_SHORT).show();
                    break;
                }
                startActivity(in);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==MANAGER_ACTIVITY_CODE)
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
        DatabaseReference get_roomies = FirebaseDatabase.getInstance().getReference().child("Apartments").child(key).child("roommates");
        get_roomies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roommates.clear();
                for(DataSnapshot snap : snapshot.getChildren())
                {

                    if(snap.getKey().equals(mAuth.getUid()))
                    {
                        CurrentName=snap.getValue().toString();
                    }

                    if(!snap.getKey().equals(mAuth.getUid()))
                    {
                        roommates.add(snap.getValue().toString());
                        usersMap.put(snap.getKey(), (String)snap.getValue());
                    }

                    if(snap.getKey().equals(uidManager))
                        manager=snap.getValue().toString();

                }

                if(roommates.isEmpty() && usersMap.isEmpty())
                {
                    listViewRoomate.setBackground(null);
                    total_balance=(TextView) findViewById(R.id.totalbalanceprice);
                    total_price=(TextView)findViewById(R.id.shekelprice);
                    total_balance.setText(null);
                    total_price.setText(null);
                }
                else
                {
                    listAdapter=new MyListAdapter(ApartmentActivity.this,roommates,usersMap,key);
                    listViewRoomate.setAdapter(listAdapter);


                    Total_Balance(key);
                }
                getEmailAdress();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getEmailAdress()
    {

        DatabaseReference getEmail;
        getEmail=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("email");
        getEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                CurrentEmail=snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setNewName(String newName)
    {
        DatabaseReference setnewname;
        setnewname=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("name");
        setnewname.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                snapshot.getRef().setValue(newName);

                getkeyaprt(newName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getkeyaprt(String newName)
    {
        DatabaseReference getkey;
        getkey=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("Apartment_key");
        getkey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                keyforchangename=snapshot.getValue().toString();
                setNameFromApartament(newName,keyforchangename);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setNameFromApartament(String nameApartament,String key)
    {
        DatabaseReference setnamefromapart;
        setnamefromapart=FirebaseDatabase.getInstance().getReference().child("Apartments").child(key).child("roommates").child(mAuth.getUid());
        setnamefromapart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                snapshot.getRef().setValue(nameApartament);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setEmailAdress(String email)
    {
        DatabaseReference setEmail;
        setEmail=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("email");
        setEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                snapshot.getRef().setValue(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void Total_Balance(String key)
    {

        total_balance=(TextView) findViewById(R.id.totalbalanceprice);
        total_price=(TextView)findViewById(R.id.shekelprice);
        balance=FirebaseDatabase.getInstance().getReference().child("Apartments").child(key).child("Balance").child(mAuth.getUid());
        balance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalBalance=0;
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    for(Map.Entry<String,String> entry : usersMap.entrySet())
                    {
                        if(snap.getKey().equals(entry.getKey()))
                        {

                            number=snap.getValue(Double.class);

                            System.out.println("++!!++!! "+ number);
                            totalBalance+=number;


                            break;
                        }
                    }
                }

                if(totalBalance>0)
                {
                    DecimalFormat df = new DecimalFormat("#.##");

                    total_balance.setText("You owed");
                    total_price.setText(df.format(totalBalance)+" \u20AA");
                    total_balance.setTextColor(Color.parseColor("#49911D"));
                    total_price.setTextColor(Color.parseColor("#49911D"));
                }
                if(totalBalance<0)
                {
                    DecimalFormat df = new DecimalFormat("#.##");
                    total_balance.setText("You owe");
                    total_price.setText(df.format(-totalBalance)+" \u20AA");
                    total_balance.setTextColor(Color.parseColor("#E14C1D"));
                    total_price.setTextColor(Color.parseColor("#E14C1D"));
                }
                if(totalBalance==0)
                {
                    DecimalFormat df = new DecimalFormat("#.##");
                    total_balance.setText("You evens");
                    total_price.setText(df.format(totalBalance)+" \u20AA");
                    total_balance.setTextColor(Color.parseColor("#1b66b1"));
                    total_price.setTextColor(Color.parseColor("#1b66b1"));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}