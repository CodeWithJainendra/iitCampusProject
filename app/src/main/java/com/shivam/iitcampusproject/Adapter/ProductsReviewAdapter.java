package com.shivam.iitcampusproject.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Model.ProductRating;
import com.shivam.iitcampusproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductsReviewAdapter extends RecyclerView.Adapter<ProductsReviewAdapter.HoldereProducrsReview> {

    private Context context;
    private ArrayList<ProductRating> productRatingList;

    public ProductsReviewAdapter(Context context, ArrayList<ProductRating> productRatingList) {
        this.context = context;
        this.productRatingList = productRatingList;
    }

    @NonNull
    @Override
    public HoldereProducrsReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_reviews,parent,false);
        return new HoldereProducrsReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HoldereProducrsReview holder, int position) {

        ProductRating productRating = productRatingList.get(position);

        String uid = productRating.getUserUid();
        String ratings = productRating.getRatings();
        String timestamp = productRating.getTimestamp();
        String review = productRating.getReview();


        LoadUserDetails(productRating,holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.reviewTv.setText(review);
        holder.dateTv.setText(dateFormat);
    }

    private void LoadUserDetails(ProductRating productRating, HoldereProducrsReview holder) {

        String uid = productRating.getUserUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = ""+snapshot.child("username").getValue();
                String image = ""+snapshot.child("photoUrl").getValue();

                holder.nameTv.setText(name);

                try {
                    Picasso.get().load(image).placeholder(R.drawable.shop).into(holder.profile_image);

                }catch (Exception e){

                    holder.profile_image.setImageResource(R.drawable.shop);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productRatingList.size();
    }

    public class HoldereProducrsReview extends RecyclerView.ViewHolder {

        CircleImageView profile_image;
        TextView nameTv,dateTv,reviewTv;
        RatingBar ratingBar;

        public HoldereProducrsReview(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
