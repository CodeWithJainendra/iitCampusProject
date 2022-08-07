package com.shivam.iitcampusproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Filters.FilterCity;
import com.shivam.iitcampusproject.Model.Shops;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.User.Show_Shop_Products_Activity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.HolderShopAdapter> implements Filterable {

    private Context context;
    public ArrayList<Shops> shopsList,filterlist;
    private FilterCity filterCity;
    private String checkedType;

    public ShopAdapter(Context context, ArrayList<Shops> shopsList, String checkedType) {
        this.context = context;
        this.shopsList = shopsList;
        this.filterlist = shopsList;
        this.checkedType = checkedType;
    }

    @NonNull
    @Override
    public HolderShopAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_shops,parent,false);
        return new HolderShopAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShopAdapter holder, int position) {

        Shops shops = shopsList.get(position);

        String type = shops.getUserType();
        String address = shops.getAddress();
        String delivery_fee = shops.getDelivery_fee();
        String email = shops.getEmail();
        String shop_status = shops.getShop_Status();
        String shopname = shops.getShopName();
        String phone= shops.getMobile();
        String profileImage = shops.getPhotoUrl();
        String city = shops.getCity();
        String uid = shops.getUid();

        LoadReviews(shops,holder);
        LoadShopRating(holder,shops);

        Log.d("TAgerbb",checkedType);

        //this is for change background color
        if (checkedType.equals("nearShops")){

            holder.backgroundColorRIv.setImageResource(R.drawable.gradient_bg2);
        }
        else {
            holder.backgroundColorRIv.setImageResource(R.drawable.gradient_bg2);
        }


        holder.shop_name.setText(shopname);
        Log.d("SHOPNAME",shopname);
        holder.phone.setText(phone);
        holder.address.setText(address);
        holder.city.setText(city);

        if (shop_status.equals("Open")){

            holder.online.setVisibility(View.VISIBLE);
            holder.shopClosed.setVisibility(View.GONE);
        }else {
            holder.online.setVisibility(View.GONE);
            holder.shopClosed.setVisibility(View.VISIBLE);
        }

        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.shop).into(holder.shop_Image);

        }catch (Exception e){

            holder.shop_Image.setImageResource(R.drawable.shop);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Show_Shop_Products_Activity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("rate",String.valueOf(avgRating));
                context.startActivity(intent);

            }
        });
    }

    private float ratingSum = 0;
    private float avgRating;
    private void LoadShopRating(HolderShopAdapter holder, Shops shops) {

        String selleruid = shops.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(selleruid).child("Rating");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    ratingSum = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                        Log.d("RATE1", String.valueOf(rating));
                        ratingSum = ratingSum + rating;
                    }

                    long numberOfReviews = snapshot.getChildrenCount();
                    avgRating = ratingSum / numberOfReviews;

                    holder.ratingBar.setRating(avgRating);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void LoadReviews(Shops shops, HolderShopAdapter holder) {
    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterCity == null){
            filterCity = new FilterCity(this,filterlist);
        }
        return filterCity;

    }

    public class HolderShopAdapter extends RecyclerView.ViewHolder {

        private ImageView shop_Image,online;
        private TextView shopClosed,shop_name,phone,address,city;
        private RatingBar ratingBar;
        private RoundedImageView backgroundColorRIv;

        public HolderShopAdapter(@NonNull View itemView) {
            super(itemView);

            shop_Image = itemView.findViewById(R.id.shop_image);
            online = itemView.findViewById(R.id.online);
            shopClosed = itemView.findViewById(R.id.shopClosed);
            shop_name = itemView.findViewById(R.id.shop_name);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            city = itemView.findViewById(R.id.city);
            backgroundColorRIv = itemView.findViewById(R.id.backgroundColorRIv);

        }
    }


}
