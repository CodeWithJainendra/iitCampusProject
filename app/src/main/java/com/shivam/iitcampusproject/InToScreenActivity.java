package com.shivam.iitcampusproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.shivam.iitcampusproject.databinding.ActivityInToScreenBinding;

public class InToScreenActivity extends AppCompatActivity {

    ActivityInToScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInToScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        TapTargetView.showFor(this, TapTarget.forView(binding.btnGetStarted,"")
        .outerCircleAlpha(0f)
        .targetCircleColor(R.color.white)
        .titleTextSize(10)
        .targetCircleColor(R.color.white)
        .descriptionTextSize(10)
        .descriptionTextColor(R.color.black)
        .textColor(R.color.black)
        .textTypeface(Typeface.SANS_SERIF)
        .dimColor(R.color.black)
        .cancelable(true)
        .tintTarget(true)
        .transparentTarget(true)
        .targetRadius(30),
                new TapTargetView.Listener(){

                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        Intent i = new Intent(InToScreenActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

        binding.btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InToScreenActivity.this,LoginActivity.class));
                finish();
            }
        });


    }
}