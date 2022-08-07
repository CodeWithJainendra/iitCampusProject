package com.shivam.iitcampusproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class SlideActivity extends AppCompatActivity {

   public static ViewPager viewPager;
    SlideViewPagerAdapter adapter;
    LinearLayout dotsLayout;
    TextView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewPager=findViewById(R.id.viewpager);
        adapter=new SlideViewPagerAdapter(this);



        viewPager.setAdapter(adapter);
        if (isOpenAlread())
        {
            Intent intent=new Intent(SlideActivity.this,InToScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            SharedPreferences.Editor editor=getSharedPreferences("slide",MODE_PRIVATE).edit();
            editor.putBoolean("slide",true);
            editor.commit();
        }

    }


    private boolean isOpenAlread() {

        SharedPreferences sharedPreferences=getSharedPreferences("slide",MODE_PRIVATE);
        boolean result=sharedPreferences.getBoolean("slide",false);
        return result;

    }
}
