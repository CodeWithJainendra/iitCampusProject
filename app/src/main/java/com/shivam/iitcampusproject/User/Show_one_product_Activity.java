package com.shivam.iitcampusproject.User;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.ProductsReviewAdapter;
import com.shivam.iitcampusproject.Adapter.QuestionsAdapter;
import com.shivam.iitcampusproject.Model.ProductRating;
import com.shivam.iitcampusproject.Model.Questions;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.databinding.ActivityShowOneProductBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Show_one_product_Activity extends AppCompatActivity {

    ActivityShowOneProductBinding binding;

    private double cost = 0;
    private double finalcost = 0;
    private int Quantity = 0;

    boolean isInMyFavorite = false;
    private  String price,username,userImage;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference favouriteRef;

    Dialog dialog;
    ProgressDialog progressDialog;

    boolean isSeller = false;

    private String product_image, discount_price, discountAvailable, original_price,product_name,quantity,product_dec,
    category,discount_dec,Selleruid,Productid;

    private String SellerCity, UserCity, SellerDeliveryFee;

    private ArrayList<ProductRating> productRatingList;
    ProductsReviewAdapter productsReviewAdapter;

    private ArrayList<Questions> questionsList;
    QuestionsAdapter questionsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowOneProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        product_image = getIntent().getStringExtra("product_image");
        discount_price = getIntent().getStringExtra("discount_price");
        discountAvailable = getIntent().getStringExtra("discountAvailable");
        original_price = getIntent().getStringExtra("original_price");
        product_name = getIntent().getStringExtra("product_name");
        quantity = getIntent().getStringExtra("quantity");
        product_dec = getIntent().getStringExtra("product_dec");
        category = getIntent().getStringExtra("category");
        discount_dec = getIntent().getStringExtra("discount_dec");
        Selleruid = getIntent().getStringExtra("Selleruid");
        Productid = getIntent().getStringExtra("Productid");



        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        LoadData();
        LoadSellerCity();
        LoadUserCity();
        checkIsFavourite();
        LoadProductReviews();
        LoadProductRating();
        CountReviews();
        LoadQuestions();
        CountQuestions();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.FavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(Show_one_product_Activity.this, "You're not Logged In", Toast.LENGTH_SHORT).show();

                } else {

                    if (isInMyFavorite) {
                        //in Favorite
                        removeFromFavourites();


                    } else {
                        //not in Favorite
                        addToFavourites();
                    }
                }
            }
        });


        if (discountAvailable.equals("true")) {
            price = discount_price;

        } else {

            price = original_price;

        }

        cost = Double.parseDouble(price);
        finalcost = Double.parseDouble(price);
        Quantity = 1;

        binding.cartPrice.setText("Rs "+finalcost);

        binding.btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalcost = finalcost + cost;
                Quantity++;



                    binding.cartPrice.setText("Rs. " + finalcost);
                    binding.quantitytv.setText("" + Quantity);


            }
        });

        binding.btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Quantity > 1) {
                    finalcost = finalcost - cost;
                    Quantity--;

                    binding.cartPrice.setText("Rs. " + finalcost);
                    binding.quantitytv.setText("" + Quantity);
                }
            }
        });

        binding.purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                dialogBox("You Need To Pay "+finalcost+" +Shipping Fee", "Confirm Order");
            }
        });

        binding.addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadAskQuestionDialog();
            }
        });


    }

    private void CountQuestions() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Selleruid).child("Products");
        reference.child(Productid).child("Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                binding.countQuestions.setText("["+String.valueOf(count)+"]");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadQuestions() {

        questionsList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(Selleruid).child("Products").child(Productid).child("Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionsList.clear();

                if (snapshot.exists()){

                    for (DataSnapshot ds : snapshot.getChildren()){

                        Questions questions = ds.getValue(Questions.class);
                        questionsList.add(questions);

                    }
                    questionsAdapter = new QuestionsAdapter(Show_one_product_Activity.this,questionsList, isSeller);
                    binding.recycleviewQuestion.setAdapter(questionsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Show_one_product_Activity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    String question="";
    private void LoadAskQuestionDialog() {

        dialog = new Dialog(Show_one_product_Activity.this);
        dialog.setContentView(R.layout.dialog_ask_question);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.edit_text);
        TextView btnSubmit = dialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                question = editText.getText().toString().trim();

                if (TextUtils.isEmpty(question)){
                    editText.setError("Please enter question..");
                }
                else {

                    progressDialog.setMessage("Submit Question");
                    progressDialog.show();

                    long timestamp = System.currentTimeMillis();

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("username",username);
                    hashMap.put("question",question);
                    hashMap.put("answer","Empty");
                    hashMap.put("Selleruid",Selleruid);
                    hashMap.put("productId",Productid);
                    hashMap.put("userImage",userImage);
                    hashMap.put("productImage",product_image);
                    hashMap.put("timestamp",""+timestamp);
                    hashMap.put("Uid",""+mAuth.getUid());

                    DatabaseReference databaseReference1 = database.getReference("Questions").child(Selleruid);
                    databaseReference1.child(String.valueOf(timestamp)).setValue(hashMap);

                    DatabaseReference databaseReference = database.getReference("Users").child(Selleruid).child("Products").child(Productid).child("Questions");
                    databaseReference.child(String.valueOf(timestamp)).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(Show_one_product_Activity.this,"Ask Question Successfully..",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(Show_one_product_Activity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private void CountReviews() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Selleruid).child("Products");
        reference.child(Productid).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    if (snapshot.exists()){
                        long count = snapshot.getChildrenCount();
                        binding.countReviews.setText("["+String.valueOf(count)+"]");
                        binding.nameTv.setText("Product");
                        binding.name1Tv.setText("Reviews");
                    }
                    else {
                        binding.countReviews.setText("["+"0"+"]");

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private float RatingSum = 0;
    private void LoadProductRating() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Selleruid).child("Products");
        reference.child(Productid).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    RatingSum = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                        Log.d("RATE",String.valueOf(rating));
                        RatingSum = RatingSum + rating;
                    }

                    long numberOfReviews = snapshot.getChildrenCount();
                    float avgRating = RatingSum / numberOfReviews;

                    binding.ratingBar.setRating(avgRating);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private float ratingSum = 0;
    private void LoadProductReviews() {

        productRatingList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(Selleruid).child("Products").child(Productid).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productRatingList.clear();
                ratingSum = 0;

                if (snapshot.exists()){

                    for (DataSnapshot ds : snapshot.getChildren()){

                        float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                        ratingSum = ratingSum + rating;

                        ProductRating productRating = ds.getValue(ProductRating.class);
                        productRatingList.add(productRating);

                    }
                    productsReviewAdapter = new ProductsReviewAdapter(Show_one_product_Activity.this,productRatingList);
                    binding.recycleview.setAdapter(productsReviewAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Show_one_product_Activity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToFavourites() {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(Show_one_product_Activity.this, "You're not Logged In", Toast.LENGTH_SHORT).show();
        } else {
            long timestamp = System.currentTimeMillis();

            HashMap<String, Object> hashMap = new HashMap<>();

            if (discountAvailable.equals("true")){

                hashMap.put("productId", Productid);
                hashMap.put("uid", Selleruid);
                hashMap.put("category", category);
                hashMap.put("product_name", product_name);
                hashMap.put("timestamp", ""+timestamp);
                hashMap.put("product_image", product_image);
                hashMap.put("discount_price", discount_price);
                hashMap.put("discountAvailable", discountAvailable);
                hashMap.put("original_price", original_price);
                hashMap.put("product_name", product_name);
                hashMap.put("quantity", quantity);
                hashMap.put("product_dec", product_dec);
                hashMap.put("discount_dec", discount_dec);
            }
            else {
                hashMap.put("productId", Productid);
                hashMap.put("uid", Selleruid);
                hashMap.put("category", category);
                hashMap.put("product_name", product_name);
                hashMap.put("timestamp",""+timestamp);
                hashMap.put("product_image", product_image);
                hashMap.put("discount_price", discount_price);
                hashMap.put("discountAvailable", discountAvailable);
                hashMap.put("original_price", original_price);
                hashMap.put("product_name", product_name);
                hashMap.put("quantity", quantity);
                hashMap.put("product_dec", product_dec);
                hashMap.put("discount_dec", "empty");
            }



            favouriteRef = database.getReference("Users");
            favouriteRef.child(FirebaseAuth.getInstance().getUid()).child("Favourites").child(Productid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Show_one_product_Activity.this, "Added To Favourites", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Show_one_product_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void removeFromFavourites() {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(Show_one_product_Activity.this, "You're not Logged In", Toast.LENGTH_SHORT).show();
        } else {

            favouriteRef = database.getReference("Users");
            favouriteRef.child(FirebaseAuth.getInstance().getUid()).child("Favourites").child(Productid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Show_one_product_Activity.this, "Removed from Favourites", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Show_one_product_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkIsFavourite() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(Show_one_product_Activity.this, "You're not Logged In", Toast.LENGTH_SHORT).show();

        } else {

            favouriteRef = database.getReference("Users");
            favouriteRef.child(mAuth.getUid()).child("Favourites").child(Productid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    isInMyFavorite = snapshot.exists();

                    if (isInMyFavorite) {

                        binding.FavouriteBtn.setImageResource(R.drawable.ic_fav_color);

                    } else {

                        binding.FavouriteBtn.setImageResource(R.drawable.ic_fav_shadow);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void LoadUserCity() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    UserCity = "" + snapshot.child("city").getValue();
                    username = ""+snapshot.child("username").getValue();
                    userImage = ""+snapshot.child("photoUrl").getValue();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dialogBox(String s, String title) {

        AlertDialog alertDialog = new AlertDialog.Builder(Show_one_product_Activity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(s);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                checkUserAddress();
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

    private void checkUserAddress() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String deafault_address = "" + snapshot.child("deafault_address").getValue();
                    String mobile = ""+snapshot.child("mobile").getValue();
                    String city = ""+snapshot.child("city").getValue();

                    if (deafault_address.equals("null")) {

                        Toast.makeText(Show_one_product_Activity.this, "Please Select your default Address from Settings", Toast.LENGTH_SHORT).show();

                    } else if (mobile.equals("Empty")){

                        Toast.makeText(Show_one_product_Activity.this, "Please Update Your Mobile Number From Settings", Toast.LENGTH_SHORT).show();

                    }else if (city.equals("Empty")){

                        Toast.makeText(Show_one_product_Activity.this, "Please Update Your City Number From Settings", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        ConfirmOrder();
                    }


                } else {

                    Toast.makeText(Show_one_product_Activity.this, "Please add Your Shipping Address Settings", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Show_one_product_Activity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int itemId = 1;
    double totalPrice;

    private void ConfirmOrder() {

        itemId++;


        String timestamp = "" + System.currentTimeMillis();
        String quantitytv = binding.quantitytv.getText().toString().trim();


        if (UserCity.equals(SellerCity)) {

            totalPrice = Double.valueOf(finalcost) + Double.valueOf("50");



            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Item_PID", Productid);
            hashMap.put("Item_Name", product_name);
            hashMap.put("Item_Price_Each", price);
            hashMap.put("Item_Price", String.valueOf(finalcost));
            hashMap.put("Item_Quantity", quantitytv);
            hashMap.put("Item_Image", product_image);
            hashMap.put("timestamp", timestamp);
            hashMap.put("SellerUid", Selleruid);
            hashMap.put("productId", Productid);
            hashMap.put("DeliveryFee", "50");// this is random price
            hashMap.put("TotalPrice", "" + totalPrice);
            hashMap.put("status", "inCart");

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference1.child(FirebaseAuth.getInstance().getUid()).child("Cart").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Show_one_product_Activity.this, "Item Added....", Toast.LENGTH_SHORT).show();
                    binding.cartPrice.setText(String.valueOf(finalcost));
                    binding.quantitytv.setText("1");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Show_one_product_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            totalPrice = Double.valueOf(finalcost) + Double.valueOf(SellerDeliveryFee);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Item_PID", Productid);
            hashMap.put("Item_Name", product_name);
            hashMap.put("Item_Price_Each", price);
            hashMap.put("Item_Price",String.valueOf(finalcost));
            hashMap.put("Item_Quantity", quantitytv);
            hashMap.put("Item_Image", product_image);
            hashMap.put("timestamp", timestamp);
            hashMap.put("SellerUid", Selleruid);
            hashMap.put("productId", Productid);
            hashMap.put("DeliveryFee", SellerDeliveryFee);
            hashMap.put("TotalPrice", "" + totalPrice);
            hashMap.put("status", "inCart");

            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference2.child(FirebaseAuth.getInstance().getUid()).child("Cart").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Show_one_product_Activity.this, "Item Added....", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Show_one_product_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void LoadSellerCity() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(Selleruid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SellerCity = "" + snapshot.child("city").getValue();
                    SellerDeliveryFee = "" + snapshot.child("delivery_fee").getValue();
                    String ShopName = ""+snapshot.child("shopName").getValue();

                    binding.shopNameTv.setText(ShopName);
                    binding.storeLocation.setText("("+SellerCity+")");
                    binding.sellerLocation.setText(SellerCity+" Area:");
                    binding.shippingFee.setText("Rs "+SellerDeliveryFee+".00");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadData() {


        if (discountAvailable.equals("true")) {

            binding.price.setVisibility(View.VISIBLE);
            binding.discountnote.setVisibility(View.VISIBLE);
            binding.originalPrice.setPaintFlags(binding.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            binding.price.setVisibility(View.GONE);
            binding.discountnote.setVisibility(View.GONE);
            binding.originalPrice.setPaintFlags(0);
        }
        try {
            Picasso.get().load(product_image).placeholder(R.drawable.picture).into(binding.productImage);

        } catch (Exception e) {
            binding.productImage.setImageResource(R.drawable.picture);

        }

        binding.price.setText("Rs. "+discount_price);
        binding.originalPrice.setText("Rs. "+original_price);
        binding.productName.setText(product_name);
        binding.productQuantity.setText("Quantity "+quantity);
        binding.productDec.setText(product_dec);
        binding.category.setText(category);
        binding.discountnote.setText("-"+discount_dec);

        //set RatingBar

    }
}