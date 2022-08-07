package com.shivam.iitcampusproject.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivam.iitcampusproject.Model.AdminCheckSeller;
import com.shivam.iitcampusproject.R;

import java.util.ArrayList;
import java.util.Calendar;

public class SellerAdapter  extends RecyclerView.Adapter<SellerAdapter.HolderSeller> {

    private Context context;
    private ArrayList<AdminCheckSeller> adminCheckSellersList;


    public SellerAdapter(Context context, ArrayList<AdminCheckSeller> adminCheckSellersList) {
        this.context = context;
        this.adminCheckSellersList = adminCheckSellersList;
    }

    @NonNull
    @Override
    public HolderSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_seller_req, parent, false);
        return new HolderSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSeller holder, int position) {

        AdminCheckSeller adminCheckSeller = adminCheckSellersList.get(position);

        String shopeName = adminCheckSeller.getShopName();
        String mobile = adminCheckSeller.getMobile();
        String address = adminCheckSeller.getAddress();
        String city = adminCheckSeller.getCity();
        String email = adminCheckSeller.getEmail();
        String accountCreatedate = adminCheckSeller.getTimestamp();
        String sellerId = adminCheckSeller.getUid();
        String key = adminCheckSeller.getKey();

        //change timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(accountCreatedate));
        String formatDate = DateFormat.format("dd/MM/yyyy",calendar).toString();



        holder.shop_name.setText(shopeName);
        holder.phone.setText(mobile);
        holder.address.setText(address);
        holder.city.setText(city);
        holder.email.setText(email);
        holder.date.setText(formatDate);

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateStatus(sellerId,key);
            }
        });

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDeclineStatus(sellerId,key);
            }
        });
    }

    private void UpdateDeclineStatus(String sellerId, String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(sellerId).child("Is_approval").setValue("notApproved").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                RemoveSellerStatusFromAdmin(sellerId,key);
                Toast.makeText(context,"Seller Approved",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RemoveSellerStatusFromAdmin(String sellerId, String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AdminChecking");
        databaseReference.child(key).child("Is_approval").setValue("notApproved");
    }

    private void UpdateStatus(String sellerId, String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(sellerId).child("Is_approval").setValue("approved").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

             //   String message = "Account is " + "approved";
                RemoveSellerFromAdmin(sellerId,key);
             //   prepareNotificationMessage(sellerId,message);
                Toast.makeText(context,"Seller Approved",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void RemoveSellerFromAdmin(String sellerId, String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AdminChecking");
        databaseReference.child(key).child("Is_approval").setValue("approved");
    }

//    private void prepareNotificationMessage(String sellerId, String message){
//        //when seller changed order,send notification to buyer
//
//        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
//        String NOTIFICATION_TITLE = "Account Verify ";
//        String NOTIFICATION_MESSAGE = ""+message;
//        String NOTIFICATION_TYPE = "AccountStatus";
//
//        //prepare json(what to send where to send)
//        JSONObject notificationJo = new JSONObject();
//        JSONObject notificationBodyJo = new JSONObject();
//
//        try {
//            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
//            notificationBodyJo.put("sellerUid",sellerId);
//            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
//            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);
//
//            //where to send
//            notificationJo.put("to",NOTIFICATION_TOPIC); //to all who subscribed to this topic
//            notificationJo.put("data",notificationBodyJo);
//
//        }catch (Exception e){
//            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
//        }
//
//        sendFcmNotification(notificationJo);
//    }
//
//    private void sendFcmNotification(JSONObject notificationJo) {
//
//        //send volley request
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                //notification sent
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //notification failed
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                //put required headers
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type","application/json");
//                headers.put("Authorization","key="+Constants.FCM_KEY);
//
//                return headers;
//
//            }
//        };
//
//        //enque the volley request
//        Volley.newRequestQueue(context).add(jsonObjectRequest);
//    }

    @Override
    public int getItemCount() {
        return adminCheckSellersList.size();
    }

    public class HolderSeller extends RecyclerView.ViewHolder {

        TextView shop_name,city,phone,address,email,date;
        ImageView acceptBtn,removeBtn;

        public HolderSeller(@NonNull View itemView) {
            super(itemView);

            shop_name = itemView.findViewById(R.id.shop_name);
            city = itemView.findViewById(R.id.city);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            email = itemView.findViewById(R.id.email);
            date = itemView.findViewById(R.id.date);
        }
    }
}
