package com.shivam.iitcampusproject.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.SellerAdapter;
import com.shivam.iitcampusproject.Model.AdminCheckSeller;
import com.shivam.iitcampusproject.databinding.ActivitySellerRequestsBinding;

import java.util.ArrayList;

public class SellerRequestsActivity extends AppCompatActivity {

    ActivitySellerRequestsBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    private ArrayList<AdminCheckSeller> adminCheckSellersList;
    private SellerAdapter sellerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        LoadSellerRequests();
    }

    private void LoadSellerRequests() {

        adminCheckSellersList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("AdminChecking");
        databaseReference.orderByChild("Is_approval").equalTo("wait").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminCheckSellersList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdminCheckSeller adminCheckSeller = ds.getValue(AdminCheckSeller.class);

                    String dd = adminCheckSeller.getAddress();
                    Log.d("GGG",dd);
                    adminCheckSellersList.add(adminCheckSeller);

                }
                sellerAdapter = new SellerAdapter(SellerRequestsActivity.this, adminCheckSellersList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(SellerRequestsActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.recycleview.setLayoutManager(layoutManager);
                binding.recycleview.setAdapter(sellerAdapter);
                sellerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SellerRequestsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}