package com.example.rommies;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PurchaseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static  final String TAG ="PurActivity";



    private FirebaseAuth mAuth;
    private DatabaseReference userRef,aprRef;

    private Spinner spinner;
    private LinearLayout linear;
    private static final String[] paths = {"other","food"};


    private String userID;
    private String key_ap;
    private Map<String, String> users = new HashMap<>();
    private Button btfinish;
    private int Amount;
    private EditText Money;
    private String spin;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrname;
    private ArrayList<String> arruser;
    private ListView listname;
    private HashMap<String, String> checkedNames = new HashMap<>();
    private HashMap<String,Double> NeedToPayMe= new HashMap<>();
    private Payment pay;
    private TextView dateText;
    private Date date = new Date();


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
        dateText=findViewById(R.id.textdate);
        findViewById(R.id.btndate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDailog();
            }
        });
        btfinish = findViewById(R.id.finish1);
        userRef = FirebaseDatabase.getInstance().getReference("/Apartments/"+key_ap).child("Balance").child(userID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String t= ""+ ds.getValue();
                    NeedToPayMe.put(ds.getKey(),Double.parseDouble(t));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        linear=findViewById(R.id.linearl);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setPrompt("Select categore");
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
                if(TextUtils.isEmpty(date.toString())){
                    dateText.setError("enter date");
                }
                Amount=Integer.parseInt(Money.getText().toString());
                spin=spinner.getSelectedItem().toString();
                double payPerPerson = (double)Amount/(checkedNames.size()+1);
                System.out.println("payPerPerson: "+payPerPerson);
                ArrayList<String> uid=new ArrayList<>();
                aprRef = FirebaseDatabase.getInstance().getReference("Apartments");

                int i=1;
                for(Map.Entry<String, String> entry : checkedNames.entrySet())//add to specific user the amount to pay me
                {

                    String owe = entry.getKey();
                    System.out.println("owe: "+owe+", user: "+userID);
                    System.out.println("11111111"+aprRef.child(key_ap).child("Balance").child(owe).child(userID).getKey());
                    aprRef.child(key_ap).child("Balance").child(owe).child(userID).runTransaction(new Transaction.Handler()
                    {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData)
                        {
                            System.out.println("2222"+currentData.getKey());
                            double v = 0;
                            if(currentData.getValue() != null) {
                                v =currentData.getValue(Double.class);
                            }
                            v-= payPerPerson;
                            currentData.setValue(v);
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            if(!committed)
                                Log.d(TAG, "runTransaction: " + error);
                            else
                                Log.d(TAG, "runTransaction completed" );
                        }
                    });
                    uid.add(owe);
                    double currentBalance = NeedToPayMe.get(owe);
                    System.out.println("currentBalance: "+currentBalance+", payPerPerson: "+ payPerPerson);
                    userRef.child(owe).setValue(currentBalance + payPerPerson);
                }
                pay = new Payment(userID, Amount, spin,uid,date);
                aprRef.child(key_ap).child("Payment").child(userRef.push().getKey()).setValue(pay);

                finish();
            }
        });
    }
    private void showDatePickerDailog(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateshow=dayOfMonth+" / "+month+" / "+year;
        date.setDay(dayOfMonth);
        date.setMonth(month);
        date.setYear(year);
        dateText.setText(dateshow);

    }
}