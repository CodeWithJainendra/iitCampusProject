package com.shivam.iitcampusproject.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.CartRecyclerViewRemoveInterface;
import com.shivam.iitcampusproject.Constants;
import com.shivam.iitcampusproject.Model.CartItem;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.User.User_Show_Order_Details_Activity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.HolderCartItem> {

    private Context context;
    private ArrayList<CartItem> cartItems;
    private CartRecyclerViewRemoveInterface cartRecyclerViewRemoveInterface;
    private Dialog dialog;

    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;


    private Double TotalPriceWithShipping;
    private String deafault_address, mobile_number, main_Address;

    private String SellerCity, UserCity, TotalPrice, SellerDiliveryFee, shopName,selleruid;
    double costs, shippingfee;
    double latitude, longitude;

    ProgressDialog progressDialog;


    public CartItemAdapter(Context context, ArrayList<CartItem> cartItems,CartRecyclerViewRemoveInterface cartRecyclerViewRemoveInterface){
        this.context = context;
        this.cartItems = cartItems;
        this.cartRecyclerViewRemoveInterface = cartRecyclerViewRemoveInterface;
    }


    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //  View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item,parent,false);
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item_activity, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, @SuppressLint("RecyclerView") int position) {

        CartItem cartItem = cartItems.get(position);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        String id = cartItem.getProductId();
        String getpId = cartItem.getItem_PID();
        String title = cartItem.getItem_Name();
        String cost = cartItem.getItem_Price();
        String priceEach = cartItem.getItem_Price_Each();
        String quantity = cartItem.getItem_Quantity();
        String image = cartItem.getItem_Image();
        selleruid = cartItem.getSellerUid();
        String CartItemTimestamp = cartItem.getTimestamp();
        String deliveryFee = cartItem.getDeliveryFee();
        String productName = cartItem.getItem_Name();

        Log.d("SELLERID", selleruid);

        holder.itemTitleTv.setText("" + title);

        holder.itemQuantityTv.setText("[" + quantity + "]");
        holder.itemPricEachtv.setText("Rs. " + priceEach);
        holder.shipping_price.setText("include " + deliveryFee + " shipping Fee");


        costs = Double.valueOf(cost);
        shippingfee = Integer.valueOf(deliveryFee);


        TotalPriceWithShipping = Double.valueOf(cost) + Double.valueOf(deliveryFee);
        holder.itemPriceTv.setText("Rs. " + TotalPriceWithShipping);


        LoadSellerDetails(holder, selleruid, cost);


        try {
            Picasso.get().load(image).placeholder(R.drawable.picture).into(holder.imageView);

        } catch (Exception e) {

            holder.imageView.setImageResource(R.drawable.picture);

        }

        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                CartItem cartItem = cartItems.get(position);
                String timestamp = cartItem.getTimestamp();

                dialogBox("Do you want to remove item", "Alert !",position,timestamp);
            }
        });

        holder.confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartItem cartItem = cartItems.get(position);
                LoadUserDetails(selleruid, getpId, cost, quantity, priceEach, productName, image, CartItemTimestamp,position);



            }
        });


    }

    @SuppressLint("RestrictedApi")
    private void ShowDialogBox(int position) {

        ImageView done;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_messagebox);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = dialog.findViewById(R.id.btn_okay);
        done = dialog.findViewById(R.id.imageView);

        Drawable drawable = done.getDrawable();

        if (drawable instanceof AnimatedVectorDrawableCompat){
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();

        }
        else if (drawable instanceof AnimatedVectorDrawable){
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartRecyclerViewRemoveInterface.onItemClick(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void LoadSellerDetails(HolderCartItem holder, String selleruid, String cost) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(selleruid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    shopName = "" + snapshot.child("shopName").getValue();
                    SellerCity = "" + snapshot.child("city").getValue();
                    SellerDiliveryFee = "" + snapshot.child("delivery_fee").getValue();

                    holder.shop_name.setText(shopName);
                    holder.shop_city.setText("(" + SellerCity + ")");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadUserDetails(String selleruid, String getpId, String cost, String quantity, String priceEach, String productName, String image, String cartItemTimestamp, int position) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String name = "" + snapshot.child("username").getValue();
                    deafault_address = "" + snapshot.child("deafault_address").getValue();
                    mobile_number = "" + snapshot.child("mobile").getValue();
                    main_Address = "" + snapshot.child("address_main").getValue();

                    if (snapshot.exists()) {

                        if (deafault_address.equals(main_Address)) {


                            latitude = Double.valueOf("" + snapshot.child("address_main_latitude").getValue());
                            longitude = Double.valueOf("" + snapshot.child("address_main_longitude").getValue());

                            deafault_address = "" + snapshot.child("deafault_address").getValue();
                        }

                        else {
                            deafault_address = "" + snapshot.child("deafault_address").getValue();
                        }


                    } else {
                        Toast.makeText(context, "User Data Not Found", Toast.LENGTH_SHORT).show();
                    }


                }
                ValidateOrder(selleruid, getpId, cost, quantity, priceEach, productName, image, cartItemTimestamp,position);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ValidateOrder(String selleruid, String getpId, String cost, String quantity, String priceEach, String productName, String image, String cartItemTimestamp, int position) {

        try {


            if (deafault_address.equals("") || deafault_address.equals(null)) {

                Toast.makeText(context, "Please select address in your account settings...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mobile_number.equals("") || mobile_number.equals("null")) {

                Toast.makeText(context, "Please enter mobile number in your profile settings...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cartItems.size() == 0) {
                //cart list is empty
                Toast.makeText(context, "No Items in cart...", Toast.LENGTH_SHORT).show();
                return;
            }

            submitOrder(selleruid, getpId, cost, quantity, priceEach, productName, image, cartItemTimestamp,position);

        } catch (Exception e) {

            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void submitOrder(String selleruid, String getpId, String cost, String quantity, String priceEach, String productName, String image, String cartItemTimestamp, int position) {

        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashmap = new HashMap<>();

        if (deafault_address.equals(main_Address)) {

            hashmap.put("orderId", "" + timestamp);
            hashmap.put("orderTime", "" + timestamp);
            hashmap.put("orderStatus", "InProgress");
            hashmap.put("orderCost", "" + TotalPriceWithShipping);
            hashmap.put("orderBy", "" + FirebaseAuth.getInstance().getUid());
            hashmap.put("orderTo", "" + selleruid);
            hashmap.put("delivery_address", deafault_address);
            hashmap.put("delivery_latitude", ""+latitude);
            hashmap.put("delivery_longitude", ""+longitude);
            hashmap.put("ShopName", shopName);


        } else {

            hashmap.put("orderId", "" + timestamp);
            hashmap.put("orderTime", "" + timestamp);
            hashmap.put("orderStatus", "InProgress");
            hashmap.put("orderCost", "" + TotalPriceWithShipping);
            hashmap.put("orderBy", "" + FirebaseAuth.getInstance().getUid());
            hashmap.put("orderTo", "" + selleruid);
            hashmap.put("delivery_address", deafault_address);
            hashmap.put("ShopName", shopName);
        }


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(selleruid).child("Orders");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Orders");
        reference1.child(timestamp).setValue(hashmap);
        reference.child(timestamp).setValue(hashmap);

        SubmitOrderProducts(timestamp, hashmap, selleruid, getpId, cost, quantity, priceEach, productName, image, cartItemTimestamp,position);
    }

    private void SubmitOrderProducts(String timestamp, HashMap<String, Object> hashmap, String selleruid, String getpId, String cost, String quantity, String priceEach, String productName, String image, String cartItemTimestamp, int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(selleruid).child("Orders");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Orders");

        reference.child(timestamp).setValue(hashmap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("pId", getpId);
                map.put("name", productName);
                map.put("cost", "" + TotalPriceWithShipping);
                map.put("Price_Each", priceEach);
                map.put("quantity", quantity);
                map.put("product_Img", image);
                map.put("shopName", shopName);

                reference.child(timestamp).child("Items").child(getpId).setValue(map);
                reference1.child(timestamp).child("Items").child(getpId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        ShowDialogBox(position);
                        ChangeInCartStatus(cartItemTimestamp);
                     //   Toast.makeText(context, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                        prepareNotificationMessage(timestamp);


                    }
                });
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ChangeInCartStatus(String CartItemTimestamp) {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Cart").child(CartItemTimestamp);
        databaseReference2.removeValue();
    }

    private void dialogBox(String s1, String s, int adapterPosition, String timestamp) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(s);
        alertDialog.setMessage(s1);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Cart");
                    databaseReference1.child(timestamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            cartRecyclerViewRemoveInterface.onItemClick(adapterPosition);
                            //  Toast.makeText(context, "You clicked on OK", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                       Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void prepareNotificationMessage(String orderId){
        //when user placed order,send notification to seller

        Log.d("TIMESTAMP", "Ckecing Notify");

        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order"+orderId;
        String NOTIFICATION_MESSAGE = "Congratulations....! You have new order.";
        String NOTIFICATION_TYPE = "NewOrder";

        //prepare json(what to send where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",FirebaseAuth.getInstance().getUid());
            notificationBodyJo.put("SellerUid",selleruid);
            notificationBodyJo.put("orderId",orderId);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to",NOTIFICATION_TOPIC); //to all who subscribed to this topic
            notificationJo.put("data",notificationBodyJo);

        }catch (Exception e){
            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo,orderId);
    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {

        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start order details activity

                //after placing order open order details activity

//                Intent intent = new Intent(context, User_Show_Order_Details_Activity.class);
//                intent.putExtra("orderId",orderId);
//                intent.putExtra("orderBy",selleruid);
//                context.startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if false sending fcm, still start order details activity
                //after placing order open order details activity
                Intent intent = new Intent(context, User_Show_Order_Details_Activity.class);
                intent.putExtra("orderId",orderId);
                intent.putExtra("orderBy",selleruid);
                context.startActivity(intent);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //put required headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key="+Constants.FCM_KEY);

                return headers;

            }
        };

        //enque the volley request
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder {


        private TextView itemTitleTv, itemPriceTv, itemPricEachtv, itemQuantityTv, itemRemoveTv, shop_name, shop_city, shipping_price;
        private ImageView imageView;
        Button confirmOrder;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPricEachtv = itemView.findViewById(R.id.itemPricEachtv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);
            imageView = itemView.findViewById(R.id.imageView);
            shop_name = itemView.findViewById(R.id.shop_name);
            shop_city = itemView.findViewById(R.id.shop_city);
            shipping_price = itemView.findViewById(R.id.shipping_price);
            confirmOrder = itemView.findViewById(R.id.confirmOrder);



//            itemRemoveTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialogBox("Do you want to remove item", "Alert !",ItemTimestamp1,getAdapterPosition());
//                }
//            });
        }
    }
}
