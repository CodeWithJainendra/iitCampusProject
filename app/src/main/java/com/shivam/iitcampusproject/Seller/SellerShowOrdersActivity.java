package com.shivam.iitcampusproject.Seller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.SellerShowOrdersAdapter;
import com.shivam.iitcampusproject.Model.ShowShopOrders;
import com.shivam.iitcampusproject.Model.UserOrderItems;
import com.shivam.iitcampusproject.databinding.ActivitySellerShowOrdersBinding;

import java.util.ArrayList;

public class SellerShowOrdersActivity extends AppCompatActivity {

    ActivitySellerShowOrdersBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private ArrayList<ShowShopOrders> showShopOrdersArrayList;
    private ArrayList<UserOrderItems> buyerOrderItemsList;
    private SellerShowOrdersAdapter sellerShowOrdersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerShowOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");


        LoadOrders();

        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.filterOrdersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options = {"All","InProgress","Completed","Cancelled"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SellerShowOrdersActivity.this);
                builder.setTitle("Filter Orders")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0){
                                    binding.filterOrdersTv.setText("Showing All Orders");
                                    sellerShowOrdersAdapter.getFilter().filter("");
                                }
                                else {
                                    String optionClicked = options[i];
                                    binding.filterOrdersTv.setText("Showing "+optionClicked+" Orders");
                                    sellerShowOrdersAdapter.getFilter().filter(optionClicked);
                                }
                            }
                        }).show();
            }
        });


    }
    private void LoadOrders() {

        showShopOrdersArrayList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                showShopOrdersArrayList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        ShowShopOrders showShopOrders = ds.getValue(ShowShopOrders.class);

                        //add to list
                        showShopOrdersArrayList.add(showShopOrders);
                    }
                    //set adapter
                    sellerShowOrdersAdapter = new SellerShowOrdersAdapter(SellerShowOrdersActivity.this, showShopOrdersArrayList);
                    binding.recycleview.setAdapter(sellerShowOrdersAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        buyerOrdersList = new ArrayList<>();
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                buyerOrdersList.clear();
//
//                for (DataSnapshot ds: snapshot.getChildren()){
//                    String uid = ""+ds.getRef().getKey();
//
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("Orders");
//
//                    databaseReference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            buyerOrdersList.clear();
//                            if (snapshot.exists()){
//                                for (DataSnapshot ds : snapshot.getChildren()){
//                                    UserOrders buyerOrders =ds.getValue(UserOrders.class);
//
//                                    //add to list
//                                    buyerOrdersList.add(buyerOrders);
//                                }
//                                //set adapter
//                                userOrderAdapter = new UserOrderAdapter(getContext(),buyerOrdersList);
//                                binding.recycleview.setAdapter(userOrderAdapter);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }
}