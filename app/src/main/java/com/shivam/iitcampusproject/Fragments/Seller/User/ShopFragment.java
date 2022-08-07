package com.shivam.iitcampusproject.Fragments.Seller.User;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.ShopAdapter;
import com.shivam.iitcampusproject.Model.Shops;
import com.shivam.iitcampusproject.databinding.FragmentShopBinding;

import java.util.ArrayList;


public class ShopFragment extends Fragment {

    FragmentShopBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    private ArrayList<Shops> shopsList;
    private ShopAdapter shopAdapter;

    private String checkedType;

    public static final String[] city = {

            "All",
            "Ampara",
            "Anuradhapura",
            "Badulla",
            "Batticaloa",
            "Colombo",
            "Galle",
            "Gampaha",
            "Hambantota",
            "Jaffna",
            "Kalutara",
            "Kandy",
            "Kegalle",
            "Kilinochchi",
            "Kurunegala",
            "Mannar",
            "Matale",
            "Matara",
            "Monaragala",
            "Mullaitivu",
            "Nuwara Eliya",
            "Polonnaruwa",
            "Puttalam",
            "Ratnapura",
            "Trincomalee",
            "Vavuniya",
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.nearShops.setChecked(true);
        loadMycity();

        binding.allShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    checkedType = "allShops";
                    binding.nearShops.setChecked(false);
                    binding.filterbtn.setVisibility(View.VISIBLE);
                    LoadAllShops();
            }
        });

        binding.nearShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedType = "nearShops";
                binding.allShops.setChecked(false);
                binding.filterbtn.setVisibility(View.GONE);
                loadMycity();
            }
        });

        binding.filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.nearShops.setChecked(false);
                binding.allShops.setChecked(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Category:").setItems(city, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected = city[i];
                        if (selected.equals("All")){
                            binding.allShops.setChecked(true);
                            LoadAllShops();
                        }
                        else {
                            shopAdapter.getFilter().filter(selected);
                        }
                    }
                }).show();
            }
        });

        return binding.getRoot();
    }

    private void loadMycity() {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String mycity = "" + snapshot.child("city").getValue();
                    Log.d("UserCity1", mycity);
                    LoadNearShops(mycity);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(mAuth.getUid()).addListenerForSingleValueEvent(valueEventListener);
    }

    private void LoadNearShops(String mycity) {

        shopsList = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopsList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Shops shops = ds.getValue(Shops.class);

                    String city = shops.getCity();
                    Log.d("SellerCity", city);
                    Log.d("UserCity", mycity);
                    //////////////////////////////////////////////////////////
                    if (city.equals(mycity)) {

                        shopsList.add(shops);
                        binding.emptyLL.setVisibility(View.GONE);

                    } else if (shopsList.size() == 0){

                        binding.emptyLL.setVisibility(View.VISIBLE);

                    }
                    else {
                    }
                }
                shopAdapter = new ShopAdapter(getContext(), shopsList, checkedType);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                binding.recycleview.setLayoutManager(layoutManager);
                binding.recycleview.setAdapter(shopAdapter);
                shopAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.orderByChild("userType").equalTo("seller").addListenerForSingleValueEvent(valueEventListener);
    }

    private void LoadAllShops() {

        shopsList = new ArrayList<>();
        binding.emptyLL.setVisibility(View.GONE);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Shops shops = ds.getValue(Shops.class);

                    String city = shops.getCity();
                    Log.d("LoadAllShops", city);
                    //////////////////////////////////////////////////////////

                    ///all shops
                    shopsList.add(shops);


                }
                shopAdapter = new ShopAdapter(getContext(), shopsList,checkedType);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                binding.recycleview.setLayoutManager(layoutManager);
                binding.recycleview.setAdapter(shopAdapter);
                shopAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.orderByChild("userType").equalTo("seller").addListenerForSingleValueEvent(valueEventListener);

    }



    @Override
    public void onStart() {
        super.onStart();
       checkedType = "nearShops";
//        loadMycity();
//        binding.nearShops.setChecked(true);
//        binding.allShops.setChecked(false);
    }
}