package com.shivam.iitcampusproject.Seller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.databinding.ActivitySellerEditProductsBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Seller_Edit_Products_Activity extends AppCompatActivity {

    ActivitySellerEditProductsBinding binding;

    private String productId;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference,databaseReference1;
    FirebaseStorage storageReference;

    String product_name, product_desc, category, quantity, price;
    String discountPrice, discount_desc;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;

    public static final String[] categories = {

            "Beverages",
            "Beauty & Personal Care",
            "Baby Kids",
            "Biscuits Snacks & Chocolates",
            "Breakfast & Diary",
            "Cooking Needs",
            "Frozen Foods",
            "Fruits",
            "Pet Care",
            "Pharmacy",
            "Vegetables",
            "Others"
    };

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerEditProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance();
        databaseReference = database.getReference().child("Users");
        databaseReference1 = database.getReference().child("Products");

        productId = getIntent().getStringExtra("productId");

        binding.toolbar.setTitle("Update Product");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        loadProductDetails();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        binding.discountswich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.txtdiscountPrice.setVisibility(View.VISIBLE);
                    binding.txtdiscountDescription.setVisibility(View.VISIBLE);
                } else {
                    binding.txtdiscountPrice.setVisibility(View.GONE);
                    binding.txtdiscountDescription.setVisibility(View.GONE);

                }
            }
        });

        binding.uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerDialog();
            }
        });

        binding.txtcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryShowDialog();
            }
        });

        binding.btnupdateproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateData();
            }
        });
    }

    private void categoryShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category").setItems(categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String category = categories[i];
                binding.txtcategory.setText(category);
            }
        }).show();
    }
    private boolean discountAvailable = false;
    private void ValidateData() {

        product_name = binding.txtproductName.getText().toString().trim();
        product_desc = binding.txtdescription.getText().toString().trim();
        category = binding.txtcategory.getText().toString().trim();
        quantity = binding.txtquantity.getText().toString().trim();
        price = binding.txtprice.getText().toString().trim();
        discountPrice = binding.txtdiscountPrice.getText().toString();
        discountAvailable = binding.discountswich.isChecked();


        if (TextUtils.isEmpty(product_name)) {
            binding.txtproductName.setError("Product Name Required");
            return;
        }
        if (TextUtils.isEmpty(product_desc)) {
            binding.txtproductName.setError("Product Description Required");
            return;
        }
        if (TextUtils.isEmpty(category)) {
            binding.txtproductName.setError("Product Category Required");
            return;
        }
        if (TextUtils.isEmpty(quantity)) {
            binding.txtproductName.setError("Product Quantity Required");
            return;
        }
        if (TextUtils.isEmpty(price)) {
            binding.txtproductName.setError("Product Price Required");
            return;
        }
        if (discountAvailable) {

            discountPrice = binding.txtdiscountPrice.getText().toString().trim();
            discount_desc = binding.txtdiscountDescription.getText().toString().trim();

            if (TextUtils.isEmpty(discountPrice)) {
                binding.txtdiscountPrice.setError("Discount Price Required");
                return;
            } else if (TextUtils.isEmpty(discount_desc)) {
                binding.txtdiscountDescription.setError("Discount Description Required");
                return;
            }

        } else {
            discountPrice = "0";
            discount_desc = "";


        }

        UpdateProducts();
    }

    private void UpdateProducts() {

     //   binding.progress.setVisibility(View.VISIBLE);
        progressDialog.setMessage("Product Updating");
        progressDialog.show();

        if (image_uri == null){

            HashMap<String,Object> obj = new HashMap<>();
            obj.put("product_name",""+product_name);
            obj.put("product_desc",""+product_desc);
            obj.put("category",""+category);
            obj.put("quantity",""+quantity);
            obj.put("price",""+price);
            obj.put("discountPrice",""+discountPrice);
            obj.put("discount_desc",""+discount_desc);
            obj.put("discount_available",""+discountAvailable);

            databaseReference1.child(productId).updateChildren(obj);
            databaseReference.child(mAuth.getUid()).child("Products").child(productId).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                   progressDialog.dismiss();
                    Toast.makeText(Seller_Edit_Products_Activity.this,"Updated",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }else {

            progressDialog.setMessage("Product Updating with Image");

            String filePathAndName = "product_images/" + ""+ productId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageUri  = uriTask.getResult();

                    if (uriTask.isSuccessful()){
                        HashMap<String,Object> obj = new HashMap<>();
                        obj.put("product_name",""+product_name);
                        obj.put("product_desc",""+product_desc);
                        obj.put("category",""+category);
                        obj.put("product_image",""+downloadImageUri);
                        obj.put("quantity",""+quantity);
                        obj.put("price",""+price);
                        obj.put("discountPrice",""+discountPrice);
                        obj.put("discount_desc",""+discount_desc);
                        obj.put("discount_available",""+discountAvailable);

                        databaseReference1.child(productId).updateChildren(obj);
                        databaseReference.child(mAuth.getUid()).child("Products").child(productId).updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                              progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                   progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void loadProductDetails() {

        databaseReference.child(mAuth.getUid()).child("Products").child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String productId = ""+ snapshot.child("productId").getValue();
                String product_name = ""+ snapshot.child("product_name").getValue();
                String product_desc = ""+ snapshot.child("product_desc").getValue();
                String category = ""+ snapshot.child("category").getValue();
                String quantity = ""+ snapshot.child("quantity").getValue();
                String product_image = ""+ snapshot.child("product_image").getValue();
                String price = ""+ snapshot.child("price").getValue();
                String discountPrice = ""+ snapshot.child("discountPrice").getValue();
                String discount_desc = ""+ snapshot.child("discount_desc").getValue();
                String discount_available = ""+ snapshot.child("discount_available").getValue();
                String timestamp = ""+ snapshot.child("timestamp").getValue();
                String uid = ""+ snapshot.child("uid").getValue();

                if (discount_available.equals("true")){

                    binding.discountswich.setChecked(true);
                    binding.txtdiscountPrice.setVisibility(View.VISIBLE);
                    binding.txtdiscountDescription.setVisibility(View.VISIBLE);
                }
                else {
                    binding.discountswich.setChecked(false);
                    binding.txtdiscountPrice.setVisibility(View.GONE);
                    binding.txtdiscountDescription.setVisibility(View.GONE);
                }
                binding.txtproductName.setText(product_name);
                binding.txtdescription.setText(product_desc);
                binding.txtcategory.setText(category);
                binding.txtquantity.setText(quantity);
                binding.txtdiscountDescription.setText(discount_desc);
                binding.txtdiscountPrice.setText(discountPrice);
                binding.txtprice.setText(price);

                try {
                    Picasso.get().load(product_image).placeholder(R.drawable.profile).into(binding.imgproduct);

                }catch (Exception e){
                    binding.imgproduct.setImageResource(R.drawable.profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String productId = ""+ snapshot.child("productId").getValue();
//                String product_name = ""+ snapshot.child("product_name").getValue();
//                String product_desc = ""+ snapshot.child("product_desc").getValue();
//                String category = ""+ snapshot.child("category").getValue();
//                String quantity = ""+ snapshot.child("quantity").getValue();
//                String product_image = ""+ snapshot.child("product_image").getValue();
//                String price = ""+ snapshot.child("price").getValue();
//                String discountPrice = ""+ snapshot.child("discountPrice").getValue();
//                String discount_desc = ""+ snapshot.child("discount_desc").getValue();
//                String discount_available = ""+ snapshot.child("discount_available").getValue();
//                String timestamp = ""+ snapshot.child("timestamp").getValue();
//                String uid = ""+ snapshot.child("uid").getValue();
//
//                if (discount_available.equals("true")){
//
//                    binding.discountswich.setChecked(true);
//                    binding.txtdiscountPrice.setVisibility(View.VISIBLE);
//                    binding.txtdiscountDescription.setVisibility(View.VISIBLE);
//                }
//                else {
//                    binding.discountswich.setChecked(false);
//                    binding.txtdiscountPrice.setVisibility(View.GONE);
//                    binding.txtdiscountDescription.setVisibility(View.GONE);
//                }
//                binding.txtproductName.setText(product_name);
//                binding.txtdescription.setText(product_desc);
//                binding.txtcategory.setText(category);
//                binding.txtquantity.setText(quantity);
//                binding.txtdiscountDescription.setText(discount_desc);
//                binding.txtdiscountPrice.setText(discountPrice);
//                binding.txtprice.setText(price);
//
//                try {
//                    Picasso.get().load(product_image).placeholder(R.drawable.profile).into(binding.imgproduct);
//
//                }catch (Exception e){
//                    binding.imgproduct.setImageResource(R.drawable.profile);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        databaseReference.child(mAuth.getUid()).child("Products").child(productId).addListenerForSingleValueEvent(valueEventListener);

    }

    private void showImagePickerDialog() {

        String[] options = {"camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (checkCameraPermissions()) {
                        pickFromCamera();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    if (checkStoragePermission()) {
                        pickFromGallery();

                    } else {
                        requestStoragePermission();
                    }

                }
            }
        });
        builder.show();
    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        image_uri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {

        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getApplicationContext(), "Camera & Storage Permissions are Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getApplicationContext(), "Camera & Storage Permissions are Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();

                binding.imgproduct.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                binding.imgproduct.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}
