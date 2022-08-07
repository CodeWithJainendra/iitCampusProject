package com.shivam.iitcampusproject.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.UserShowProducts;
import com.shivam.iitcampusproject.Model.Products;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.databinding.ActivityShowShopProductsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Show_Shop_Products_Activity extends AppCompatActivity {

    ActivityShowShopProductsBinding binding;

    private String Shopuid,rate;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    private ArrayList<Products> productsList;
    private UserShowProducts userShowProductsadapter;
    String Shopphone,shopname,buyer_address;

    public String delivery_fee,main_Address;
    String image;

    double buyerLatitude,buyerLongitude;

    Double latitude,longitude;
    public String deafault_address,mobile_number;

    private ProgressDialog progressDialog;

    DatabaseReference reference;

    public static final String[] categories1 = {

            "Beverages",
            "Bakery",
            "Chilled",
            "Meats",
            "Grocery",
            "HomeWare",
            "Medicine",
            "PetCare",
            "Vegetables",
            "Fruits",
            "Crafts & Arts",
            "Clothes",
            "Foods",
            "Others"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowShopProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Shopuid = getIntent().getStringExtra("uid");
        rate = getIntent().getStringExtra("rate");


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        loadShopDetails();
        loadShopProducts();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.CallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialNumber();
            }
        });

        binding.MapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
//                String shop_google_address = "https://maps.google.com/maps?saddr="+latitude+","+longitude;
//                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(shop_google_address));
//                startActivity(intent);
            }
        });

        binding.searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    userShowProductsadapter.getFilter().filter(charSequence);
                }
                catch (Exception e)
                {
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
                loadShopProducts();
            }
        });

        binding.beveragesBtn.setOnClickListener(new View.OnClickListener() {
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

    }

    private void LoadByCategoryProducts(String cat) {
        productsList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(Shopuid).child("Products").orderByChild("category").equalTo(cat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Products products = ds.getValue(Products.class);

                    String d = products.getCategory();
                    Log.d("TAGS",d);
                    productsList.add(products);
                }
                userShowProductsadapter = new UserShowProducts(Show_Shop_Products_Activity.this,productsList);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                binding.recycleview.setLayoutManager(staggeredGridLayoutManager);
                binding.recycleview.setAdapter(userShowProductsadapter);
                userShowProductsadapter.notifyDataSetChanged();
                binding.recycleview.setHasFixedSize(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShopProducts() {

        productsList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(Shopuid).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Products products = ds.getValue(Products.class);
                    productsList.add(products);
                }
                userShowProductsadapter = new UserShowProducts(Show_Shop_Products_Activity.this,productsList);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                binding.recycleview.setLayoutManager(staggeredGridLayoutManager);
                binding.recycleview.setAdapter(userShowProductsadapter);
                userShowProductsadapter.notifyDataSetChanged();
                binding.recycleview.setHasFixedSize(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShopDetails() {

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(Shopuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username =""+snapshot.child("username").getValue();
                shopname = ""+snapshot.child("shopName").getValue();
                String email = ""+snapshot.child("email").getValue();
                String phone = ""+snapshot.child("mobile").getValue();
                delivery_fee = ""+snapshot.child("delivery_fee").getValue();
                String image = ""+snapshot.child("photoUrl").getValue();
                String address = ""+snapshot.child("address").getValue();
                String shopOpen = ""+snapshot.child("Shop_Status").getValue();
                latitude = Double.valueOf(""+snapshot.child("latitude").getValue());
                longitude = Double.valueOf(""+snapshot.child("longitude").getValue());
                Shopphone = ""+snapshot.child("mobile").getValue();

                Log.d("PHONETAG",Shopphone);

                binding.shopName.setText(shopname);
                binding.shopEmail.setText(email);
                binding.phone.setText(phone);
                binding.address.setText(address);
                binding.deliveryFee.setText("Delivery Fee: "+delivery_fee);

                float rating = Float.parseFloat(rate);

                binding.ratingBar.setRating(rating);

                if (shopOpen.equals("Open")){
                    binding.openclose.setText("Open");
                }else {
                    binding.openclose.setText("Closed");
                }

                try {
                    Picasso.get().load(image).into(binding.sellerImage);

                }catch (Exception e){

                    binding.sellerImage.setImageResource(R.drawable.profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DialNumber() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(Shopphone))));
        Toast.makeText(this,""+Shopphone,Toast.LENGTH_SHORT).show();
    }
}