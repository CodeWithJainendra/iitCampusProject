package com.shivam.iitcampusproject.User;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.databinding.ActivityUserAddressBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class User_Address_Activity extends AppCompatActivity {

    ActivityUserAddressBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String address;

    private LocationManager locationManager;

    private double latitude, longitude;

    private String main_address, second_address;
    private String  default_address = "no";
    FusedLocationProviderClient fusedLocationProviderClient;

    private static final int LOCATION_REQUEST_CODE = 100;
    private String[] locationPermissions;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        checkDefaultAddressCheckBox();

        binding.radioMain.setOnClickListener(view -> {

            binding.radioSecond.setChecked(false);
            binding.linaer.setVisibility(View.VISIBLE);

            binding.addressSecond.setVisibility(View.GONE);


            binding.maingpsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ActivityCompat.checkSelfPermission(User_Address_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        showLocation();

                    else
                        ActivityCompat.requestPermissions(User_Address_Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);
                    locationEnabled();



//                    getLocation();
                }
            });

        });

        binding.radioSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.addressSecond.setVisibility(View.VISIBLE);
                binding.radioMain.setChecked(false);

                binding.linaer.setVisibility(View.GONE);

            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.radioMain.isChecked()) {

                    // String main_address = binding.addressMain.getText().toString();
                    String main_gps_address = binding.currentMainAddress.getText().toString();

                    if (TextUtils.isEmpty(main_gps_address)) {

                        binding.currentMainAddress.setError("Address is Empty");
                    } else {

                        progressDialog.setMessage("Address main Saving.....");
                        progressDialog.show();

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("address_main", "" + main_gps_address);
                        data.put("address_main_latitude", "" + latitude);
                        data.put("address_main_longitude", "" + longitude);


                        databaseReference.child(mAuth.getUid()).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(User_Address_Activity.this, "Address Main Saved Successfully", Toast.LENGTH_SHORT);
                                toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                toast.show();
                                binding.currentMainAddress.setText("");

                            }
                        }).addOnFailureListener(e -> {

                            progressDialog.dismiss();
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                            toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            toast.show();
                        });
                    }


                } else if (binding.radioSecond.isChecked()) {

                    String second_address = binding.addressSecond.getText().toString();

                    if (TextUtils.isEmpty(second_address)) {

                        binding.addressSecond.setError("Empty Address");
                    } else {

                        progressDialog.setMessage("Address Second Saving.....");
                        progressDialog.show();


                        HashMap<String, Object> data = new HashMap<>();
                        data.put("address_second", "" + second_address);


                        databaseReference.child(mAuth.getUid()).updateChildren(data).addOnSuccessListener(unused -> {
                            progressDialog.dismiss();
                            Toast toast = Toast.makeText(User_Address_Activity.this, "Address Second Saved Successfully", Toast.LENGTH_SHORT);
                            toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            toast.show();
                            binding.addressSecond.setText("");
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                                toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                toast.show();
                            }
                        });
                    }

                }
            }
        });



        binding.mainbtn.setOnClickListener(view -> {
            binding.secondbtn.setChecked(false);

            try {

                if (main_address.equals("Empty")) {

                    Toast.makeText(User_Address_Activity.this, "Empty", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("deafault_address", "" + main_address);


                    databaseReference.child(mAuth.getUid()).updateChildren(data).addOnSuccessListener(unused -> {
                        Toast.makeText(User_Address_Activity.this, "Default Address Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

            } catch (Exception e) {

                Toast.makeText(User_Address_Activity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }


        });

        binding.secondbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.mainbtn.setChecked(false);
                try {

                    if (second_address.equals("Empty")) {
                        Toast.makeText(User_Address_Activity.this, "Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("deafault_address", "" + second_address);

                        databaseReference.child(mAuth.getUid()).updateChildren(data).addOnSuccessListener(unused -> {
                            Toast.makeText(User_Address_Activity.this, "Default Address Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        });

                    }

                } catch (Exception e) {

                    Toast.makeText(User_Address_Activity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

    private void checkDefaultAddressCheckBox() {

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    main_address = ""+snapshot.child("address_main").getValue();
                    second_address = ""+snapshot.child("address_second").getValue();

                    if (main_address.equals("Empty")){

                        binding.mainbtn.setChecked(false);

                    }else if (second_address.equals("Empty")){

                        binding.secondbtn.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location!=null){
                    Geocoder geocoder = new Geocoder(User_Address_Activity.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(),1);
                        latitude = addressList.get(0).getLatitude();
                        longitude = addressList.get(0).getLongitude();
                        binding.currentMainAddress.setText(addressList.get(0).getAddressLine(0));
                    }catch (Exception e){

                    }
                }

            }
        });
    }

    private void LoadShippingAddress() {

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    main_address = ""+snapshot.child("address_main").getValue();
                    second_address = ""+snapshot.child("address_second").getValue();
                    default_address = ""+snapshot.child("deafault_address").getValue();


                    if (main_address.equals("Empty")){

                        binding.showAddress.setText("Empty");

                    }else {
                        binding.showAddress.setText(main_address);

                    }

                    if (second_address.equals("Empty")){

                        binding.showAddress1.setText("Empty");

                    }else {

                        binding.showAddress1.setText(second_address);
                    }

                    LoadDefualtAddress(default_address, main_address, second_address);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    Users users = snapshot.getValue(Users.class);
//
//                        try {
//
//                            binding.showAddress.setText(users.getAddress_main());
//
//                            Log.d("CHECKADD",users.getAddress_main());
//                            binding.showAddress1.setText(users.getAddress_second());

//                            main_address = users.getAddress_main();
//                            second_address = users.getAddress_second();
//                            default_address = users.getDeafault_address();
//
////                            binding.showAddress.setText(main_address);
////                            binding.showAddress1.setText(second_address);
//
                         //   LoadDefualtAddress(default_address, main_address, second_address);
//
//                            if (binding.mainbtn.equals(main_address)) {
//                                binding.mainbtn.setChecked(true);
//                            }
//
//
//                        } catch (Exception e) {
//
//                            binding.showAddress.setText("Empty");
//                            binding.showAddress1.setText("Empty");
//                        }
//
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        databaseReference.child(mAuth.getUid()).addListenerForSingleValueEvent(valueEventListener);

    }

    private void LoadDefualtAddress(String default_address, String main_address, String second_address) {

        if (this.default_address.equals(this.main_address)) {

            binding.mainbtn.setChecked(true);


        } else {
            binding.mainbtn.setChecked(false);

        }

        if (default_address.equals(second_address)){

            binding.secondbtn.setChecked(true);
        }else {
            binding.secondbtn.setChecked(false);
        }
    }

//    private void getLocation() {
//
//        try {
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(User_Address_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(User_Address_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                return;
//            }
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
//
//        }catch (Exception e){
//
//            e.printStackTrace();
//        }
//    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(User_Address_Activity.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to get your current Location.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", (paramDialogInterface, paramInt) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    protected void onStart() {
        LoadShippingAddress();
        super.onStart();

    }
}