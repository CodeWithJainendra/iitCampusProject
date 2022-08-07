package com.shivam.iitcampusproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Model.Banners;
import com.shivam.iitcampusproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private ArrayList<Banners> bannersList;

    public BannerAdapter(Context context, ArrayList<Banners> bannersList) {
        this.context = context;
        this.bannersList = bannersList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Banners banners = bannersList.get(position);

        String image = banners.getImagelink();
        String timestamp = banners.getTimestamp();

        try {
            Picasso.get().load(image).placeholder(R.color.home_background).into(holder.imageView);

        } catch (Exception e) {

            holder.imageView.setImageResource(R.color.home_background);

        }

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Banners banners = bannersList.get(position);
                String timestamp = banners.getTimestamp();


                DeleteImage(timestamp);

            }
        });
    }

    private void DeleteImage(String timestamp) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Banners").child(timestamp);
        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context,"Successfully Removed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bannersList.size();
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView imageView;
        ImageButton deleteBtn;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
