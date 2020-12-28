package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.SyncTree;
import com.google.firebase.database.core.view.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    private static final String[] paths = {"other","food","Bills"};
    private Spinner sp;
    private Button apply;
    private ListView listv;
    private Map<String, String> users = new HashMap<>();
    private ArrayList<lastinfo> linfo =new ArrayList<lastinfo>();
    private Button btdate,btpayer,btcategory,btprice;
    private boolean clicker=true;
    private  boolean isManager = false;
    private Map<String, Payment> payments = new HashMap<>();
    private String key_ap;
    private TextView tvManager;
    private InfoAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        adapter = new InfoAdapter(InfoActivity.this,R.layout.item_layout,linfo);
        key_ap = getIntent().getExtras().getString("com.app.java.Info.key");
        btdate=findViewById(R.id.btndate);
        btcategory=findViewById(R.id.btncatergory);
        btpayer=findViewById(R.id.btnbuyer);
        btprice=findViewById(R.id.btnprice);
        apply=findViewById(R.id.btnapply);
        listv=findViewById(R.id.listes);
        listv.setAdapter(adapter);
        tvManager = findViewById(R.id.textViewManager);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Apartments").child(key_ap).child("Payment");
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Apartments").child(key_ap);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Manager").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    isManager = true;
                    tvManager.setVisibility(View.VISIBLE);
                }
                for(DataSnapshot ds : snapshot.child("roommates").getChildren()){
                    users.put(ds.getKey(),ds.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    Payment pay = ds.getValue(Payment.class);
                    payments.put(pay.getKey(),pay);
                    lastinfo lf=new lastinfo();
                    StringBuilder sb = new StringBuilder();
                    for(int i= 0 ; i<pay.getParticipant().size() ;i++)
                    {
                        if(!(i+1==pay.getParticipant().size()))
                        {
                            sb.append(users.get(pay.getParticipant().get(i))+" , ");
                        }
                        else
                        {
                            sb.append(users.get(pay.getParticipant().get(i)));
                        }
                    }

                    lf.setAmount(pay.getAmount());
                    lf.setPayer(users.get(pay.getPayer()));
                    lf.setDate(pay.getDate());
                    lf.setReason(pay.getReason());
                    lf.setPartic(sb.toString());
                    lf.setKey(pay.getKey());
                    linfo.add(lf);

                }
//                InfoAdapter adapt=new InfoAdapter(InfoActivity.this,R.layout.item_layout,linfo);
                adapter.notifyDataSetChanged();
                listv.setOnItemLongClickListener((parent, view1, pos, id) -> {
                    if(!isManager)return false;
                    new AlertDialog.Builder(InfoActivity.this)
                            .setIcon(android.R.drawable.ic_delete)
                            .setTitle("Are you sure you want to delete this payment?")
                            .setMessage("this wil recalculate balances")
                            .setPositiveButton("Yes", (dialog, which) -> deletePayment(pos))
                            .setNegativeButton("No", null)
                            .show();
                       return true;
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sp = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(InfoActivity.this, android.R.layout.simple_spinner_item,paths);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter1);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category=sp.getSelectedItem().toString();
                linfo.clear();
                rootRef.orderByChild("reason").equalTo(category).addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Payment pay = ds.getValue(Payment.class);
                            lastinfo lf=new lastinfo();
                            StringBuilder sb = new StringBuilder();
                            for(int i= 0 ; i<pay.getParticipant().size() ; i++)
                            {
                                if(pay.getParticipant().size() != i+1)
                                {
                                    sb.append(users.get(pay.getParticipant().get(i))+" , ");
                                }
                                else {
                                    sb.append(users.get(pay.getParticipant().get(i)));
                                }
                            }
                            lf.setAmount(pay.getAmount());
                            lf.setPayer(users.get(pay.getPayer()));
                            lf.setDate(pay.getDate());
                            lf.setReason(pay.getReason());
                            lf.setPartic(sb.toString());
                            lf.setKey(pay.getKey());
                            linfo.add(lf);
                        }
//                            InfoAdapter adapt=new InfoAdapter(InfoActivity.this,R.layout.item_layout,linfo);
//                            listv.setAdapter(adapt);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        });
        btprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicker) {
                    clicker=false;

                    Collections.sort(linfo, new Comparator<lastinfo>() {
                        @Override
                        public int compare(lastinfo o1, lastinfo o2) {
                            if (o1.getAmount() - o2.getAmount() > 0) {
                                return 1;
                            }
                            if (o1.getAmount() - o2.getAmount() < 0) {
                                return -1;
                            }
                            return 0;
                        }
                    });
                }else{
                    clicker=true;
                    Collections.sort(linfo, new Comparator<lastinfo>() {
                        @Override
                        public int compare(lastinfo o1, lastinfo o2) {
                            if (o1.getAmount() - o2.getAmount() < 0) {
                                return 1;
                            }
                            if (o1.getAmount() - o2.getAmount() > 0) {
                                return -1;
                            }
                            return 0;
                        }
                    });
                }
