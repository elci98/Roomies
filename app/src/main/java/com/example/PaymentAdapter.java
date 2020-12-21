package com.example.rommies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    Context context;
    List<Payment> payment_list;

    public PaymentAdapter(Context context , List<Payment> list){
        this.context=context;
        this.payment_list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(payment_list!=null && payment_list.size()>0){
            Payment pay=payment_list.get(position);
            holder.Buyer.setText(pay.getPayer());
            holder.Categore.setText(pay.getReason());
            holder.Price.setText((String.valueOf(pay.getAmount())));
            holder.Date.setText(pay.getDate().toString());

        }else {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return payment_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Buyer,Price,Categore,Date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Buyer=itemView.findViewById(R.id.buy);
            Price=itemView.findViewById(R.id.pri);
            Categore=itemView.findViewById(R.id.categ);
            Date=itemView.findViewById(R.id.date);

        }
    }
}
