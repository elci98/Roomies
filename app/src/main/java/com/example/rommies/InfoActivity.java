package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    private static final String[] paths = {"other","food"};
    private Spinner sp;
    private Button apply;
    private TableLayout tlayout;
    private PaymentAdapter adapter;
    private RecyclerView recycler_view;
    private Map<String, String> users = new HashMap<>();



    private String key_ap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        key_ap = getIntent().getExtras().getString("com.app.java.Info.key");
        //users = (Map<String, String>)getIntent().getExtras().get("maxim");
       // System.out.println("maximmaxim   : "+users.get());


        tlayout=findViewById(R.id.tablelay);
        apply=findViewById(R.id.btnapply);
        recycler_view=findViewById(R.id.recycler_view);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Apartments").child(key_ap).child("Payment");
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Apartments").child(key_ap).child("roommates");
        List<Payment> pay_list=new ArrayList<>();
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter=new PaymentAdapter(this,pay_list);
        recycler_view.setAdapter(adapter);
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


        sp = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(InfoActivity.this, android.R.layout.simple_spinner_item,paths);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter1);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categore=sp.getSelectedItem().toString();
                pay_list.clear();
                Payment pay=new Payment();
                //Query queryother = rootRef.child("Payment").orderByChild("reason").equalTo(categore);
               // Query queryfood = rootRef.child("Payment").orderByChild("reason").equalTo("food");

                    rootRef.orderByChild("reason").equalTo(categore).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Payment pay = ds.getValue(Payment.class);
                                pay.setPayer(users.get(pay.getPayer()));
                                pay_list.add(pay);

                            }
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        });
    }

    private void setRecyclerView(List<Payment> list) {
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter=new PaymentAdapter(this,list);
        recycler_view.setAdapter(adapter);
    }

    private List<Payment> getList() {
        List<Payment> pay_list=new ArrayList<>();
        pay_list.add(new Payment("123",50,"food",null));


        return pay_list;
    }
}