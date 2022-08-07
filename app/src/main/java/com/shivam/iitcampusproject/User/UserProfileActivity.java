package com.shivam.iitcampusproject.User;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.shivam.iitcampusproject.databinding.ActivityUserProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri;
    ArrayList<String> arrayList;

    Dialog dialog;

    ProgressDialog progressDialog;
    String storePath = "Users_Profile_Imgs/";

    String profilePhoto = "image";

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        arrayList = new ArrayList<>();

        arrayList.add("Afghanistan");
        arrayList.add("Albania");
        arrayList.add("Algeria");
        arrayList.add("Andorra");
        arrayList.add("Angola");
        arrayList.add("Antigua and Barbuda");
        arrayList.add("Argentina");
        arrayList.add("Armenia");
        arrayList.add("Australia");
        arrayList.add("Austria");
        arrayList.add("The Bahamas");
        arrayList.add("Bahrain");
        arrayList.add("Bangladesh");
        arrayList.add("Barbados");
        arrayList.add("Belarus");
        arrayList.add("Belgium");
        arrayList.add("Belize");
        arrayList.add("Benin");
        arrayList.add("Bhutan");
        arrayList.add("Bolivia");
        arrayList.add("Bosnia and Herzegovina");
        arrayList.add("Botswana");
        arrayList.add("Brazil");
        arrayList.add("Bulgaria");
        arrayList.add("Cambodia");
        arrayList.add("Cameroon");
        arrayList.add("Canada");
        arrayList.add("Central African Republic");
        arrayList.add("Chad");
        arrayList.add("Chile");
        arrayList.add("China");
        arrayList.add("Colombia");
        arrayList.add("Comoros");
        arrayList.add("Cyprus");
        arrayList.add("Denmark");
        arrayList.add("Djibouti");
        arrayList.add("Dominica");
        arrayList.add("Dominican Republic");
        arrayList.add("East Timor (Timor-Leste)");
        arrayList.add("Ecuador");
        arrayList.add("Egypt");
        arrayList.add("El Salvador");
        arrayList.add("Eswatini");
        arrayList.add("Eritrea");
        arrayList.add("Estonia");
        arrayList.add("Finland");
        arrayList.add("France");
        arrayList.add("Fiji");
        arrayList.add("Gabon");
        arrayList.add("The Gambia");
        arrayList.add("Georgia");
        arrayList.add("Germany");
        arrayList.add("Ghana");
        arrayList.add("Greece");
        arrayList.add("Grenada");
        arrayList.add("Guatemala");
        arrayList.add("Guinea");
        arrayList.add("Guinea-Bissau");
        arrayList.add("Haiti");
        arrayList.add("Honduras");
        arrayList.add("Hungary");
        arrayList.add("Iceland");
        arrayList.add("India");
        arrayList.add("Indonesia");
        arrayList.add("Iran");
        arrayList.add("Iraq");
        arrayList.add("Ireland");
        arrayList.add("Italy");
        arrayList.add("Japan");
        arrayList.add("Jamaica");
        arrayList.add("Jordan");
        arrayList.add("Kazakhstan");
        arrayList.add("Kenya");
        arrayList.add("Kiribati");
        arrayList.add("Korea, North");
        arrayList.add("Korea, South");
        arrayList.add("Kosovo");
        arrayList.add("Kuwait");
        arrayList.add("Laos");
        arrayList.add("Latvia");
        arrayList.add("Lebanon");
        arrayList.add("Lesotho");
        arrayList.add("Liberia");
        arrayList.add("Libya");
        arrayList.add("Liechtenstein");
        arrayList.add("Lithuania");
        arrayList.add("Mexico");
        arrayList.add("Moldova");
        arrayList.add("Monaco");
        arrayList.add("Mongolia");
        arrayList.add("Mauritius");
        arrayList.add("Other.....");

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        LoadUserData();

        binding.captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.txtcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadCityDialog();
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUserDetails();
            }
        });
    }

    private void LoadCityDialog() {

        dialog = new Dialog(UserProfileActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_city);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserProfileActivity.this, android.R.layout.simple_list_item_1, arrayList);
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

    private void UpdateUserDetails() {

        progressDialog.setMessage("Updating.........");
        progressDialog.show();

        String username = binding.txtusername.getText().toString().trim();
        String mobile = binding.txtmobile.getText().toString().trim();
        String city = binding.txtcity.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username",username);
        hashMap.put("mobile",mobile);
        hashMap.put("city",city);

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(mAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, "Profile Uploaded ....", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadUserData() {

        DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String username = "" + snapshot.child("username").getValue();
                    String mobile = "" + snapshot.child("mobile").getValue();
                    String city = "" + snapshot.child("city").getValue();
                    String email = "" + snapshot.child("email").getValue();
                    String profilePic = "" + snapshot.child("photoUrl").getValue();


                    binding.txtusername.setText(username);
                    binding.txtmobile.setText(mobile);
                    binding.txtcity.setText(city);
                    binding.profileName.setText(username);
                    binding.userEmail.setText(email);

                    try {
                        Picasso.get().load(profilePic).into(binding.profileImage);

                    }catch (Exception e){

                        binding.profileImage.setImageResource(R.drawable.profile);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showImagePickDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Pick Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (i == 1) {

                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });

        builder.create().show();
    }

    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = UserProfileActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "please enable camera & storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "please enable storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                uploadProfilePhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                uploadProfilePhoto(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri image_uri) {

        progressDialog.setMessage("Uploading Image.....");
        progressDialog.show();

        String filePathName = storePath + "" + profilePhoto + "_" + mAuth.getUid();

        StorageReference storageReference1 = storageReference.child(filePathName);
        storageReference1.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("photoUrl", downloadUri.toString());

                            databaseReference.child(mAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserProfileActivity.this, "Image Uploaded....", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        LoadUserData();
        LoadCartItemCount();
        super.onStart();
    }

    private void LoadCartItemCount() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid()).child("Cart");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                Log.d("COUNT",String.valueOf(count));
                binding.cartItem.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}