package com.shivam.iitcampusproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Filters.FilterProductBuyer;
import com.shivam.iitcampusproject.Model.Products;
import com.shivam.iitcampusproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserShowProducts extends RecyclerView.Adapter<UserShowProducts.HolderUserShowProducts> implements Filterable {

    private Context context;
    public ArrayList<Products> productsList, filterlist;
    private FilterProductBuyer filter;

    private String SellerCity, UserCity, SellerDeliveryFee, DeliveryFee;


    public UserShowProducts(Context context, ArrayList<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterlist = productsList;
    }

    @NonNull
    @Override
    public HolderUserShowProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_products_viewholder, parent, false);
        return new HolderUserShowProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUserShowProducts holder, int position) {

        Products products = productsList.get(position);

        String discountAvailable = products.getDiscount_available();
        String discount_note = products.getDiscount_desc();
        String discount_price = products.getDiscountPrice();
        String category = products.getCategory();
        String original_price = products.getPrice();
        String product_dec = products.getProduct_desc();
        String product_name = products.getProduct_name();
        String quantity = products.getQuantity();
        String product_id = products.getProductId();
        String timestamp = products.getTimestamp();
        String product_image = products.getProduct_image();
        String uid = products.getUid();

        LoadSellerCity(uid);
        LoadUserCity();

        holder.product_name.setText(product_name);
        holder.quantity.setText(quantity);
        holder.discount_note.setText(discount_note);
        holder.discount_price.setText("Rs." + discount_price);
        holder.original_price.setText("Rs." + original_price);

        if (discountAvailable.equals("true")) {

            holder.discount_price.setVisibility(View.VISIBLE);
            holder.discount_note.setVisibility(View.VISIBLE);
            holder.original_price.setPaintFlags(holder.original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            holder.discount_price.setVisibility(View.GONE);
            holder.discount_note.setVisibility(View.GONE);
            holder.original_price.setPaintFlags(0);
        }
        try {
            Picasso.get().load(product_image).placeholder(R.drawable.picture).into(holder.product_image);

        } catch (Exception e) {
            holder.product_image.setImageResource(R.drawable.picture);

        }

        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserAddress(products);


            }
        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, Show_one_product_Activity.class);
