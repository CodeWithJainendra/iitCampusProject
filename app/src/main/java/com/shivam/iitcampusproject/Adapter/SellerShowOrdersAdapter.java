package com.shivam.iitcampusproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Filters.FilterOrders;
import com.shivam.iitcampusproject.Model.ShowShopOrders;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.Seller.Seller_Show_Order_Details_Activity;

import java.util.ArrayList;
import java.util.Calendar;

public class SellerShowOrdersAdapter extends RecyclerView.Adapter<SellerShowOrdersAdapter.HolderSellerShowOrders> implements Filterable {

    private Context context;
    public ArrayList<ShowShopOrders> showShopOrdersArrayList,filterList;
    private FilterOrders filter;
    private String orderBy, orderId;

    public SellerShowOrdersAdapter(Context context, ArrayList<ShowShopOrders> showShopOrdersArrayList) {
        this.context = context;
        this.showShopOrdersArrayList = showShopOrdersArrayList;
        this.filterList = showShopOrdersArrayList;
    }

    @NonNull
    @Override
    public HolderSellerShowOrders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_seller_show_orders, parent, false);
        return new HolderSellerShowOrders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSellerShowOrders holder, @SuppressLint("RecyclerView") int position) {

        ShowShopOrders showShopOrders = showShopOrdersArrayList.get(position);

        orderId = showShopOrders.getOrderId();
        orderBy = showShopOrders.getOrderBy();
        String orderCost = showShopOrders.getOrderCost();
        String orderStatus = showShopOrders.getOrderStatus();
        String orderTime = showShopOrders.getOrderTime();
        String orderT0 = showShopOrders.getOrderTo();
        String shopName = showShopOrders.getShopName();
        String image = showShopOrders.getProduct_Img();

        holder.amountTv.setText("Amount Rs. :" + orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("Order Id :" + orderId);
        Log.d("ORDERBy", orderBy);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(orderBy);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String email = "" + snapshot.child("email").getValue();

                    holder.emailTv.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        if (orderStatus.equals("InProgress")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.success));
            holder.cardView.setImageResource(R.drawable.ordder_inprogress_bg);

        } else if (orderStatus.equals("Completed")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.background2_startcolor));
            holder.cardView.setImageResource(R.drawable.order_completed_bg);

        } else if (orderStatus.equals("Cancelled")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.col_red));
            holder.cardView.setImageResource(R.drawable.order_cancelled_bg);
        }

        //change timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatDate = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.orderDateTv.setText(formatDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowShopOrders showShopOrders = showShopOrdersArrayList.get(position);
                String id = showShopOrders.getOrderId();
                String orderBy1 = showShopOrders.getOrderBy();

                Log.d("ORDERIDADAPTER",id);

                Intent intent = new Intent(context, Seller_Show_Order_Details_Activity.class);
                intent.putExtra("orderId",id);
                intent.putExtra("orderBy",orderBy1);
                intent.putExtra("orderTo",orderT0);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return showShopOrdersArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterOrders(this,filterList);
        }
        return filter;
    }

    public class HolderSellerShowOrders extends RecyclerView.ViewHolder {

        TextView orderIdTv, emailTv, amountTv, orderDateTv, statusTv;
        RoundedImageView cardView;

        public HolderSellerShowOrders(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
