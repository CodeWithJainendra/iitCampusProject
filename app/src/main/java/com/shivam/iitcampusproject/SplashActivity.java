package com.shivam.iitcampusproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Seller.SellerHomeActivity;
import com.shivam.iitcampusproject.User.UserHomeActivity;
import com.shivam.iitcampusproject.admin.AdminHomeActivity;
import com.shivam.iitcampusproject.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    SharedPreferences onBordingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIsFirstTime();
              //  checkUser();
            }
        }, 11000);
    }

    private void checkIsFirstTime() {
        onBordingScreen = getSharedPreferences("onBordingScreen",MODE_PRIVATE);
        boolean isFirstTime = onBordingScreen.getBoolean("firstTime",true);

        if (isFirstTime){

            SharedPreferences.Editor editor = onBordingScreen.edit();
            editor.putBoolean("firstTime",false);
            editor.commit();

            Intent intent = new Intent(getApplicationContext(), InToScreenActivity.class);
            startActivity(intent);
            finish();


        }
        else {

            checkUser();
        }
    }

    private void checkUser() {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {

            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {
            DatabaseReference reference = database.getReference("Users");
            reference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String userType = "" + snapshot.child("userType").getValue();
                    String approval = "" + snapshot.child("Is_approval").getValue();


                    if (userType.equals("user")) {

                        startActivity(new Intent(SplashActivity.this, UserHomeActivity.class));
                        finish();

                    } else if (userType.equals("seller")) {

                        if (approval.equals("approved")) {

                            startActivity(new Intent(SplashActivity.this, SellerHomeActivity.class));
                            finish();

                        }
                        else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                    else {
                        startActivity(new Intent(SplashActivity.this, AdminHomeActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}