package com.shivam.iitcampusproject.Seller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.SellerShowProducts;
import com.shivam.iitcampusproject.Model.Products;
import com.shivam.iitcampusproject.databinding.ActivitySellerShowProductsBinding;

import java.util.ArrayList;

public class Seller_Show_Products_Activity extends AppCompatActivity {

    ActivitySellerShowProductsBinding binding;

    private ArrayList<Products> productsList;
    private SellerShowProducts adaptersellerShowProducts;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Products products;

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
        binding = ActivitySellerShowProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Users");


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LoadAllProducts();


        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adaptersellerShowProducts.getFilter().filter(charSequence);
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

        binding.filterproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Seller_Show_Products_Activity.this);
                builder.setTitle("Choose Category:").setItems(categories1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected = categories1[i];
                        binding.categoryTv.setText(selected);
                        if (selected.equals("All")) {
                            LoadAllProducts();
                        } else {
                            loadFilterdProducts(selected);
                        }
                    }
                }).show();

            }
        });

        binding.allProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadAllProducts();
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
        binding.categoryTv.setText(cat);
        productsList = new ArrayList<>();

        databaseReference.child(mAuth.getUid()).child("Products").orderByChild("category").equalTo(cat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Products products = ds.getValue(Products.class);

                    String d = products.getCategory();
                    Log.d("TAGS", d);
                    productsList.add(products);

                }
                adaptersellerShowProducts = new SellerShowProducts(Seller_Show_Products_Activity.this, productsList);
                // StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                binding.recycleview.setLayoutManager(staggeredGridLayoutManager);
                binding.recycleview.setAdapter(adaptersellerShowProducts);
                adaptersellerShowProducts.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFilterdProducts(String selected) {

        productsList = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String productCategory = "" + ds.child("category").getValue();

                    if (selected.equals(productCategory)) {
                        Products products = ds.getValue(Products.class);
                        productsList.add(products);
                    }


                }

                adaptersellerShowProducts = new SellerShowProducts(Seller_Show_Products_Activity.this, productsList);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                binding.recycleview.setLayoutManager(staggeredGridLayoutManager);
                binding.recycleview.setAdapter(adaptersellerShowProducts);
                adaptersellerShowProducts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child(mAuth.getUid()).child("Products").addListenerForSingleValueEvent(valueEventListener);

    }

    private void LoadAllProducts() {

        productsList = new ArrayList<>();
        binding.categoryTv.setText("All Items");

        databaseReference.child(mAuth.getUid()).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    products = ds.getValue(Products.class);

//                    String d = products.getCategory();
//                    Log.d("TAGS", d);
                    productsList.add(products);
                }
                adaptersellerShowProducts = new SellerShowProducts(getApplicationContext(), productsList);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                binding.recycleview.setLayoutManager(staggeredGridLayoutManager);
                binding.recycleview.setAdapter(adaptersellerShowProducts);
                adaptersellerShowProducts.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Seller_Show_Products_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}