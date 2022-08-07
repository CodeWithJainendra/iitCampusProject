package com.shivam.iitcampusproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.SplashActivity;
import com.shivam.iitcampusproject.databinding.ActivityAdminHomeBinding;

public class AdminHomeActivity extends AppCompatActivity {

    ActivityAdminHomeBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.sellerReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, SellerRequestsActivity.class));
               // finish();
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertMessageLogout();

            }
        });

        binding.sellerAddImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this,adminAddBannersActivity.class));
            }
        });
    }


    private void AlertMessageLogout() {

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AdminHomeActivity.this);
        dialog.setTitle("Task Status")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Admin, You want Logout ?")
                .setPositiveButton("Ok", (dialog1, which) -> LogOut())
                .setNegativeButton("Cancel", (dialoginterface, i) -> dialoginterface.cancel()).show();

    }

    private void LogOut() {
        mAuth.signOut();
        startActivity(new Intent(AdminHomeActivity.this, SplashActivity.class));
        finish();
    }
}