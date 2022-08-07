package com.shivam.iitcampusproject.Fragments.Seller.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.UserOrderAdapter;
import com.shivam.iitcampusproject.Model.UserOrderItems;
import com.shivam.iitcampusproject.Model.UserOrders;
import com.shivam.iitcampusproject.databinding.FragmentShowOrdersBinding;

import java.util.ArrayList;


public class ShowOrdersFragment extends Fragment {

    FragmentShowOrdersBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private ArrayList<UserOrders> buyerOrdersList;
    private ArrayList<UserOrderItems> buyerOrderItemsList;
    private UserOrderAdapter userOrderAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShowOrdersBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        LoadOrders();
        // LoadOrderItems();


        return binding.getRoot();
    }

    private void LoadOrderItems() {

        buyerOrderItemsList = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                buyerOrdersList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = "" + ds.getRef().getKey();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("Orders");

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            buyerOrdersList.clear();
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    UserOrders buyerOrders = ds.getValue(UserOrders.class);

                                    //add to list
                                    buyerOrdersList.add(buyerOrders);
                                }
                                //set adapter
                                userOrderAdapter = new UserOrderAdapter(getContext(), buyerOrdersList);
                                binding.recycleview.setAdapter(userOrderAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void LoadOrders() {

        buyerOrdersList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                buyerOrdersList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        UserOrders buyerOrders = ds.getValue(UserOrders.class);

                        //add to list
                        buyerOrdersList.add(buyerOrders);
                    }
                    //set adapter
                    userOrderAdapter = new UserOrderAdapter(getContext(), buyerOrdersList);
                    binding.recycleview.setAdapter(userOrderAdapter);
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

    @Override
    public void onStart() {
        LoadOrders();
        super.onStart();
    }
}