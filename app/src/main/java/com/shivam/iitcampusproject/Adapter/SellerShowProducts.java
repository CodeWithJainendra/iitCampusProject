package com.shivam.iitcampusproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shivam.iitcampusproject.Filters.FilterProduct;
import com.shivam.iitcampusproject.Model.Products;
import com.shivam.iitcampusproject.R;
import com.shivam.iitcampusproject.Seller.Seller_Edit_Products_Activity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SellerShowProducts extends RecyclerView.Adapter<SellerShowProducts.HolderSellerShowProducts> implements Filterable {

    private Context context;
    public ArrayList<Products> productList,filterList;
    private FilterProduct filter;

    public SellerShowProducts(Context context, ArrayList<Products> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderSellerShowProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_viewholder,parent,false);
        return new HolderSellerShowProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSellerShowProducts holder, int position) {

        Products products = productList.get(position);

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

        holder.product_name.setText(product_name);
        holder.quantity.setText(quantity);
        holder.discount_note.setText(discount_dec);
        holder.discount_price.setText("Rs."+discount_price);
        holder.original_price.setText("Rs."+original_price);

        if (discountAvailable.equals("true")){

            holder.discount_price.setVisibility(View.VISIBLE);
            holder.discount_note.setVisibility(View.VISIBLE);
            holder.original_price.setPaintFlags(holder.original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            holder.discount_price.setVisibility(View.GONE);
            holder.discount_note.setVisibility(View.GONE);
            holder.original_price.setPaintFlags(0);
        }
        try {
            Picasso.get().load(image).placeholder(R.drawable.picture).into(holder.productImage);

        }catch (Exception e){
            holder.productImage.setImageResource(R.drawable.picture);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getRootView().getContext());
                bottomSheetDialog.setContentView(R.layout.seller_product_details);

                ImageButton btn_back = bottomSheetDialog.findViewById(R.id.btnback);
                ImageButton btn_delete = bottomSheetDialog.findViewById(R.id.btndelete);
                ImageButton btn_edit = bottomSheetDialog.findViewById(R.id.btnedit);
                ImageView txt_product_image = bottomSheetDialog.findViewById(R.id.product_image);
                TextView txt_discount_dec = bottomSheetDialog.findViewById(R.id.discount_dec);
                TextView txt_product_name = bottomSheetDialog.findViewById(R.id.product_name);
                TextView txt_product_dec = bottomSheetDialog.findViewById(R.id.product_dec);
                TextView txt_category = bottomSheetDialog.findViewById(R.id.category);
                TextView txt_quantity = bottomSheetDialog.findViewById(R.id.quantity);
                TextView txt_discount_price = bottomSheetDialog.findViewById(R.id.discount_price);
                TextView txt_price = bottomSheetDialog.findViewById(R.id.price);

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

                txt_product_name.setText(product_name);
                txt_product_dec.setText(product_dec);
                txt_category.setText(category);
                txt_quantity.setText(quantity);
                txt_discount_dec.setText(discount_dec);
                txt_discount_price.setText(""+discount_price);
                txt_price.setText(""+original_price);

                if (discountAvailable.equals("true")){

                    txt_discount_price.setVisibility(View.VISIBLE);
                    txt_discount_dec.setVisibility(View.VISIBLE);
                    txt_price.setPaintFlags(txt_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                }else {
                    txt_discount_price.setVisibility(View.GONE);
                    txt_discount_dec.setVisibility(View.GONE);
                }

                try {
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(txt_product_image);

                }catch (Exception e){
                    txt_product_image.setImageResource(R.drawable.profile);

                }
                bottomSheetDialog.show();

                btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        Intent intent = new Intent(context, Seller_Edit_Products_Activity.class);
                        intent.putExtra("productId",id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Delete").setMessage("Are you sure want to delete Product"+ product_name+"?")
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteProduct(id);
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });

    }

    private void deleteProduct(String id) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Products");
        reference1.child(id).removeValue();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mAuth.getUid()).child("Products").child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context,"Product deleted Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterProduct(this,filterList);
        }
        return filter;
    }

    public class HolderSellerShowProducts extends RecyclerView.ViewHolder {


        private RoundedImageView productImage;
        private TextView discount_note,product_name,quantity,discount_price,original_price;


        public HolderSellerShowProducts(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            discount_note = itemView.findViewById(R.id.discountnote);
            product_name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            discount_price = itemView.findViewById(R.id.discount_price);
            original_price = itemView.findViewById(R.id.original_price);
        }
    }
}
