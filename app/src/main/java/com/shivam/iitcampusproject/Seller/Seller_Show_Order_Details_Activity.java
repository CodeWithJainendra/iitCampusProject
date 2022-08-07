package com.shivam.iitcampusproject.Seller;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.SellerShowUserOrderItems;
import com.shivam.iitcampusproject.Constants;
import com.shivam.iitcampusproject.Model.UserOrderItems;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.databinding.ActivitySellerShowOrderDetailsBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Seller_Show_Order_Details_Activity extends AppCompatActivity {

    ActivitySellerShowOrderDetailsBinding binding;

    private String orderId, orderBy,orderStatus;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    private String delivery_address;
    private String deafault_address, main_address;
    double buyer_longitude, buyer_latitude;
    double seller_longitude, seller_latitude;

    private ArrayList<UserOrderItems> userOrderItemsArrayList;
    private SellerShowUserOrderItems adaptersellerShowUserOrderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerShowOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        binding.toolbar.setTitle("Order Details");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LoadMyInfo();
        LoadBuyerInfo();
        loadOrderdItems();


    }
    private void LoadMyInfo() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    seller_latitude = Double.parseDouble("" + snapshot.child("latitude").getValue());
                    //  Log.d("TAGS",String.valueOf(seller_latitude));
                    seller_longitude = Double.parseDouble("" + snapshot.child("longitude").getValue());
                } else {
                    Toast.makeText(Seller_Show_Order_Details_Activity.this, "No Location Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Seller_Show_Order_Details_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadBuyerInfo() {
        DatabaseReference databaseReference = database.getReference("Users").child(orderBy);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String email = "" + snapshot.child("email").getValue();
                    String phone = "" + snapshot.child("mobile").getValue();
                    main_address = "" + snapshot.child("address_main").getValue();
                    deafault_address = "" + snapshot.child("deafault_address").getValue();


                    binding.emailTv.setText(email);
                    binding.phoneTv.setText(phone);

                    LoadOrderDetails(main_address,deafault_address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Seller_Show_Order_Details_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadOrderDetails(String main_address, String deafault_address) {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders").child(orderId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    String orderBy = "" + snapshot.child("orderBy").getValue();
                    String orderCost = "" + snapshot.child("orderCost").getValue();
                    String orderId = "" + snapshot.child("orderId").getValue();
                    orderStatus = "" + snapshot.child("orderStatus").getValue();
                    String orderTime = "" + snapshot.child("orderTime").getValue();
                    String orderTo = "" + snapshot.child("orderTo").getValue();
                    String delivery_fee = "" + snapshot.child("delivery_fee").getValue();
                    delivery_address = "" + snapshot.child("delivery_address").getValue();


                    if (snapshot.exists()) {

                       if (deafault_address.equals(main_address)){

                            buyer_latitude = Double.parseDouble("" + snapshot.child("delivery_latitude").getValue());
                            buyer_longitude = Double.parseDouble("" + snapshot.child("delivery_longitude").getValue());
                            //   findAddress(buyer_latitude,buyer_longitude);
                        } else {
                            delivery_address = "" + snapshot.child("delivery_address").getValue();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                    }


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(orderTime));
                    String dateFormated = DateFormat.format("dd/MM/yyyy", calendar).toString();

                    if (orderStatus.equals("InProgress")) {
                        binding.orderStatusTv.setTextColor(getResources().getColor(R.color.success));

                    } else if (orderStatus.equals("Completed")) {
                        binding.orderStatusTv.setTextColor(getResources().getColor(R.color.col_red));
                    } else if (orderStatus.equals("Cancelled")) {
                        binding.orderStatusTv.setTextColor(getResources().getColor(R.color.background2_startcolor));
                    }

                    binding.orderIdTv.setText(orderId);
                    binding.orderStatusTv.setText(orderStatus);
                    binding.amountTv.setText("Rs." + orderCost + "[Including delivery Fee" + "]");
                    binding.dateTv.setText(dateFormated);
                    binding.addressTv.setText(delivery_address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Seller_Show_Order_Details_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrderdItems() {

        userOrderItemsArrayList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders").child(orderId).child("Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userOrderItemsArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    UserOrderItems userOrderItems = ds.getValue(UserOrderItems.class);

                    userOrderItemsArrayList.add(userOrderItems);
                }
                adaptersellerShowUserOrderItems = new SellerShowUserOrderItems(getApplicationContext(), userOrderItemsArrayList);
                binding.recycleview.setAdapter(adaptersellerShowUserOrderItems);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.buyer_location_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_location:

                try {
                    if (main_address.equals(deafault_address)) {

                        Log.d("TAGS1", String.valueOf(seller_latitude));
                        Log.d("TAGS2", String.valueOf(seller_longitude));
                        Log.d("TAGS3", String.valueOf(buyer_latitude));
                        Log.d("TAGS4", String.valueOf(buyer_longitude));

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + seller_latitude + "," + seller_longitude + "&daddr=" + buyer_latitude + "," + buyer_longitude));
                        startActivity(intent);
//
                    } else {

                        Toast.makeText(getApplicationContext(), "You didn't choose main address as default address", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_edit:
                Log.d("ORDERBYBY",orderBy);
                Log.d("ORDERBYIDID",orderId);
                editOrderStatusDialog();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void editOrderStatusDialog() {

        String[] options = {"InProgress", "Completed", "Cancelled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selectedOption = options[i];

                        editOrderStatus(selectedOption);
                    }
                }).show();
    }

    private void editOrderStatus(String selectedOption) {

        HashMap<String, Object> update = new HashMap<>();
        update.put("orderStatus", "" + selectedOption);


        DatabaseReference reference = database.getReference("Users");
        reference.child(orderBy).child("Orders").child(orderId).updateChildren(update);
        reference.child(mAuth.getUid()).child("Orders").child(orderId).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                String message = "Order is now " + selectedOption;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                prepareNotificationMessage(orderId,message);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        LoadBuyerInfo();
        super.onStart();
    }

    private void prepareNotificationMessage(String orderId,String message){
        //when seller changed order,send notification to buyer

        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order "+orderId;
        String NOTIFICATION_MESSAGE = ""+message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        //prepare json(what to send where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",orderBy);
            notificationBodyJo.put("SellerUid",mAuth.getUid());
            notificationBodyJo.put("orderId",orderId);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to",NOTIFICATION_TOPIC); //to all who subscribed to this topic
            notificationJo.put("data",notificationBodyJo);

        }catch (Exception e){

            Toast.makeText(Seller_Show_Order_Details_Activity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {

        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //notification sent
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //notification failed
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
        Volley.newRequestQueue(Seller_Show_Order_Details_Activity.this).add(jsonObjectRequest);
    }
}