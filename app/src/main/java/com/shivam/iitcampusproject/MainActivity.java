package com.shivam.iitcampusproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {


    LottieAnimationView lottie,mainname;
    SharedPreferences onBoardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lottie=findViewById(R.id.lottieone);
        mainname=findViewById(R.id.appname);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i =new Intent(getApplicationContext(),SlideActivity.class);
                startActivity(i);
                finish();




            }
        },11000);
    }
}