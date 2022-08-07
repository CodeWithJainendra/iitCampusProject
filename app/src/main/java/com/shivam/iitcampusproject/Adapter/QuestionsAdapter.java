package com.shivam.iitcampusproject.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Model.Questions;
import com.shivam.iitcampusproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    private Context context;
    private ArrayList<Questions> questionsList;
    boolean isSeller;
    Dialog dialog;
    String name,answer,sellerUid,Uid;
    ProgressDialog progressDialog;

    public QuestionsAdapter(Context context, ArrayList<Questions> questionsList, boolean isSeller) {
        this.context = context;
        this.questionsList = questionsList;
        this.isSeller = isSeller;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_questions, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {

        Questions questions = questionsList.get(position);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        String timestamp = questions.getTimestamp();
        String username = questions.getUsername();
        String question = questions.getQuestion();
        answer = questions.getAnswer();
        String image = questions.getProductImage();
        String userimage = questions.getUserImage();
        String product_id = questions.getProductId();
        sellerUid = questions.getSelleruid();
        Uid = questions.getUid();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.nameTv.setText(username);
        holder.dateTv.setText(dateFormat);
        holder.QuestionTv.setText(question);


        LoadData(holder,image,userimage,answer,product_id,question,timestamp);

    }


    private void LoadData(QuestionViewHolder holder, String image, String userimage, String answer, String product_id, String question, String timestamp) {

        if (isSeller){

            holder.replyAnswer.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.home_background));

            try {
                Picasso.get().load(image).placeholder(R.drawable.picture).into(holder.product_image);

            }catch (Exception e){

                holder.product_image.setImageResource(R.drawable.picture);
            }

            if (answer.equals("Empty")){

                holder.AnswerTv.setVisibility(View.GONE);

            }else {

                holder.AnswerTv.setVisibility(View.VISIBLE);
                holder.AnswerTv.setText("Reply : "+answer);
            }

            holder.replyAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LoadAnswerDialog(image,product_id,question,timestamp);
                }
            });

        }

        else{

            try {
                Picasso.get().load(userimage).placeholder(R.drawable.profile).into(holder.product_image);
             ///   Log.d("IMAGE",""+userimage);

            }catch (Exception e){

                holder.product_image.setImageResource(R.drawable.profile);
            }

            if (answer.equals("Empty")){

                holder.replyAnswer.setVisibility(View.VISIBLE);
                holder.AnswerTv.setVisibility(View.GONE);
            }
            else {

                holder.replyAnswer.setVisibility(View.GONE);
                holder.AnswerTv.setVisibility(View.VISIBLE);
                //holder.product_image.setVisibility(View.GONE);
                holder.AnswerTv.setText("Reply : "+answer);
            }

            holder.replyAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOptionsPickerDialog(question,sellerUid,timestamp,product_id);
                    //Toast.makeText(context,"Hello",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showOptionsPickerDialog(String question, String sellerUid, String timestamp, String product_id) {
        String[] options = {"Edit","Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Option").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    LoadAskQuestionDialog(question,sellerUid,timestamp,product_id);

                } else {

                    DeleteQuestion(sellerUid,timestamp,product_id);

                }
            }
        });
        builder.show();

    }

    private void DeleteQuestion(String sellerUid, String timestamp, String product_id) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sellerUid).child("Products").child(product_id).child("Questions");
        databaseReference.child(timestamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DeleteSellerQuestion(sellerUid,timestamp);
                Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DeleteSellerQuestion(String sellerUid, String timestamp) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Questions").child(sellerUid);
        databaseReference.child(timestamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadAnswerDialog(String image, String product_id, String question, String timestamp) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.row_quesion_details);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        CircleImageView imageView = dialog.findViewById(R.id.product_image);
        TextView product_name = dialog.findViewById(R.id.product_name);
        TextView discount_decTv = dialog.findViewById(R.id.discount_dec);
        TextView product_decTv = dialog.findViewById(R.id.product_dec);
        TextView categoryTv = dialog.findViewById(R.id.category);
        TextView quantityTv = dialog.findViewById(R.id.quantity);
        TextView Discount_priceTv = dialog.findViewById(R.id.discount_price);
        TextView priceTv = dialog.findViewById(R.id.price);
        TextView questionTv = dialog.findViewById(R.id.questionTv);
        TextView answer_text = dialog.findViewById(R.id.answer_text);
        TextView btnSubmit = dialog.findViewById(R.id.btnSubmit);


        LoadProductDetails(product_id,product_name,discount_decTv,product_decTv,categoryTv,quantityTv,Discount_priceTv,priceTv,questionTv,question);

        Picasso.get().load(image).placeholder(R.drawable.profile).into(imageView);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitAnswer(answer_text,product_id,timestamp);
            }
        });


    }

    private void SubmitAnswer(TextView answer_text, String product_id, String timestamp) {

        String answer = answer_text.getText().toString().trim();

        if (TextUtils.isEmpty(answer)) {
            answer_text.setError("Please enter answer..");

        } else {

            progressDialog.setMessage("Submitting Answer....");
            progressDialog.show();

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("answer", answer);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sellerUid).child("Products").child(product_id).child("Questions");
            databaseReference.child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    UpdatedUserAnswer(hashMap,product_id,timestamp);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

    private void UpdatedUserAnswer(HashMap<String, Object> hashMap, String product_id, String timestamp) {

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Questions").child(sellerUid).child(timestamp);
        databaseReference1.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dialog.dismiss();
                progressDialog.dismiss();
                Toast.makeText(context,"Successfully Submitted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadProductDetails(String product_id, TextView product_name, TextView discount_decTv, TextView product_decTv, TextView categoryTv, TextView quantityTv, TextView discount_priceTv, TextView priceTv, TextView questionTv, String question) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Products");
        databaseReference.child(product_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    name = ""+snapshot.child("product_name").getValue();
                    String discountAvailable = ""+snapshot.child("discount_available").getValue();
                    String discount_dec = ""+snapshot.child("discount_desc").getValue();
                    String discount_price = ""+snapshot.child("discountPrice").getValue();
                    String category = ""+snapshot.child("category").getValue();
                    String product_desc = ""+snapshot.child("product_desc").getValue();
                    String quantity = ""+snapshot.child("quantity").getValue();
                    String original_price = ""+snapshot.child("price").getValue();

                    if (discountAvailable.equals("true")){

                        discount_priceTv.setVisibility(View.VISIBLE);
                        discount_decTv.setVisibility(View.VISIBLE);
                        priceTv.setPaintFlags(priceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    }else {
                        discount_priceTv.setVisibility(View.GONE);
                        discount_decTv.setVisibility(View.GONE);
                        priceTv.setPaintFlags(0);
                    }

                    product_name.setText(name);
                    quantityTv.setText(quantity);
                    discount_decTv.setText(discount_dec);
                    discount_priceTv.setText("Rs."+discount_price);
                    priceTv.setText("Rs."+original_price);
                    categoryTv.setText(category);
                    product_decTv.setText(product_desc);
                    questionTv.setText(question);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    String Question;
    private void LoadAskQuestionDialog(String question, String sellerUid, String timestamp, String product_id) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_ask_question);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.edit_text);
        TextView btnSubmit = dialog.findViewById(R.id.btnSubmit);
        TextView titileTv = dialog.findViewById(R.id.titileTv);

        editText.setText(question);
        btnSubmit.setText("Update");
        titileTv.setText("Update Question");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Question = editText.getText().toString().trim();

                if (TextUtils.isEmpty(Question)){
                    editText.setError("Please enter question..");
                }
                else {

                    progressDialog.setMessage("Update Question");
                    progressDialog.show();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("question",Question);

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Questions").child(sellerUid);
                    databaseReference1.child(String.valueOf(timestamp)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateUserQuestion(sellerUid,timestamp,hashMap,product_id);
                            progressDialog.dismiss();
                            dialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void updateUserQuestion(String sellerUid, String timestamp, HashMap<String, Object> hashMap, String product_id) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sellerUid).child("Products").child(product_id).child("Questions");
        databaseReference.child(String.valueOf(timestamp)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context,"Update Question Successfully..",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv,dateTv,QuestionTv,AnswerTv;
        CircleImageView product_image;
        ImageButton replyAnswer;
        CardView cardView;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            QuestionTv = itemView.findViewById(R.id.QuestionTv);
            AnswerTv = itemView.findViewById(R.id.AnswerTv);
            product_image = itemView.findViewById(R.id.product_image);
            replyAnswer = itemView.findViewById(R.id.replyAnswer);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
