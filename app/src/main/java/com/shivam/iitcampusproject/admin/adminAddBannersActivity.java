package com.shivam.iitcampusproject.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shivam.iitcampusproject.Adapter.BannerAdapter;
import com.shivam.iitcampusproject.Model.Banners;
import com.shivam.iitcampusproject.databinding.ActivityAdminAddBannersBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class adminAddBannersActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    ActivityAdminAddBannersBinding binding;

    ArrayList<Uri> ImageList = new ArrayList<Uri>();

    private ArrayList<Banners> bannersList;

    private Uri ImageUri;
    private ProgressDialog progressDialog;
    private int upload_count = 0;

    Banners banners;
    BannerAdapter bannerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddBannersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        LoadBanners();

        binding.chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        binding.uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageList.size() == 0){
                    Toast.makeText(adminAddBannersActivity.this,"No Image Selected",Toast.LENGTH_SHORT).show();
                }
                else {
                    UploadImages();
                }

            }
        });
    }

    private void LoadBanners() {

        bannersList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banners");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannersList.clear();
                for (DataSnapshot ds : snapshot.getChildren() ){
                    banners = ds.getValue(Banners.class);
                    bannersList.add(banners);
                }

                bannerAdapter = new BannerAdapter(adminAddBannersActivity.this,bannersList);
               binding.recycleview.setAdapter(bannerAdapter);
                bannerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UploadImages() {

        progressDialog.setMessage("Uploading Images....");
        progressDialog.show();

        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("Banners");
        for (upload_count = 0; upload_count < ImageList.size(); upload_count++){

            Uri IndividualImage = ImageList.get(upload_count);
            StorageReference ImageName = ImageFolder.child("Image"+IndividualImage.getLastPathSegment());

            ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            StoreBannerLinks(url);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(adminAddBannersActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void StoreBannerLinks(String url) {

        long timestamp = System.currentTimeMillis();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banners");
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Imagelink",url);
        hashMap.put("timestamp",""+timestamp);

        databaseReference.child(String.valueOf(timestamp)).setValue(hashMap);
        progressDialog.dismiss();
        Toast.makeText(adminAddBannersActivity.this,"Successfully Saved Links",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE){
            if (resultCode == RESULT_OK){
                if (data.getClipData() != null){

                    int countClipData = data.getClipData().getItemCount();
                    int currentImageSelect = 0;
                    while (currentImageSelect < countClipData){

                        ImageUri = data.getClipData().getItemAt(currentImageSelect).getUri();
                        ImageList.add(ImageUri);
                        currentImageSelect = currentImageSelect +1;

                    }

                    Toast.makeText(adminAddBannersActivity.this,"You hve Selected "+ImageList.size() +" Images",Toast.LENGTH_SHORT).show();
                }
                else {
                 //   Toast.makeText(adminAddBannersActivity.this,"Please Select Multiple Image",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}