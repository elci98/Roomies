package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    private static final String[] paths = {"other","food"};
    private Spinner sp;
    private Button apply;
   // private TableLayout tlayout;
    //private PaymentAdapter adapter;
    //private RecyclerView recycler_view;
    private ListView listv;
    private Map<String, String> users = new HashMap<>();
    private ArrayList<lastinfo> linfo =new ArrayList<lastinfo>();
    private Button btdate,btpayer,btcategory,btprice;
    private boolean clicker=true;



    private String key_ap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        key_ap = getIntent().getExtras().getString("com.app.java.Info.key");
        btdate=findViewById(R.id.btndate);
        btcategory=findViewById(R.id.btncatergory);
        btpayer=findViewById(R.id.btnbuyer);
        btprice=findViewById(R.id.btnprice);


       // tlayout=findViewById(R.id.tablelay);
        apply=findViewById(R.id.btnapply);
        listv=findViewById(R.id.listes);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Apartments").child(key_ap).child("Payment");
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Apartments").child(key_ap).child("roommates");
        //List<Payment> pay_list=new ArrayList<>();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    users.put( ds.getKey(),ds.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //setRecyclerView();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    Payment pay = ds.getValue(Payment.class);
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
                    linfo.add(lf);

                }
                InfoAdapter adapt=new InfoAdapter(InfoActivity.this,R.layout.item_layout,linfo);
                listv.setAdapter(adapt);
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
                String categore=sp.getSelectedItem().toString();
                linfo.clear();
                Payment pay=new Payment();


                    rootRef.orderByChild("reason").equalTo(categore).addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                               Payment pay = ds.getValue(Payment.class);
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
                                linfo.add(lf);

                            }
                            InfoAdapter adapt=new InfoAdapter(InfoActivity.this,R.layout.item_layout,linfo);
                            listv.setAdapter(adapt);
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
                InfoAdapter adapt=new InfoAdapter(InfoActivity.this,R.layout.item_layout,linfo);
                listv.setAdapter(adapt);
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
                InfoAdapter adapt = new InfoAdapter(InfoActivity.this, R.layout.item_layout, linfo);
                listv.setAdapter(adapt);
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
                InfoAdapter adapt = new InfoAdapter(InfoActivity.this, R.layout.item_layout, linfo);
                listv.setAdapter(adapt);

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
                InfoAdapter adapt = new InfoAdapter(InfoActivity.this, R.layout.item_layout, linfo);
                listv.setAdapter(adapt);

            }
        });
    }


}