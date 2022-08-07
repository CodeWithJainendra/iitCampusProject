package com.shivam.iitcampusproject.Fragments.Seller.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.shivam.iitcampusproject.User.UserNotificationActivity;
import com.shivam.iitcampusproject.User.UserProfileActivity;
import com.shivam.iitcampusproject.User.User_Address_Activity;
import com.shivam.iitcampusproject.databinding.FragmentUserSettingsBinding;


public class SettingsUserFragment extends Fragment {

    FragmentUserSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserSettingsBinding.inflate(inflater, container, false);

        binding.profileSettingCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UserProfileActivity.class));
            }
        });

        binding.ShippingCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), User_Address_Activity.class));
            }
        });

        binding.notificationsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UserNotificationActivity.class));
            }
        });

        return binding.getRoot();
    }
}