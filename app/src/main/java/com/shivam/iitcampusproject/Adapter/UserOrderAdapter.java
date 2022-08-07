package com.shivam.iitcampusproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Model.UserOrders;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.User.User_Show_Order_Details_Activity;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.HolderUserOrder> {

    private Context context;
    private ArrayList<UserOrders> buyerOrdersList;

    private String orderBy,orderId;

    public UserOrderAdapter(Context context, ArrayList<UserOrders> buyerOrdersList) {
        this.context = context;
        this.buyerOrdersList = buyerOrdersList;
    }

    @NonNull
    @Override
    public HolderUserOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_user,parent,false);
        return new HolderUserOrder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUserOrder holder, @SuppressLint("RecyclerView") int position) {

        UserOrders buyerOrders = buyerOrdersList.get(position);

        orderId = buyerOrders.getOrderId();
        orderBy = buyerOrders.getOrderBy();
        String orderCost = buyerOrders.getOrderCost();
        String orderStatus = buyerOrders.getOrderStatus();
        String orderTime= buyerOrders.getOrderTime();
        String orderT0 = buyerOrders.getOrderTo();
        String shopName = buyerOrders.getShopname();
        String image = buyerOrders.getProduct_Img();

        holder.amountTv.setText("Amount Rs. :" +orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderTv.setText("Order Id :" +orderId);
     //   Log.d("ORDERTV",orderId);
        holder.shopNameTv.setText("Shop Name :" +shopName);

        Log.d("NEWID",orderId);


        if (orderStatus.equals("InProgress")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.success));
            holder.backgroundIv.setImageResource(R.drawable.ordder_inprogress_bg);



        }else if (orderStatus.equals("Completed")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.background2_startcolor));
            holder.backgroundIv.setImageResource(R.drawable.order_completed_bg);

        }

        else if (orderStatus.equals("Cancelled")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.col_red));
            holder.backgroundIv.setImageResource(R.drawable.order_cancelled_bg);
        }

        //change timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatDate = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.dateTv.setText(formatDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserOrders buyerOrders = buyerOrdersList.get(position);
                String id = buyerOrders.getOrderId();

                Intent intent = new Intent(context, User_Show_Order_Details_Activity.class);
                intent.putExtra("orderBy",orderBy);
                intent.putExtra("orderId",id);
                Log.d("ADAPTERID",orderId);
                intent.putExtra("orderTo",orderT0);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return buyerOrdersList.size();
    }

    public class HolderUserOrder extends RecyclerView.ViewHolder {

        TextView orderTv,dateTv,shopNameTv,amountTv,statusTv;
        ImageView imageView;
        RelativeLayout expandableLayout;
        CircleImageView productImg;
        RoundedImageView backgroundIv;

        public HolderUserOrder(@NonNull View itemView) {
            super(itemView);

            orderTv = itemView.findViewById(R.id.orderTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            backgroundIv = itemView.findViewById(R.id.backgroundIv);



        }
    }
}
