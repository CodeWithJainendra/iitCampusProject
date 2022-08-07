package com.shivam.iitcampusproject.Seller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.shivam.iitcampusproject.Fragments.Seller.CategoryFragment;
import com.shivam.iitcampusproject.Fragments.Seller.HomeFragment;
import com.shivam.iitcampusproject.Fragments.Seller.SettingsFragment;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.databinding.ActivitySellerHomeBinding;

public class SellerHomeActivity extends AppCompatActivity  {

    ActivitySellerHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //transparent status bar
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bottomMenu();

//        binding.BottomView.setOnNavigationItemSelectedListener(navListner);
        binding.BottomView.setItemSelected(R.id.nav_home,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }

    private void bottomMenu() {

        binding.BottomView.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment selectedFragment = null;
                switch (i){
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.nav_category:
                        selectedFragment = new CategoryFragment();
                        break;

                    case R.id.nav_settings:
                        selectedFragment = new SettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }
        });
    }

//    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment selectedFragment = null;
//
//            switch (item.getItemId()){
//                case R.id.nav_home:
//                    selectedFragment = new HomeFragment();
//                    break;
//
//                case R.id.nav_category:
//                    selectedFragment = new CategoryFragment();
//                    break;
//
//                case R.id.nav_settings:
//                    selectedFragment = new SettingsFragment();
//                    break;
//            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
//
//            return true;
//        }
//    };
}