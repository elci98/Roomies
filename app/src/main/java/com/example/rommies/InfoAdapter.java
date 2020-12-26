package com.example.rommies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InfoAdapter extends ArrayAdapter<lastinfo> {
        private static  final String TAG="InfoAdapter";
    private Context mContext;
    private int mResource;
    //private Object LayoutInflater;

    public InfoAdapter(@NonNull Context context, int resource, @NonNull List<lastinfo> objects) {

        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       String payer=getItem(position).getPayer();
        double money=getItem(position).getAmount();
        String Date=getItem(position).getDate().toString();
        String res=getItem(position).getReason();
        String Part=getItem(position).getPartic();

        LayoutInflater inflater=LayoutInflater.from(mContext);

         convertView=inflater.inflate(mResource,parent,false);
        TextView tvpayer=(TextView) convertView.findViewById(R.id.buy);
        TextView tvmoney=(TextView) convertView.findViewById(R.id.pri);
        TextView tvdate=(TextView) convertView.findViewById(R.id.date);
        TextView tvres=(TextView) convertView.findViewById(R.id.categ);
        TextView tvpart=(TextView) convertView.findViewById(R.id.partic);

        tvpayer.setText(payer);
        tvdate.setText(Date);
        String mon=" "+money;
        System.out.println("maxim123 " + money);
        tvmoney.setText(mon );
        tvres.setText(res);
        tvpart.setText(Part);
            return convertView;
    }

 
//
//    public InfoAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Payment> objects) {
//        super(context, resource, objects);
//        mcon =context;
//    }
}
