package com.shivam.iitcampusproject.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Fragments.Seller.User.CartFragment;
import com.shivam.iitcampusproject.Fragments.Seller.User.FavouriteFragment;
import com.shivam.iitcampusproject.Fragments.Seller.User.SettingsUserFragment;
import com.shivam.iitcampusproject.Fragments.Seller.User.ShopFragment;
import com.shivam.iitcampusproject.Fragments.Seller.User.ShowOrdersFragment;
import com.shivam.iitcampusproject.Fragments.Seller.User.UserHomeFragment;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.SplashActivity;
import com.shivam.iitcampusproject.databinding.ActivityUserHomeBinding;
import com.squareup.picasso.Picasso;

public class UserHomeActivity extends AppCompatActivity {

    ActivityUserHomeBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

   // int numCart=0;
    ActionBarDrawerToggle toggle;
    static final float END_SCALE = 0.7f;

    ImageView nav_profile;
    TextView navBar_name,navBar_id;

    int numCart=0;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        View headerView=binding.navigationView.getHeaderView( 0 );
        navBar_name=headerView.findViewById( R.id.navBar_name );
        nav_profile=headerView.findViewById( R.id.nav_profile );
        navBar_id=headerView.findViewById( R.id.navBar_id );

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        navigationDrawer();
        LoadNavigationData();
        LoadCartData();


        // openFragmentDefault(new UserHomeFragment());
        openFragmentDefault(new UserHomeFragment());

       if(numCart==0){ binding.cartBadgeTextView.setVisibility(View.GONE); }else{ binding.cartBadgeTextView.setText(numCart+""); }
        binding.cartIcon.setOnClickListener(v -> { openFragment(new CartFragment()); });

    }

    public void LoadCartData() {
        databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Cart");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                numCart=0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    numCart++;

                }

                if(numCart==0){
                    binding.cartBadgeTextView.setVisibility(View.GONE);
                }else{
                    binding.cartBadgeTextView.setVisibility(View.VISIBLE);
                    binding.cartBadgeTextView.setText(numCart+"");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadNavigationData() {
        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    String image = ""+snapshot.child("photoUrl").getValue();
                    String useremail = ""+snapshot.child("email").getValue();
                    String id = ""+mAuth.getUid();

                    navBar_name.setText(useremail);
                    navBar_id.setText(id);

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(nav_profile);

                    }catch (Exception e){

                        nav_profile.setImageResource(R.drawable.profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void navigationDrawer() {

       binding.navigationView.bringToFront();
        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    binding.navigationView.setCheckedItem(R.id.nav_home);
                   binding.drawerLayout.closeDrawer(GravityCompat.START);
                    openFragment(new UserHomeFragment());
                    break;


                case R.id.nav_shop:
                    binding.navigationView.setCheckedItem(R.id.nav_shop);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    openFragment(new ShopFragment());
                    break;

                case R.id.nav_settings:
                    binding.navigationView.setCheckedItem(R.id.nav_shop);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    openFragment(new SettingsUserFragment());
                    break;

                case R.id.nav_orders:
                    binding.navigationView.setCheckedItem(R.id.nav_orders);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    openFragment(new ShowOrdersFragment());
                    break;

                case R.id.nav_favourites:
                    binding.navigationView.setCheckedItem(R.id.nav_orders);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    openFragment(new FavouriteFragment());
                    break;

                case R.id.nav_logout:
                    binding.navigationView.setCheckedItem(R.id.nav_logout);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    AlertMessageLogout();
                    break;



            }
            return false;
        });

        binding.menuIcon.setOnClickListener(v -> {
            if (binding.drawerLayout.isDrawerVisible(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            else  binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        binding.drawerLayout.setScrimColor(getResources().getColor(R.color.colorPrimaryDark));
        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //Scale the view based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                binding.container.setScaleX(offsetScale);
                binding.container.setScaleY(offsetScale);

                //Translating the view accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = binding.container.getWidth() * diffScaledOffset / 2;
                final float xTranslaiton = xOffset - xOffsetDiff;
                binding.container.setTranslationX(xTranslaiton);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        transaction.commit();
    }

    private void openFragmentDefault(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();

    }

    private void AlertMessageLogout(){

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(UserHomeActivity.this);
        dialog.setTitle( "Task Status" )
                .setIcon(R.drawable.ic_warning)
                .setMessage("Do You want Logout")
                .setPositiveButton("Ok", (dialog1, which) -> LogOut())
                .setNegativeButton("Cancel", (dialoginterface, i) -> dialoginterface.cancel()).show();

    }

    private void LogOut() {
        mAuth.signOut();
        startActivity(new Intent(UserHomeActivity.this, SplashActivity.class));finish();
    }

    @Override
    protected void onStart() {
        LoadCartData();
        super.onStart();
    }
}