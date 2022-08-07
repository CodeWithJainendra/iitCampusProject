package com.shivam.iitcampusproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Model.UserOrderItems;
import com.shivam.iitcampusproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SellerShowUserOrderItems extends RecyclerView.Adapter<UserOrderdItemsAdapter.HolderBuyerOrderdItems>{

    Context context;
    ArrayList<UserOrderItems> userOrderItemsArrayList;

    public SellerShowUserOrderItems(Context context, ArrayList<UserOrderItems> userOrderItemsArrayList) {
        this.context = context;
        this.userOrderItemsArrayList = userOrderItemsArrayList;
    }

    @NonNull
    @Override
    public UserOrderdItemsAdapter.HolderBuyerOrderdItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seller_user_order_items_viewholder, parent, false);
        return new UserOrderdItemsAdapter.HolderBuyerOrderdItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrderdItemsAdapter.HolderBuyerOrderdItems holder, int position) {

        UserOrderItems userOrderItems = userOrderItemsArrayList.get(position);

        String getpId = userOrderItems.getpId();
        String name = userOrderItems.getName();
        String cost = userOrderItems.getCost();
        String priceEach = userOrderItems.getPrice_Each();
        String quantity = userOrderItems.getQuantity();
        String image = userOrderItems.getProduct_Img();

        holder.itemNameTv.setText(name);
        holder.itemPriceEachTv.setText("Rs." + priceEach);
        holder.itemPriceTv.setText("Rs." + cost);
        holder.itemQuantityTv.setText("[" + quantity + "]");

        try {
            Picasso.get().load(image).placeholder(R.drawable.picture).into(holder.imageView);

        } catch (Exception e) {

            holder.imageView.setImageResource(R.drawable.picture);

        }
    }

    @Override
    public int getItemCount() {
        return userOrderItemsArrayList.size();
    }

    class HolderBuyerOrderdItems extends RecyclerView.ViewHolder {

        TextView itemNameTv, itemPriceTv, itemPriceEachTv, itemQuantityTv;
        RoundedImageView imageView;


        public HolderBuyerOrderdItems(@NonNull View itemView) {
            super(itemView);

            itemNameTv = itemView.findViewById(R.id.itemNameTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            imageView = itemView.findViewById(R.id.productImg);
        }
    }
}
