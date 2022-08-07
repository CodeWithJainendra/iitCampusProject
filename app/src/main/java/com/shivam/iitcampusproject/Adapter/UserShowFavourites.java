package com.shivam.iitcampusproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Model.Favourites;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.User.Show_one_product_Activity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserShowFavourites extends RecyclerView.Adapter<UserShowFavourites.HolderUserShowFavourites> {

    private Context context;
    private ArrayList<Favourites> favouritesList;

    DatabaseReference favouriteRef;
    FirebaseAuth mAuth;
    FirebaseDatabase database;


    boolean isInMyFavorite = false;

    public UserShowFavourites(Context context, ArrayList<Favourites> favouritesList) {
        this.context = context;
        this.favouritesList = favouritesList;
    }

    @NonNull
    @Override
    public HolderUserShowFavourites onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_favourites, parent, false);
        return new HolderUserShowFavourites(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUserShowFavourites holder, int position) {

        Favourites favourites = favouritesList.get(position);

        checkIsFavourite(holder, position);

        String productId = favourites.getProductId();
        String category = favourites.getCategory();
        String discountAvailable = favourites.getDiscountAvailable();
        String discount_dec = favourites.getDiscount_dec();
        String discount_price = favourites.getDiscount_price();
        String original_price = favourites.getOriginal_price();
        String product_dec = favourites.getProduct_dec();
        String product_image = favourites.getProduct_image();
        String product_name = favourites.getProduct_name();
        String quantity = favourites.getQuantity();
        String timestamp = favourites.getTimestamp();
        String sellerUid = favourites.getUid();

        holder.product_name.setText(product_name);
        holder.quantity.setText(quantity);
        holder.discount_note.setText(discount_dec);
        holder.discount_price.setText("Rs." + discount_price);
        holder.original_price.setText("Rs." + original_price);


        if (discountAvailable.equals("true")) {

            holder.discount_price.setVisibility(View.VISIBLE);
            holder.discount_note.setVisibility(View.VISIBLE);
            holder.original_price.setPaintFlags(holder.original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            holder.discount_price.setVisibility(View.GONE);
            holder.discount_note.setVisibility(View.GONE);
            holder.original_price.setPaintFlags(0);
        }
        try {
            Picasso.get().load(product_image).placeholder(R.drawable.picture).into(holder.productImage);

        } catch (Exception e) {
            holder.productImage.setImageResource(R.drawable.picture);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Show_one_product_Activity.class);
                intent.putExtra("product_image", product_image);
                intent.putExtra("discountAvailable", discountAvailable);
                intent.putExtra("discount_price", discount_price);
                intent.putExtra("original_price", original_price);
                intent.putExtra("product_name", product_name);
                intent.putExtra("quantity", quantity);
                intent.putExtra("product_dec", product_dec);
                intent.putExtra("category", category);
                intent.putExtra("discount_dec", discount_dec);
                intent.putExtra("Selleruid", sellerUid);
                intent.putExtra("Productid", productId);
                context.startActivity(intent);
            }
        });

    }

    private void checkIsFavourite(HolderUserShowFavourites holder, int position) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Favourites favourites = favouritesList.get(position);
        String product_id = favourites.getProductId();

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(context, "You're not Logged In", Toast.LENGTH_SHORT).show();

        } else {

            favouriteRef = database.getReference("Users");
            favouriteRef.child(mAuth.getUid()).child("Favourites").child(product_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    isInMyFavorite = snapshot.exists();

                    if (isInMyFavorite) {

                        holder.favBtn.setBackgroundResource(R.drawable.ic_fav_color);

                    } else {

                        holder.favBtn.setBackgroundResource(R.drawable.ic_fav_shadow);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public class HolderUserShowFavourites extends RecyclerView.ViewHolder {

        private RoundedImageView productImage;
        private TextView discount_note, product_name, quantity, discount_price, original_price;
        ImageButton favBtn;


        public HolderUserShowFavourites(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            discount_note = itemView.findViewById(R.id.discountnote);
            product_name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            discount_price = itemView.findViewById(R.id.discount_price);
            original_price = itemView.findViewById(R.id.original_price);
            favBtn = itemView.findViewById(R.id.favBtn);
        }
    }
}
