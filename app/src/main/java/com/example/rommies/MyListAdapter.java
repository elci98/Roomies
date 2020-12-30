package com.example.rommies;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyListAdapter extends BaseAdapter {

    private ArrayList<String> roomies;
    private LayoutInflater inflater;
    private HashMap<String, String> usersMap;
    private FirebaseAuth fAuth;
    private DatabaseReference balance;
    private String aprkey;
    double p;
    String n;

    public MyListAdapter(Context c,ArrayList<String> r,HashMap<String, String> u,String key)
    {
        this.inflater=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.roomies=r;
        this.usersMap=u;
        this.aprkey=key;
    }


    @Override
    public int getCount() {
        return roomies.size();
    }

    @Override
    public Object getItem(int position) {
        return roomies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView=inflater.inflate(R.layout.account_activity,null);
        TextView name= (TextView) convertView.findViewById(R.id.Name2);
        TextView price= (TextView) convertView.findViewById(R.id.Price2);
        TextView state= (TextView) convertView.findViewById(R.id.State2);
        ImageView avatar= (ImageView) convertView.findViewById(R.id.imageView4);

        fAuth=FirebaseAuth.getInstance();


            balance=FirebaseDatabase.getInstance().getReference().child("Apartments").child(aprkey).child("Balance").child(fAuth.getUid());
            balance.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot snap : snapshot.getChildren())
                    {

                        for(Map.Entry<String,String> entry : usersMap.entrySet())
                        {

                            if(snap.getKey().equals(entry.getKey()) && roomies.get(position).equals(entry.getValue()))
                            {
                                n=roomies.get(position);
                                p=snap.getValue(Double.class);
                                if(p>0)
                                {
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    name.setText(n);
                                    price.setText(df.format(p)+" \u20AA");
                                    price.setTextColor(Color.parseColor("#49911D"));
                                    state.setText("owes you :");
                                    state.setTextColor(Color.parseColor("#49911D"));

                                }
                                if(p<0)
                                {
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    name.setText(n);
                                    price.setText(df.format(-p)+" \u20AA");
                                    price.setTextColor(Color.parseColor("#E14C1D"));
                                    state.setText("you owe :");
                                    state.setTextColor(Color.parseColor("#E14C1D"));
                                }

                                if(p==0)
                                {
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    name.setText(n);
                                    price.setText(df.format(p)+" \u20AA");
                                    price.setTextColor(Color.parseColor("#1b66b1"));
                                    state.setText("you evens!");
                                    state.setTextColor(Color.parseColor("#1b66b1"));
                                }
                                break;
                            }
                        }
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        return convertView;
    }
}
