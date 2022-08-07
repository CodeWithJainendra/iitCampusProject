package com.shivam.iitcampusproject.Seller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.storage.UploadTask;
import com.shivam.iitcampusproject.Model.Products;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.databinding.ActivitySellerAddProductBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Seller_add_Product_Activity extends AppCompatActivity {

    ActivitySellerAddProductBinding binding;


    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storageReference;
    String product_name, product_desc, category, quantity, price;
    String discountPrice,discount_desc,shippingFee;


    Products addProducts;

    ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private Uri image_uri;

    private String[] cameraPermissions;
    private String[] storagePermissions;



    public static final String[] categories = {

            "Beverages",
            "Bakery",
            "Chilled",
            "Meats",
            "Grocery",
            "HomeWare",
            "Medicine",
            "PetCare",
            "Vegetables",
            "Fruits",
            "Crafts & Arts",
            "Clothes",
            "Foods",
            "Others"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance();
        databaseReference = database.getReference().child("Users");

        checkShippingFee();


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        binding.discountswich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    binding.txtdiscountPrice.setVisibility(View.VISIBLE);
                    binding.txtdiscountDescription.setVisibility(View.VISIBLE);
                }
                else {
                    binding.txtdiscountPrice.setVisibility(View.GONE);
                    binding.txtdiscountDescription.setVisibility(View.GONE);

                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

        binding.btnaddproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shippingFee.equals("Empty")){

                    Toast.makeText(Seller_add_Product_Activity.this,"Please update your shipping fee from settings",Toast.LENGTH_SHORT).show();
                }
                else {

                    ValidateData();
                }
            }
        });
    }

    private void checkShippingFee() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shippingFee = ""+snapshot.child("delivery_fee").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean discountAvailable = false;
    private void ValidateData() {

        product_name= binding.txtproductName.getText().toString().trim();
        product_desc= binding.txtdescription.getText().toString().trim();
        category= binding.txtcategory.getText().toString().trim();
        quantity= binding.txtquantity.getText().toString().trim();
        price= binding.txtprice.getText().toString().trim();
        discountPrice = binding.txtdiscountPrice.getText().toString();
        discountAvailable = binding.discountswich.isChecked();


        if (TextUtils.isEmpty(product_name)){
            binding.txtproductName.setError("Product Name Required");
            return;
        }
        if (TextUtils.isEmpty(product_desc)){
            binding.txtproductName.setError("Product Description Required");
            return;
        }
        if (TextUtils.isEmpty(category)){
            binding.txtproductName.setError("Product Category Required");
            return;
        }
        if (TextUtils.isEmpty(quantity)){
            binding.txtproductName.setError("Product Quantity Required");
            return;
        }
        if (TextUtils.isEmpty(price)){
            binding.txtproductName.setError("Product Price Required");
            return;
        }
        if (discountAvailable){

            discountPrice = binding.txtdiscountPrice.getText().toString().trim();
            discount_desc = binding.txtdiscountDescription.getText().toString().trim();

            if (TextUtils.isEmpty(discountPrice)){
                binding.txtdiscountPrice.setError("Discount Price Required");
                return;
            }
            else if (TextUtils.isEmpty(discount_desc)){
                binding.txtdiscountDescription.setError("Discount Description Required");
                return;
            }

        }
        else {
            discountPrice = "0";
            discount_desc = "";


        }

        addProducts();
    }

    private void addProducts() {

       // binding.progress.setVisibility(View.VISIBLE);
        progressDialog.setMessage("Saving Product Details");
        progressDialog.show();

        String timestamp = ""+System.currentTimeMillis();

        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        String time = currentTime.format(calendar2.getTime());

        if (image_uri == null){
            //upload without image

            HashMap<String,Object> obj = new HashMap<>();


            obj.put("productId",""+timestamp);
            obj.put("product_name",""+product_name);
            obj.put("product_desc",""+product_desc);
            obj.put("category",""+category);
            obj.put("quantity",""+quantity);
            obj.put("price",""+price);
            obj.put("product_image","");
            obj.put("discountPrice",""+discountPrice);
            obj.put("discount_desc",""+discount_desc);
            obj.put("discount_available",""+discountAvailable);
            obj.put("timestamp",""+time);
            obj.put("uid",""+mAuth.getUid());

            DatabaseReference databaseReference1 = database.getReference("Products");
            databaseReference1.child(timestamp).setValue(obj);
            databaseReference.child(mAuth.getUid()).child("Products").child(timestamp)
                    .setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(Seller_add_Product_Activity.this, "Product Added Successfully", Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    toast.show();
                    ClearData();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(Seller_add_Product_Activity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    toast.show();
                }
            });

        }
        else {
            progressDialog.setMessage("Saving Product Details with Image");
            //upload with image
            String filepathname = "Product_images/"+ ""+ timestamp;

            DatabaseReference databaseReference2 = database.getReference("Products");

            storageReference.getReference(filepathname).putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadimageUri = uriTask.getResult();

                    if (uriTask.isSuccessful()){


                        HashMap<String,Object> obj = new HashMap<>();

                        obj.put("productId",""+timestamp);
                        obj.put("product_name",""+product_name);
                        obj.put("product_desc",""+product_desc);
                        obj.put("category",""+category);
                        obj.put("quantity",""+quantity);
                        obj.put("price",""+price);
                        obj.put("product_image",downloadimageUri.toString());
                        obj.put("discountPrice",""+discountPrice);
                        obj.put("discount_desc",""+discount_desc);
                        obj.put("discount_available",""+discountAvailable);
                        obj.put("timestamp",""+time);
                        obj.put("uid",""+mAuth.getUid());


                        databaseReference2.child(timestamp).setValue(obj);
                        databaseReference.child(mAuth.getUid()).child("Products").child(timestamp)
                                .setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), "Product Added Successfully", Toast.LENGTH_SHORT);
                                toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                toast.show();
                                ClearData();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull  Exception e) {

                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                                toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                toast.show();
                            }
                        });
                    }

                }
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

    private void ClearData() {
        binding.imgproduct.setImageResource(R.drawable.profile);
        image_uri= null;
        binding.txtproductName.setText("");
        binding.txtdescription.setText("");
        binding.txtcategory.setText("");
        binding.txtquantity.setText("");
        binding.txtprice.setText("");
        binding.txtdiscountPrice.setText("");
        binding.txtdiscountDescription.setText("");
    }

    private void pickFromGallery(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image_Description");

        image_uri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {

        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Camera & Storage Permissions are Required",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(getApplicationContext(),"Camera & Storage Permissions are Required",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();

                binding.imgproduct.setImageURI(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                binding.imgproduct.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}