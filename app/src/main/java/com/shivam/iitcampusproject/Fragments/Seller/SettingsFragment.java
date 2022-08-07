package com.shivam.iitcampusproject.Fragments.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.Seller.SellerNotificationsActivity;
import com.shivam.iitcampusproject.Seller.Seller_Profile_Activity;
import com.shivam.iitcampusproject.SplashActivity;
import com.shivam.iitcampusproject.databinding.FragmentSettingsBinding;


public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();

        binding.BuyerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Seller_Profile_Activity.class));
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertMessageLogout();
            }
        });

        binding.notificationsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SellerNotificationsActivity.class));
            }
        });


        return binding.getRoot();
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