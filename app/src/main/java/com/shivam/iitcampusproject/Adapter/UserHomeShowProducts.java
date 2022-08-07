package com.shivam.iitcampusproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Filters.UserHomeFilterProduct;
import com.shivam.iitcampusproject.Model.Products;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.User.Show_one_product_Activity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserHomeShowProducts extends RecyclerView.Adapter<UserHomeShowProducts.HolderUserHomeShowProducts> implements Filterable {

    private Context context;
    public ArrayList<Products> productList, filterList;
    private UserHomeFilterProduct filter;

    DatabaseReference favouriteRef;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

 //   public boolean isShimmer = true;

    boolean isInMyFavorite = false;


    public UserHomeShowProducts(Context context, ArrayList<Products> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderUserHomeShowProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user_home_products, parent, false);
        return new HolderUserHomeShowProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUserHomeShowProducts holder, @SuppressLint("RecyclerView") int position) {


//        if (isShimmer) {
//            holder.shimmerFrameLayout.startShimmer();
//        }
//        else {
//
//            holder.shimmerFrameLayout.stopShimmer();
//            holder.shimmerFrameLayout.setShimmer(null);
//
//            holder.productImage.setBackground(null);
//            holder.product_name.setBackground(null);
//            holder.ratingBar.setBackground(null);

            Products products = productList.get(position);

            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();

            checkIsFavourite(holder, position);


            String id = products.getProductId();
            String uid = products.getUid();
            String discountAvailable = products.getDiscount_available();
            String product_name = products.getProduct_name();
            String discount_dec = products.getDiscount_desc();
            String discount_price = products.getDiscountPrice();
            String category = products.getCategory();
            String product_dec = products.getProduct_desc();
            String image = products.getProduct_image();
            String quantity = products.getQuantity();
            String timestamp = products.getTimestamp();
            String original_price = products.getPrice();

            LoadReviews(holder, uid, id, position, products);
            LoadReviewCount(holder,products);

//        if (category.equals("Clothes")){
//            holder.productsDetailsCL.setCardBackgroundColor(context.getResources().getColor(R.color.clothes));
//        }
//        else if (category.equals("Foods")){
//            holder.productsDetailsCL.setCardBackgroundColor(context.getResources().getColor(R.color.foods));
//
//        }else if (category.equals("Vegetables")){
//            holder.productsDetailsCL.setCardBackgroundColor(context.getResources().getColor(R.color.vegetables));
//        }

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
                Picasso.get().load(image).placeholder(R.drawable.picture).into(holder.productImage);

            } catch (Exception e) {
                holder.productImage.setImageResource(R.drawable.picture);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Show_one_product_Activity.class);
                    intent.putExtra("product_image", image);
                    intent.putExtra("discountAvailable", discountAvailable);
                    intent.putExtra("discount_price", discount_price);
                    intent.putExtra("original_price", original_price);
                    intent.putExtra("product_name", product_name);
                    intent.putExtra("quantity", quantity);
                    intent.putExtra("product_dec", product_dec);
                    intent.putExtra("category", category);
                    intent.putExtra("discount_dec", discount_dec);
                    intent.putExtra("Selleruid", uid);
                    intent.putExtra("Productid", id);
                    context.startActivity(intent);
                }
            });

     //   }


    }

    private void LoadReviewCount(HolderUserHomeShowProducts holder,Products products) {

        String sellerUid = products.getUid();
        String productId = products.getProductId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(sellerUid).child("Products");
        reference.child(productId).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    if (snapshot.exists()) {
                        long count = snapshot.getChildrenCount();
                        holder.countReviews.setText("[" + String.valueOf(count) + "]");
                        Log.d("LOADCOUNT",String.valueOf(count));
                    } else {
                        holder.countReviews.setText("[" + "0" + "]");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float ratingSum = 0;

    private void LoadReviews(HolderUserHomeShowProducts holder, String id, String uid, int position, Products products) {

        String selleruid = products.getUid();
        String productId = products.getProductId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(selleruid).child("Products");
        reference.child(productId).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    ratingSum = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                        Log.d("RATE", String.valueOf(rating));
                        ratingSum = ratingSum + rating;
                    }

                    long numberOfReviews = snapshot.getChildrenCount();
                    float avgRating = ratingSum / numberOfReviews;

                    holder.ratingBar.setRating(avgRating);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void checkIsFavourite(HolderUserHomeShowProducts holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Products products = productList.get(position);
        String product_id = products.getProductId();

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
       // return isShimmer?productList.size();
       return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new UserHomeFilterProduct(this, filterList);
        }
        return filter;
    }

    public class HolderUserHomeShowProducts extends RecyclerView.ViewHolder {

        private RoundedImageView productImage;
        private TextView discount_note, product_name, quantity, discount_price, original_price, countReviews;
        ImageButton favBtn;
        RatingBar ratingBar;
        CardView productsDetailsCL;
    //    ShimmerFrameLayout shimmerFrameLayout;

        public HolderUserHomeShowProducts(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            discount_note = itemView.findViewById(R.id.discountnote);
            product_name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            discount_price = itemView.findViewById(R.id.discount_price);
            original_price = itemView.findViewById(R.id.original_price);
            favBtn = itemView.findViewById(R.id.favBtn);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            countReviews = itemView.findViewById(R.id.countReviews);
            productsDetailsCL = itemView.findViewById(R.id.productsDetailsCL);
         //   shimmerFrameLayout = itemView.findViewById(R.id.shimmerFrameLayout);
        }
    }


}
