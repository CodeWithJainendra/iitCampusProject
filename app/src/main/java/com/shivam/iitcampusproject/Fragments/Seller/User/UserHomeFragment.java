package com.shivam.iitcampusproject.Fragments.Seller.User;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.UserHomeShowProducts;
import com.shivam.iitcampusproject.Model.Products;
import com.shivam.iitcampusproject.databinding.FragmentUserHomeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserHomeFragment extends Fragment {

    FragmentUserHomeBinding binding;

    private ArrayList<Products> productsList;
    private UserHomeShowProducts adapteruserHomeShowProducts;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    boolean btnClicked = false;
    Products products;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserHomeBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.loadingTv.setVisibility(View.VISIBLE);
        LoadAllProducts();
        LoadSliderImages();


        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapteruserHomeShowProducts.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.allProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadAllProducts();


            }
        });

        binding.beveragesBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Beverages");


            }
        });

        binding.bakeryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Bakery");
            }
        });

        binding.chilledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Chilled");
            }
        });

        binding.meatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Meats");
            }
        });

        binding.groceryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Grocery");
            }
        });

        binding.homewareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("HomeWare");
            }
        });

        binding.medicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Medicine");
            }
        });

        binding.petcareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("PetCare");
            }
        });

        binding.vegetablesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Vegetables");
            }
        });

        binding.fruitsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Fruits");
            }
        });

        binding.craftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Crafts & Arts");
            }
        });

        binding.clothesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Clothes");
            }
        });

        binding.foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Foods");
            }
        });

        binding.othersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadByCategoryProducts("Others");
            }
        });


        return binding.getRoot();
    }

    private void LoadSliderImages() {

        List<SlideModel> slideImages = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banners");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slideImages.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String url = "" + ds.child("Imagelink").getValue();

                    slideImages.add(new SlideModel(url));
                    binding.slider.setImageList(slideImages, true);
                    binding.loadingTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        List<SlideModel> slideModels = new ArrayList<>();
//        slideModels.add(new SlideModel(R.drawable.image1));
//        slideModels.add(new SlideModel(R.drawable.illustration));
//        slideModels.add(new SlideModel(R.drawable.img_ilustrasi));
//        binding.slider.setImageList(slideModels,true);
    }

    private void LoadByCategoryProducts(String cat) {


        productsList = new ArrayList<>();
        binding.categoryTv.setText("Showing All " + cat);

        databaseReference = database.getReference("Products");
        databaseReference.orderByChild("category").equalTo(cat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    products = ds.getValue(Products.class);

                    String d = products.getCategory();
                    Log.d("TAGS", d);

                    //shuffle products

                    Collections.shuffle(productsList);
                    productsList.add(products);
                }
                adapteruserHomeShowProducts = new UserHomeShowProducts(getActivity(), productsList);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                binding.recycleview.setLayoutManager(staggeredGridLayoutManager);
                binding.recycleview.setAdapter(adapteruserHomeShowProducts);
                adapteruserHomeShowProducts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void LoadAllProducts() {

        productsList = new ArrayList<>();
        binding.categoryTv.setText("Showing All Category");
        databaseReference = database.getReference("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    products = ds.getValue(Products.class);

                    String d = products.getCategory();
                    Log.d("TAGS", d);


                    //shuffle products
                    Collections.shuffle(productsList);
                    productsList.add(products);
                }
                adapteruserHomeShowProducts = new UserHomeShowProducts(getActivity(), productsList);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                binding.recycleview.setLayoutManager(staggeredGridLayoutManager);
                binding.recycleview.setAdapter(adapteruserHomeShowProducts);
                adapteruserHomeShowProducts.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}