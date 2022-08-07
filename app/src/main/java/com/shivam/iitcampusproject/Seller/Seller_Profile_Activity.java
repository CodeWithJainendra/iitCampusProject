package com.shivam.iitcampusproject.Seller;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shivam.iitcampusproject.Model.Sellers;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.databinding.ActivitySellerProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Seller_Profile_Activity extends AppCompatActivity implements LocationListener {

    ActivitySellerProfileBinding binding;

    private static final int LOCATION_REQUEST_CODE = 500;
    private String[] locationPermissions;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    DatabaseReference databaseReference;

    private static final int PReqCode = 2;
    private static final int REQ_CODE = 1000;

    private Uri image_uri;
    String UserId;

    ArrayList<String> arrayList;
    Dialog dialog;

    Double Latitude, Longitude;

    private LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
        UserId = mAuth.getUid();



        arrayList = new ArrayList<>();

        arrayList.add("Ampara");
        arrayList.add("Anuradhapura");
        arrayList.add("Badulla");
        arrayList.add("Batticaloa");
        arrayList.add("Colombo");
        arrayList.add("Galle");
        arrayList.add("Gampaha");
        arrayList.add("Hambantota");
        arrayList.add("Jaffna");
        arrayList.add("Kalutara");
        arrayList.add("Kandy");
        arrayList.add("Kegalle");
        arrayList.add("Kilinochchi");
        arrayList.add("Kurunegala");
        arrayList.add("Mannar");
        arrayList.add("Matale");
        arrayList.add("Matara");
        arrayList.add("Monaragala");
        arrayList.add("Mullaitivu");
        arrayList.add("Nuwara Eliya");
        arrayList.add("Polonnaruwa");
        arrayList.add("Puttalam");
        arrayList.add("Ratnapura");
        arrayList.add("Trincomalee");
        arrayList.add("Vavuniya");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait......");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.txtcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(Seller_Profile_Activity.this);
                dialog.setContentView(R.layout.dialog_searchable_city);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Seller_Profile_Activity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        binding.txtcity.setText(adapter.getItem(i));
                        dialog.dismiss();
                    }
                });


            }
        });

        binding.captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestForPermission();
            }
        });

        binding.btnGps.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkLocationPermission()) {
                    getShopLocation();
                    //  getLocation();
                } else {
                    requestLocationPermissions();
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.shopOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.shopOpen.isChecked()) {
                    binding.shopOpen.setText("Shop Open");


                    StatusShopOpen();
                    Toast.makeText(Seller_Profile_Activity.this, "Shop Open", Toast.LENGTH_SHORT).show();


                } else {

                    StatusShopClosed();
                    Toast.makeText(Seller_Profile_Activity.this, "Shop Closed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Updating.........");
                progressDialog.show();

                //  binding.progress.setVisibility(View.VISIBLE);

                String username = binding.txtusername.getText().toString().trim();
                String mobile = binding.txtmobile.getText().toString().trim();
                String shopname = binding.txtshopname.getText().toString().trim();
                String fee = binding.txtdelivery.getText().toString().trim();
                String city = binding.txtcity.getText().toString().trim();
                String Latitude = binding.latitude.getText().toString().trim();
                String Longitude = binding.longitude.getText().toString().trim();


                Log.d("GOOGLE",Latitude);

                //  DatabaseReference databaseReference = database.getReference().child("Users").child(mAuth.getUid());

                HashMap<String, Object> obj = new HashMap<>();

                obj.put("username", username);
                obj.put("mobile", mobile);
                obj.put("shopName", shopname);
                obj.put("delivery_fee", fee);
                obj.put("city", city);
                obj.put("latitude", Double.valueOf(Latitude));
                obj.put("longitude", Double.valueOf(Longitude));

                DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid());
                databaseReference.updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(Seller_Profile_Activity.this, "Profile Uploaded ....", Toast.LENGTH_SHORT).show();
                    }
                });

                //  binding.progress.setVisibility(View.GONE);

            }
        });


    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Seller_Profile_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(Seller_Profile_Activity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Seller_Profile_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
            return;
        } else {
            Intent OpenGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(OpenGalleryIntent, REQ_CODE);
        }
    }

    private void StatusShopClosed() {

        DatabaseReference databaseReference = database.getReference("Users").child(UserId);

        binding.shopOpen.setText("Shop Closed");
        binding.shopOpen.setTextColor(Color.parseColor("#EA0808"));

        HashMap<String, Object> obj = new HashMap<>();
        obj.put("Shop_Status", "Closed");
        databaseReference.updateChildren(obj);
    }

    private void StatusShopOpen() {
        DatabaseReference databaseReference = database.getReference("Users").child(UserId);

        binding.shopOpen.setTextColor(Color.parseColor("#199BE1"));
        HashMap<String, Object> obj = new HashMap<>();
        obj.put("Shop_Status", "Open");
        databaseReference.updateChildren(obj);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {


                image_uri = data.getData();
                try {

                    UploadToDatabase(image_uri);

                } catch (Exception e) {
                    Log.e("TAG", "Error : " + e);
                }

            }

        }
    }

    private void UploadToDatabase(Uri image_uri) {

        progressDialog.setMessage("Image Uploading.....");
        progressDialog.show();

        StorageReference fileRef = storage.getReference().child("profile_pic").child(FirebaseAuth.getInstance().getUid());
        fileRef.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.
                TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        progressDialog.dismiss();
                        database.getReference().child("Users").child(mAuth.getUid()).child("photoUrl").setValue(uri.toString());
                        Picasso.get().load(uri).into(binding.profileImage);


                    }
                });
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(Seller_Profile_Activity.this, "Profile Uploading Failed ....", Toast.LENGTH_SHORT).show();
            Toast.makeText(Seller_Profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(Seller_Profile_Activity.this, "Enable Location....", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        getCurrentLocation();
                    } else {
                        Toast.makeText(Seller_Profile_Activity.this, "Location Permissions Required...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        }


    }

    private void getLocation() {
        Toast.makeText(Seller_Profile_Activity.this, "Please Wait....", Toast.LENGTH_LONG).show();

        locationManager = (LocationManager) Seller_Profile_Activity.this.getSystemService(Seller_Profile_Activity.this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void getShopLocation() {
        if (ActivityCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.
                permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.
                permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //////////When Both permissions are granted/////////////

            /////////////call Method/////////
            getCurrentLocation();
        } else {
            //////When permission is not granted
            //////Request permission
            ActivityCompat.requestPermissions(Seller_Profile_Activity.this, new
                    String[]{Manifest.
                    permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    private void getCurrentLocation() {

        LocationManager locationManager = (LocationManager) Seller_Profile_Activity.this.getSystemService(Context.
                LOCATION_SERVICE);

        ////////////////check condition/////////////////////
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            ///////////////When Location is enabled//////////////////
            ///////////Get Last Location////////////////////////////
            if (ActivityCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    ////////////Initialize Location////////////////////
                    Location location = task.getResult();
                    /////check condition//////
                    if (location != null) {
                        ////////////// when location result is not null///////////
                        //// Set Lalitude
                        binding.latitude.setText(String.valueOf(location.getLatitude()));
                        /////set Longitude
                        binding.longitude.setText(String.valueOf(location.getLongitude()));

                    } else {
                        ///When Location result is null
                        /////Intialize Location request
                        LocationRequest locationRequest = new LocationRequest().setPriority
                                (LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(1000).setFastestInterval(1000).setNumUpdates(1);

                        ///////////Initialize Location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                //////////Initialize Location
                                Location location1 = locationResult.getLastLocation();
                                ///////////set Latitude
                                Latitude = (location1.getLatitude());
                                binding.latitude.setText(Latitude.toString());

                                Longitude = (location1.getLongitude());
                                binding.longitude.setText(Longitude.toString());

                            }
                        };

                        ////////////Request Location updates
                        if (ActivityCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Seller_Profile_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());
                    }

                }
            });

        } else {
            ///////////When Location service is not enabled
            //////////Open Location Setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        LoadData();
       // StatusShopClosed();
        CountInProgress();
        CountCancelled();
        CountCompleted();
    }

    private void CountCompleted() {
        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders");
        databaseReference.orderByChild("orderStatus").equalTo("Completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getChildrenCount();

                binding.countCompleted.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Seller_Profile_Activity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void CountCancelled() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders");
        databaseReference.orderByChild("orderStatus").equalTo("Cancelled").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getChildrenCount();

                binding.countCancelled.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Seller_Profile_Activity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CountInProgress() {

            DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Orders");
            databaseReference.orderByChild("orderStatus").equalTo("InProgress").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Long count = snapshot.getChildrenCount();

                    binding.countInprogress.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Seller_Profile_Activity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void LoadData() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

              //  String city = ""+snapshot.child("city").getValue();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Sellers sellers = snapshot.getValue(Sellers.class);

                    binding.txtusername.setText(sellers.getUsername());
                    binding.txtmobile.setText(sellers.getMobile());
                    binding.txtshopname.setText(sellers.getShopName());
                    binding.txtaddress.setText(sellers.getAddress());
                    binding.txtdelivery.setText(sellers.getDelivery_fee());
                    binding.txtcity.setText(sellers.getCity());
                    binding.latitude.setText(String.valueOf(sellers.getLatitude()));
                    binding.longitude.setText(String.valueOf(sellers.getLongitude()));

                    String name = sellers.getUsername();
                    binding.profileName.setText(sellers.getUsername());

                    String shop_status = sellers.getShop_Status();
                    binding.profileName.setText(sellers.getUsername());
                    binding.userEmail.setText(sellers.getEmail());

                    try {
                        if (shop_status.equals("Open")) {

                            binding.shopOpen.setChecked(true);
                            binding.shopOpen.setText("Shop Open");
                            binding.shopOpen.setTextColor(Color.parseColor("#199BE1"));

                        } else if (shop_status.equals("Closed")) {
                            binding.shopOpen.setChecked(false);
                            binding.shopOpen.setText("Shop Closed");
                            binding.shopOpen.setTextColor(Color.parseColor("#EA0808"));
                        }
                    } catch (Exception e) {


                    }

                    try {
                        Picasso.get().load(sellers.getPhotoUrl()).placeholder(R.drawable.profile).into(binding.profileImage);

                    }catch (Exception e){

                        binding.profileImage.setImageResource(R.drawable.profile);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Seller_Profile_Activity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("EROOR",error.getMessage().toString());
            }
        });

    }


}