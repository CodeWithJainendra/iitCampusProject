package com.shivam.iitcampusproject.Seller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shivam.iitcampusproject.Constants;
import com.shivam.iitcampusproject.databinding.ActivitySellerNotificationsBinding;

public class SellerNotificationsActivity extends AppCompatActivity {

    ActivitySellerNotificationsBinding binding;

    private static final String enabledMessage = "Notifications are enabled";
    private static final String disabledMessage = "Notifications are disabled";

    private boolean isChecked = false;

    private FirebaseAuth mAuth;

    private SharedPreferences sp_buyer;
    private SharedPreferences.Editor spBuyerEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        sp_buyer = getSharedPreferences("SELLER_SETTINGS_SP", MODE_PRIVATE);
        //check last selected option: true/false
        isChecked = sp_buyer.getBoolean("SELLER_FCM_ENABLED", false);
        binding.fcmSwitch.setChecked(isChecked);
        if (isChecked) {
            //was enabled
            binding.notificationStatusTv.setText(enabledMessage);
        } else {
            binding.notificationStatusTv.setText(disabledMessage);
        }

        binding.fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //enable notification
                    subscribeToTopic();

                } else {
                    //disable notification
                    unSubscribeToTopic();

                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //subscribed Successfully
                //save settings ins shared preferences
                spBuyerEditor = sp_buyer.edit();
                spBuyerEditor.putBoolean("SELLER_FCM_ENABLED", true);
                spBuyerEditor.apply();
                Toast.makeText(getApplicationContext(), "" + enabledMessage, Toast.LENGTH_SHORT).show();
                binding.notificationStatusTv.setText(enabledMessage);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //subscribed failed
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unSubscribeToTopic() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Unsubscribe
                //save settings ins shared preferences
                spBuyerEditor = sp_buyer.edit();
                spBuyerEditor.putBoolean("SELLER_FCM_ENABLED", false);
                spBuyerEditor.apply();
                Toast.makeText(getApplicationContext(), "" + disabledMessage, Toast.LENGTH_SHORT).show();
                binding.notificationStatusTv.setText(disabledMessage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Unsubscribe Failed
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}