//                intent.putExtra("productId",product_id);
//                intent.putExtra("uid",uid);
//                context.startActivity(intent);
//
//            }
//        });

    }

    private void checkUserAddress(Products products) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        String deafault_address = "" + snapshot.child("deafault_address").getValue();

                        if (deafault_address.equals("null")) {

                            Toast.makeText(context, "Please Select your default Address from Settings", Toast.LENGTH_SHORT).show();

                        } else {
                            showQuantityDialog(products);
                        }


                    } else {

                        Toast.makeText(context, "Please add Your Shipping Address Settings", Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadUserCity() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    UserCity = "" + snapshot.child("city").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadSellerCity(String uid) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SellerCity = "" + snapshot.child("city").getValue();
                    SellerDeliveryFee = "" + snapshot.child("delivery_fee").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double cost = 0;
    private double finalcost = 0;
    private int Quantity = 0;

    private void showQuantityDialog(Products products) {

        View view = LayoutInflater.from(context).inflate(R.layout.quantity_dialogbox, null);

        CircleImageView product_imageTv = view.findViewById(R.id.product_image);
        TextView product_nameTv = view.findViewById(R.id.product_name);
        TextView quantityTv = view.findViewById(R.id.quantitytv);
        TextView product_quantity = view.findViewById(R.id.quantity);
        TextView descriptionTv = view.findViewById(R.id.description);
        TextView discount_noteTv = view.findViewById(R.id.discount_note);
        TextView original_priceTv = view.findViewById(R.id.original_price);
        TextView discount_priceTv = view.findViewById(R.id.discount_price);
        TextView final_priceTv = view.findViewById(R.id.final_price);
        ImageButton btn_decrementTv = view.findViewById(R.id.btn_decrement);
        ImageButton btn_incrementTv = view.findViewById(R.id.btn_increment);
        // TextView txtquantityTv  = view.findViewById(R.id.quantitytv);
        Button btn_continueTv = view.findViewById(R.id.btncontinue);


        //get data from model product
        String discountAvailable = products.getDiscount_available();
        String discount_note = products.getDiscount_desc();
        String discount_price = products.getDiscountPrice();
        String category = products.getCategory();
        String original_price = products.getPrice();
        String product_dec = products.getProduct_desc();
        String product_name = products.getProduct_name();
        String quantity = products.getQuantity();
        String product_id = products.getProductId();
        String timestamp = products.getTimestamp();
        String product_image = products.getProduct_image();

        String price;
        if (products.getDiscount_available().equals("true")) {
            price = products.getDiscountPrice();
            discount_noteTv.setVisibility(View.VISIBLE);
            original_priceTv.setPaintFlags(original_priceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {

            discount_noteTv.setVisibility(View.GONE);
            discount_priceTv.setVisibility(View.GONE);
            price = products.getPrice();

        }

        cost = Double.parseDouble(price.replaceAll("Rs.", ""));
        finalcost = Double.parseDouble(price.replaceAll("Rs.", ""));
        Quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
        builder.setView(view);

        try {
            Picasso.get().load(product_image).placeholder(R.drawable.profile).into(product_imageTv);

        } catch (Exception e) {
            product_imageTv.setImageResource(R.drawable.profile);
        }

        product_nameTv.setText("" + product_name);
        quantityTv.setText("" + Quantity);
        descriptionTv.setText("" + product_dec);
        discount_noteTv.setText("" + discount_note);
        product_quantity.setText("" + quantity);
        original_priceTv.setText("" + products.getPrice());
        discount_priceTv.setText("" + products.getDiscountPrice());
        final_priceTv.setText("" + finalcost);

        AlertDialog dialog = builder.create();
        dialog.show();

        btn_incrementTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalcost = finalcost + cost;
                Quantity++;

                final_priceTv.setText("" + finalcost);
                quantityTv.setText("" + Quantity);
            }
        });

        btn_decrementTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Quantity > 1) {
                    finalcost = finalcost - cost;
                    Quantity--;

                    final_priceTv.setText("Rs." + finalcost);
                    quantityTv.setText("" + Quantity);
                }
            }
        });

        btn_continueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String product_name = product_nameTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = final_priceTv.getText().toString().trim();
                String quantity = quantityTv.getText().toString().trim();
                String image = product_image.toString().trim();
                String uid = products.getUid();
                String productId = products.getProductId();

                //add to Db
                addTocart(product_id, product_name, priceEach, totalPrice, quantity, image, uid, product_id);
                dialog.dismiss();
            }
        });

    }


    private int itemId = 1;
    double totalPrice;

    private void addTocart(String product_id, String product_name, String priceEach, String price, String quantity, String image, String uid, String productId) {
        itemId++;


        String timestamp = "" + System.currentTimeMillis();

        if (UserCity.equals(SellerCity)) {

            totalPrice = Double.valueOf(price) + Double.valueOf("50");

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Item_PID", product_id);
            hashMap.put("Item_Name", product_name);
            hashMap.put("Item_Price_Each", priceEach);
            hashMap.put("Item_Price", price);
            hashMap.put("Item_Quantity", quantity);
            hashMap.put("Item_Image", image);
            hashMap.put("timestamp", timestamp);
            hashMap.put("SellerUid", uid);
            hashMap.put("productId", product_id);
            hashMap.put("DeliveryFee", "50");// this is random price
            hashMap.put("TotalPrice", "" + totalPrice);
            hashMap.put("status", "inCart");

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference1.child(FirebaseAuth.getInstance().getUid()).child("Cart").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context.getApplicationContext(), "Item Added....", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            totalPrice = Double.valueOf(price) + Double.valueOf(SellerDeliveryFee);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Item_PID", product_id);
            hashMap.put("Item_Name", product_name);
            hashMap.put("Item_Price_Each", priceEach);
            hashMap.put("Item_Price", price);
            hashMap.put("Item_Quantity", quantity);
            hashMap.put("Item_Image", image);
            hashMap.put("timestamp", timestamp);
            hashMap.put("SellerUid", uid);
            hashMap.put("productId", product_id);
            hashMap.put("DeliveryFee", SellerDeliveryFee);
            hashMap.put("TotalPrice", "" + totalPrice);
            hashMap.put("status", "inCart");

            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference2.child(FirebaseAuth.getInstance().getUid()).child("Cart").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context.getApplicationContext(), "Item Added....", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterProductBuyer(this, filterlist);
        }
        return filter;
    }

    class HolderUserShowProducts extends RecyclerView.ViewHolder {

        RoundedImageView product_image;
        TextView product_name, quantity, discount_price, original_price, discount_note, quantitytv;
        TextView addtocart;

        public HolderUserShowProducts(@NonNull View itemView) {
            super(itemView);

            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            discount_price = itemView.findViewById(R.id.discount_price);
            original_price = itemView.findViewById(R.id.original_price);
            discount_note = itemView.findViewById(R.id.discountnote);
            addtocart = itemView.findViewById(R.id.addtocart);
            //quantitytv = itemView.findViewById(R.id.quantity);
        }
    }
}
