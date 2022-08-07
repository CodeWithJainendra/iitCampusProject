package com.shivam.iitcampusproject.Fragments.Seller.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.UserShowFavourites;
import com.shivam.iitcampusproject.Model.Favourites;
import com.shivam.iitcampusproject.databinding.FragmentFavouriteBinding;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {

    FragmentFavouriteBinding binding;

    private ArrayList<Favourites> favouritesList;
    private UserShowFavourites adapteruserShowFavourites;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Favourites favourites;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        LoadFavouriteProducts();


        return binding.getRoot();
    }

    private void LoadFavouriteProducts() {

        favouritesList = new ArrayList<>();
        databaseReference = database.getReference("Users").child(mAuth.getUid());
        databaseReference.child("Favourites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favouritesList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    favourites = ds.getValue(Favourites.class);

                    favouritesList.add(favourites);
                }
                adapteruserShowFavourites = new UserShowFavourites(getActivity(), favouritesList);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                binding.recycleview.setLayoutManager(staggeredGridLayoutManager);
                binding.recycleview.setAdapter(adapteruserShowFavourites);
                adapteruserShowFavourites.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}