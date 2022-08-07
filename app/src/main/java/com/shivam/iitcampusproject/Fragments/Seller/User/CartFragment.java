package com.shivam.iitcampusproject.Fragments.Seller.User;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.CartItemAdapter;
import com.shivam.iitcampusproject.CartRecyclerViewRemoveInterface;
import com.shivam.iitcampusproject.Model.CartItem;
import com.shivam.iitcampusproject.User.UserHomeActivity;
import com.shivam.iitcampusproject.databinding.FragmentCartBinding;

import java.util.ArrayList;

public class CartFragment extends Fragment implements CartRecyclerViewRemoveInterface {

    FragmentCartBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase database;

   private String sellerUid,deafault_address,mobile_number,main_Address;

    private ArrayList<CartItem> cartItemList;
    CartItemAdapter cartItemAdapter;

   private double SubTotalPrice = 0.0;
   private double FullTotalPrice = 0.0;

    Double latitude,longitude;

    ProgressDialog progressDialog;

    private String UserCartStatus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater,container,false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

//        cartItemList = new ArrayList<>();
//        cartItemAdapter = new CartItemAdapter(getActivity(),cartItemList,this);
//        binding.recycleview.setAdapter(cartItemAdapter);
//
//
//        loadShopProducts();
//        LoadSubTotalPrice();

        return binding.getRoot();
    }


    private void LoadSubTotalPrice() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid());
        databaseReference.child("Cart").orderByChild("status").equalTo("inCart").addListenerForSingleValueEvent(new ValueEventListener() {

            double Total = 0;
            double TotalPrice = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                for (DataSnapshot ds : snapshot.getChildren()){

                    String Item_Price = ""+ds.child("Item_Price").getValue();
                    String totalPrice = ""+ds.child("TotalPrice").getValue();
                    UserCartStatus = ""+ds.child("status").getValue();

                    Log.d("STATUS",UserCartStatus);


                    double value = Double.valueOf(Item_Price);
                    double value1 = Double.valueOf(totalPrice);

                    Total += value;
                    TotalPrice += value1;

                    SubTotalPrice = Total;
                    FullTotalPrice = TotalPrice;


                    binding.sTotalTv.setText(String.valueOf("Rs " + SubTotalPrice));
                    binding.totalTv.setText(String.valueOf("Rs " + FullTotalPrice));
                }

                }else {

                    binding.emptyLL.setVisibility(View.VISIBLE);
                    binding.sTotalTv.setText(String.valueOf("Rs " + "00"));
                    binding.totalTv.setText(String.valueOf("Rs " + "00"));
                }

                cartItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadUserDetails() {

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    String name = ""+snapshot.child("username").getValue();
                 //   String defaultAddress = ""+snapshot.child("deafault_address").getValue();

                    binding.userNameTv.setText(name);
                  //  binding.userAddressTv1.setText(defaultAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShopProducts() {

      //  cartItemList = new ArrayList<>();
        Log.d("cartItemList",""+cartItemList.size());

        DatabaseReference databaseReference = database.getReference("Users").child(mAuth.getUid());
        databaseReference.child("Cart").orderByChild("status").equalTo("inCart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItemList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){
                    CartItem cartItem = ds.getValue(CartItem.class);
                    cartItemList.add(cartItem);

                    String count = ""+snapshot.getChildrenCount();

                    if (count.equals("0")){

                        binding.emptyLL.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.emptyLL.setVisibility(View.GONE);
                    }

                     sellerUid  = cartItem.getSellerUid();

                    if (cartItemList.size()==0){

                        binding.emptyLL.setVisibility(View.VISIBLE);
                    }
                    else {

                        binding.emptyLL.setVisibility(View.GONE);
                    }

                }
//                cartItemAdapter = new CartItemAdapter(getActivity(),cartItemList,this);
//                binding.recycleview.setAdapter(cartItemAdapter);
//                binding.recycleview.setHasFixedSize(true);
                cartItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {

        cartItemList = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(getActivity(),cartItemList,this);
        binding.recycleview.setAdapter(cartItemAdapter);

        loadShopProducts();
        LoadSubTotalPrice();
        LoadUserDetails();
        super.onStart();
    }

    public void refreshUserHomeActivity(){
        UserHomeActivity userHomeActivity = (UserHomeActivity) getActivity();
        if(userHomeActivity!=null){
            userHomeActivity.LoadCartData();
        }
    }

    @Override
    public void onItemClick(int position) {
        LoadSubTotalPrice();
        refreshUserHomeActivity();

        }

}