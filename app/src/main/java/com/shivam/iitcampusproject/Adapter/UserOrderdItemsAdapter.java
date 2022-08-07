package com.shivam.iitcampusproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Model.UserOrderItems;
import com.shivam.iitcampusproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class UserOrderdItemsAdapter extends RecyclerView.Adapter<UserOrderdItemsAdapter.HolderBuyerOrderdItems> {

    Context context;
    ArrayList<UserOrderItems> userOrderItemsArrayList;
    private String status, selleruid, getpId,orderid;
    FirebaseAuth mAuth;

    public UserOrderdItemsAdapter(Context context, ArrayList<UserOrderItems> userOrderItemsArrayList, String orderStatus, String orderTo, String orderId) {
        this.context = context;
        this.userOrderItemsArrayList = userOrderItemsArrayList;
        this.status = orderStatus;
        this.selleruid = orderTo;
        this.orderid = orderId;
    }

    @NonNull
    @Override
    public HolderBuyerOrderdItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_items_viewholder, parent, false);
        return new HolderBuyerOrderdItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBuyerOrderdItems holder, int position) {

        UserOrderItems userOrderItems = userOrderItemsArrayList.get(position);

        getpId = userOrderItems.getpId();
        String name = userOrderItems.getName();
        String cost = userOrderItems.getCost();
        String priceEach = userOrderItems.getPrice_Each();
        String quantity = userOrderItems.getQuantity();
        String image = userOrderItems.getProduct_Img();

        checkRatingAvailable(holder);

        holder.itemNameTv.setText(name);
        holder.itemPriceEachTv.setText("Rs." + priceEach);
        holder.itemPriceTv.setText("Rs." + cost);
        holder.itemQuantityTv.setText("[" + quantity + "]");

        try {
            Picasso.get().load(image).placeholder(R.drawable.picture).into(holder.imageView);

        } catch (Exception e) {

            holder.imageView.setImageResource(R.drawable.picture);

        }

        holder.rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(context, "You're not Logged In", Toast.LENGTH_SHORT).show();

                } else {
                    if (status.equals("Completed")) {

                        checkRatingAvailable(holder);
                    } else {
                        Toast.makeText(context, "Your Order is " + status, Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        holder.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputData(holder);

            }
        });
    }

    private void checkRatingAvailable(HolderBuyerOrderdItems holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(selleruid);
        reference.child("Products").child(getpId).child("Rating").child(orderid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {



                    //  Toast.makeText(context, "You Already Rated", Toast.LENGTH_SHORT).show();
                    holder.rateBtn.setImageResource(R.drawable.ic_baseline_star_rate_24);
                    holder.HiddenLayout.setVisibility(View.GONE);

                } else {

                    if (status.equals("Completed")) {
                        holder.rateBtn.setImageResource(R.drawable.ic_rate);
                        holder.HiddenLayout.setVisibility(View.VISIBLE);
                    } else {

                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void InputData(HolderBuyerOrderdItems holder) {

        String ratings = "" + holder.ratingBar.getRating();
        String review = "" + holder.reviewEt.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();

        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> data = new HashMap<>();
        data.put("userUid", "" + mAuth.getUid());
        data.put("ratings", "" + ratings);
        data.put("review", "" + review);
        data.put("timestamp", "" + timestamp);

        //this is for shop review
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(selleruid).child("Rating");
        databaseReference1.child(orderid).setValue(data);

        //this is for product review
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(selleruid);
        reference.child("Products").child(getpId).child("Rating").child(orderid).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(context, "Review Published Successfully...", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userOrderItemsArrayList.size();
    }

    static class HolderBuyerOrderdItems extends RecyclerView.ViewHolder {

        TextView itemNameTv, itemPriceTv, itemPriceEachTv, itemQuantityTv;
        RoundedImageView imageView;
        ImageButton rateBtn;
        RelativeLayout HiddenLayout;
        RatingBar ratingBar;
        EditText reviewEt;
        FloatingActionButton submitBtn;

        public HolderBuyerOrderdItems(@NonNull View itemView) {
            super(itemView);

            itemNameTv = itemView.findViewById(R.id.itemNameTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            imageView = itemView.findViewById(R.id.productImg);
            rateBtn = itemView.findViewById(R.id.rateBtn);
            HiddenLayout = itemView.findViewById(R.id.HiddenLayout);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            reviewEt = itemView.findViewById(R.id.reviewEt);
            submitBtn = itemView.findViewById(R.id.submitBtn);
        }
    }
}
