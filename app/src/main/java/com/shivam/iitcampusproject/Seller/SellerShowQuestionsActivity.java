package com.shivam.iitcampusproject.Seller;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Adapter.QuestionsAdapter;
import com.shivam.iitcampusproject.Model.Questions;
import com.shivam.iitcampusproject.databinding.ActivityShowQuestionsBinding;

import java.util.ArrayList;

public class SellerShowQuestionsActivity extends AppCompatActivity {

    ActivityShowQuestionsBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    boolean isSeller = true;

    private ArrayList<Questions> questionsList;
    QuestionsAdapter questionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        LoadQuestions();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void LoadQuestions() {

        questionsList = new ArrayList<>();

        DatabaseReference databaseReference = database.getReference("Questions");
        databaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionsList.clear();

                if (snapshot.exists()){

                    for (DataSnapshot ds : snapshot.getChildren()){

                        Questions questions = ds.getValue(Questions.class);
                        questionsList.add(questions);

                    }
                    questionsAdapter = new QuestionsAdapter(SellerShowQuestionsActivity.this,questionsList,isSeller);
                    binding.recycleviewQuestion.setAdapter(questionsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SellerShowQuestionsActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}