//                InfoAdapter adapt=new InfoAdapter(InfoActivity.this,R.layout.item_layout,linfo);
//                listv.setAdapter(adapt);
                adapter.notifyDataSetChanged();
                //Collections.sort(linfo);
            }
        });
        btpayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicker) {
                    clicker=false;

                    Collections.sort(linfo, new Comparator<lastinfo>() {
                        @Override
                        public int compare(lastinfo o1, lastinfo o2) {
                            return o1.getPayer().compareToIgnoreCase(o2.getPayer());
                        }

                    });


                }else{
                    clicker=true;
                    Collections.sort(linfo, new Comparator<lastinfo>(){
                        @Override
                        public int compare(lastinfo o1, lastinfo o2) {
                            if(o2.getPayer().compareToIgnoreCase(o1.getPayer())>0)
                            {
                                return 1;
                            }if(o2.getPayer().compareToIgnoreCase(o1.getPayer())<0){
                                return -1;
                            }
                            return 0;
                        }

                    });
                }
//                InfoAdapter adapt = new InfoAdapter(InfoActivity.this, R.layout.item_layout, linfo);
//                listv.setAdapter(adapt);
                adapter.notifyDataSetChanged();

            }
        });
        btdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicker) {
                    clicker = false;

                    Collections.sort(linfo, new Comparator<lastinfo>() {
                        @Override
                        public int compare(lastinfo o1, lastinfo o2) {
                            if (o1.getDate().getYear() < o2.getDate().getYear()) {
                                return 1;
                            }
                            if (o1.getDate().getYear() > o2.getDate().getYear()) {
                                return -1;
                            }
                            if (o1.getDate().getMonth() < o2.getDate().getMonth()) {
                                return 1;
                            }
                            if (o1.getDate().getMonth() > o2.getDate().getMonth()) {
                                return -1;
                            }
                            if (o1.getDate().getDay() < o2.getDate().getDay()) {
                                return 1;
                            }
                            if (o1.getDate().getDay() > o2.getDate().getDay()) {
                                return -1;
                            }

                            return 0;
                        }

                    });
                }else{
                    clicker=true;
                    Collections.sort(linfo, new Comparator<lastinfo>() {
                        @Override
                        public int compare(lastinfo o1, lastinfo o2) {
                            if (o1.getDate().getYear() > o2.getDate().getYear()) {
                                return 1;
                            }
                            if (o1.getDate().getYear() < o2.getDate().getYear()) {
                                return -1;
                            }
                            if (o1.getDate().getMonth() > o2.getDate().getMonth()) {
                                return 1;
                            }
                            if (o1.getDate().getMonth() < o2.getDate().getMonth()) {
                                return -1;
                            }
                            if (o1.getDate().getDay() > o2.getDate().getDay()) {
                                return 1;
                            }
                            if (o1.getDate().getDay() < o2.getDate().getDay()) {
                                return -1;
                            }

                            return 0;
                        }

                    });

                }
//                InfoAdapter adapt = new InfoAdapter(InfoActivity.this, R.layout.item_layout, linfo);
//                listv.setAdapter(adapt);
                adapter.notifyDataSetChanged();
            }
        });
        btcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicker) {
                    clicker=false;

                    Collections.sort(linfo, new Comparator<lastinfo>() {
                        @Override
                        public int compare(lastinfo o1, lastinfo o2) {
                            return o1.getReason().compareToIgnoreCase(o2.getReason());
                        }

                    });


                }else{
                    clicker=true;
                    Collections.sort(linfo, new Comparator<lastinfo>(){
                        @Override
                        public int compare(lastinfo o1, lastinfo o2) {
                            if(o2.getReason().compareToIgnoreCase(o1.getReason())>0)
                            {
                                return 1;
                            }if(o2.getReason().compareToIgnoreCase(o1.getReason())<0){
                                return -1;
                            }
                            return 0;
                        }

                    });
                }
//                InfoAdapter adapt = new InfoAdapter(InfoActivity.this, R.layout.item_layout, linfo);
//                listv.setAdapter(adapt);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void deletePayment(int position)
    {
        Payment p = payments.get(linfo.get(position).getKey());
        DatabaseReference pRef = FirebaseDatabase.getInstance().getReference("Apartments").child(key_ap).child("Payment");

        pRef.child(p.getKey()).removeValue((error, ref) -> {
            if (error == null) {
                ArrayList<String> participants = p.getParticipant();
                Double payPerPerson = p.getAmount() / (participants.size() + 1);
                String payer = p.getPayer();
                DatabaseReference bRef = FirebaseDatabase.getInstance().getReference("Apartments").child(key_ap).child("Balance");
                int i = 0, pos = 0;
                String other = "";
                while( pos < participants.size())//re adjust payments balance
                {
                    if( i % 2 == 0)
                    {
                        other = participants.get(pos);
                        payer = p.getPayer();
                    }
                    int finalI = i;
                    bRef.child(other).child(payer).runTransaction(new Transaction.Handler()
                    {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData)
                        {
                            double v = 0;
                            if(currentData.getValue() != null) {
                                v = currentData.getValue(Double.class);
                                Log.d("trDebug", v + " before transaction");
                            }
                            v = (finalI % 2==0) ? v + payPerPerson : v - payPerPerson;
                            Log.d("trDebug", v + " after transaction ");
                            currentData.setValue(v);
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            if(!committed)
                                Log.d("trDebug", "runTransaction: " + error);
                            else
                                Log.d("trDebug", "runTransaction completed" );

                        }
                    });

                    String t = other;
                    other = payer;
                    payer = t;
                    if( ++i % 2 == 0)
                        pos++;
                }
                linfo.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(InfoActivity.this, "Payment Deleted Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
