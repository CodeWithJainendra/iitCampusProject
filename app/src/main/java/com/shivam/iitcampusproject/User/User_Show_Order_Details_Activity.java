package com.shivam.iitcampusproject.User;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.UserOrderdItemsAdapter;
import com.shivam.iitcampusproject.Model.UserOrderItems;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.databinding.ActivityUserShowOrderDetailsBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class User_Show_Order_Details_Activity extends AppCompatActivity {

    ActivityUserShowOrderDetailsBinding binding;

    private String orderId,orderBy,orderStatus,orderTo;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    String delivery_address;
    String deafault_address,main_address;
    double buyer_longitude,buyer_latitude;
    double seller_longitude,seller_latitude;

    private ArrayList<UserOrderItems> userOrderItemsArrayList;
    private UserOrderdItemsAdapter userOrderdItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserShowOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");



       // LoadMyInfo();
        LoadBuyerInfo();
        LoadOrderDetails();
        loadOrderdItems();



    }

    private void LoadBuyerInfo() {

        DatabaseReference databaseReference = database.getReference("Users").child(orderBy);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren() ){
                    String email = ""+snapshot.child("email").getValue();
                    String phone = ""+snapshot.child("mobile").getValue();
                    main_address = ""+snapshot.child("address_main").getValue();
                    deafault_address = ""+snapshot.child("deafault_address").getValue();

                    Log.d("MAINADDRESS",main_address);
                    Log.d("MAINADDRESS1",deafault_address);

                    binding.emailTv.setText(email);
                    binding.phoneTv.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void LoadOrderDetails() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders").child(orderId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    String orderBy = ""+snapshot.child("orderBy").getValue();
                    String orderCost = ""+snapshot.child("orderCost").getValue();
                    String orderId = ""+snapshot.child("orderId").getValue();
                    orderStatus = ""+snapshot.child("orderStatus").getValue();
                    String orderTime = ""+snapshot.child("orderTime").getValue();
                    orderTo = ""+snapshot.child("orderTo").getValue();
                    String delivery_fee = ""+snapshot.child("delivery_fee").getValue();
                    delivery_address = ""+snapshot.child("delivery_address").getValue();

                    if (snapshot.exists()){

                            if (deafault_address.equals(main_address)){

                            buyer_latitude = Double.parseDouble(""+snapshot.child("delivery_latitude").getValue());
                            buyer_longitude = Double.parseDouble(""+snapshot.child("delivery_longitude").getValue());
                            //   findAddress(buyer_latitude,buyer_longitude);
                        }
                        else {
                            delivery_address= ""+snapshot.child("delivery_address").getValue();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"No Data",Toast.LENGTH_SHORT).show();
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(orderTime));
                    String dateFormated = DateFormat.format("dd/MM/yyyy",calendar).toString();

                    if (orderStatus.equals("InProgress")){
                        binding.orderStatusTv.setTextColor(getResources().getColor(R.color.success));

                    }else if (orderStatus.equals("Completed")){
                        binding.orderStatusTv.setTextColor(getResources().getColor(R.color.col_red));
                    }
                    else if (orderStatus.equals("Cancelled")){
                        binding.orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }

                    binding.orderIdTv.setText(orderId);
                    binding.orderStatusTv.setText(orderStatus);
                    binding.amountTv.setText("Rs."+orderCost +"[Including delivery Fee"+"]");
                    binding.dateTv.setText(dateFormated);
                    binding.addressTv.setText(delivery_address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void LoadMyInfo() {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seller_latitude = Double.parseDouble(""+snapshot.child("latitude").getValue());
                //  Log.d("TAGS",String.valueOf(seller_latitude));
                seller_longitude = Double.parseDouble(""+snapshot.child("longitude").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.child(mAuth.getUid()).addListenerForSingleValueEvent(valueEventListener);

    }

    private void loadOrderdItems(){

        userOrderItemsArrayList = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userOrderItemsArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){
                    UserOrderItems userOrderItems = ds.getValue(UserOrderItems.class);

                    userOrderItemsArrayList.add(userOrderItems);
                }
                userOrderdItemsAdapter = new UserOrderdItemsAdapter(User_Show_Order_Details_Activity.this,userOrderItemsArrayList,orderStatus,orderTo,orderId);
                binding.recycleview.setAdapter(userOrderdItemsAdapter);

                //set total of items/products in order
               // binding.totalItemsTv.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.child(orderBy).child("Orders").child(orderId).child("Items").addListenerForSingleValueEvent(valueEventListener);

    }

}