package com.shivam.iitcampusproject.Fragments.Seller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.Seller.SellerShowOrdersActivity;
import com.shivam.iitcampusproject.Seller.SellerShowQuestionsActivity;
import com.shivam.iitcampusproject.Seller.Seller_Show_Products_Activity;
import com.shivam.iitcampusproject.Seller.Seller_add_Product_Activity;
import com.shivam.iitcampusproject.SplashActivity;
import com.shivam.iitcampusproject.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment implements View.OnClickListener {

    FragmentHomeBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        LoadQuesionCount();

        binding.addProduct.setOnClickListener(this::onClick);
        binding.showProducts.setOnClickListener(this::onClick);
        binding.orders.setOnClickListener(this::onClick);
        binding.questions.setOnClickListener(this::onClick);

        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    //ubah background
                    int transit_bg = getResources().getColor(R.color.black);
                    binding.mainRl.setBackgroundColor(transit_bg);

                    //ubah tv_title
                    int transit_tv_title = getResources().getColor(R.color.white);
                    binding.tvTitle.setTextColor(transit_tv_title);


                    //ubah tv_title
                    int transit_tv_title2 = getResources().getColor(R.color.white);
                    binding.textview.setTextColor(transit_tv_title2);

                    //ubah toolbar
                    Drawable transit_toolbar = getResources().getDrawable(R.drawable.bg_toolbar_black);
                    binding.toolbar.setBackground(transit_toolbar);

                } else {

                    //ubah background
                    int transit_bg = getResources().getColor(R.color.white);
                    binding.mainRl.setBackgroundColor(transit_bg);

                    //hello tv_title
                    int transit_tv_title = getResources().getColor(R.color.black);
                    binding.tvTitle.setTextColor(transit_tv_title);

                    //ubah tv_title
                    int transit_tv_title2 = getResources().getColor(R.color.black);
                    binding.textview.setTextColor(transit_tv_title2);


                    //toolbar
                    Drawable transit_toolbar = getResources().getDrawable(R.drawable.bg_toolbar);
                    binding.toolbar.setBackground(transit_toolbar);


                }
            }
        });

//        binding.searchEt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.searchEt.setFocusable(true);
//            }
//        });

//        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertMessageLogout();
//            }
//        });

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addProduct:

                Intent intent = new Intent(getActivity(), Seller_add_Product_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.showProducts:

                Intent intent1 = new Intent(getActivity(), Seller_Show_Products_Activity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                break;

            case R.id.orders:

                Intent intent2 = new Intent(getActivity(), SellerShowOrdersActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                break;

            case R.id.questions:

                Intent intent3 = new Intent(getActivity(), SellerShowQuestionsActivity.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent3);
                break;



        }
    }

    @Override
    public void onStart() {
        LoadUserdata();
        CountProducts();
        CountInProgressOrders();
        CountCompletedOrders();
        CountCancelledOrders();
        LoadRatingCount();
        LoadShopRating();
        LoadQuesionCount();
        super.onStart();
    }

    private void LoadQuesionCount() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Questions").child(mAuth.getUid());
        reference.orderByChild("answer").equalTo("Empty").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Long count = snapshot.getChildrenCount();
                        binding.questionsTextView.setText(String.valueOf(count));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private float ratingSum = 0;
    private float avgRating;
    private void LoadShopRating() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("Rating");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    ratingSum = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                        Log.d("RATE1", String.valueOf(rating));
                        ratingSum = ratingSum + rating;
                    }

                    long numberOfReviews = snapshot.getChildrenCount();
                    avgRating = ratingSum / numberOfReviews;

                    binding.ratingBar.setRating(avgRating);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadRatingCount() {
        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Rating");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getChildrenCount();
                binding.ratingBadgeTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CountCancelledOrders() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders");
        databaseReference.orderByChild("orderStatus").equalTo("Cancelled").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getChildrenCount();

                binding.ordersCancelledCompletedBadgeTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CountCompletedOrders() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders");
        databaseReference.orderByChild("orderStatus").equalTo("Completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getChildrenCount();

                binding.ordersCompletedBadgeTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CountInProgressOrders() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders");
        databaseReference.orderByChild("orderStatus").equalTo("InProgress").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getChildrenCount();

                binding.ordersInprogressBadgeTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CountProducts() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Products");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getChildrenCount();

                binding.productBadgeTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadUserdata() {

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String image = "" + snapshot.child("photoUrl").getValue();
                    String shopname = "" + snapshot.child("shopName").getValue();
                    String email = "" + snapshot.child("email").getValue();

                    binding.shopName.setText(shopname);
                    //binding.useremail.setText(email);

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(binding.profileIv);

                    } catch (Exception e) {

                        binding.profileIv.setImageResource(R.drawable.account);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AlertMessageLogout() {

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        dialog.setTitle("Task Status")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Do You want Logout")
                .setPositiveButton("Ok", (dialog1, which) -> LogOut())
                .setNegativeButton("Cancel", (dialoginterface, i) -> dialoginterface.cancel()).show();

    }

    private void LogOut() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), SplashActivity.class));
        getActivity().finish();
    }
}