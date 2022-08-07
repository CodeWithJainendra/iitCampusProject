package com.shivam.iitcampusproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivam.iitcampusproject.databinding.ActivitySellerRegisterBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class SellerRegisterActivity extends AppCompatActivity {

    ActivitySellerRegisterBinding binding;

    private ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    ArrayList<String> arrayList;

    long latitude = 0;
    long longitude = 0;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait........");
        progressDialog.setCanceledOnTouchOutside(false);


        arrayList = new ArrayList<>();





        arrayList.add("Afghanistan");
        arrayList.add("Albania");
        arrayList.add("Algeria");
        arrayList.add("Andorra");
        arrayList.add("Angola");
        arrayList.add("Antigua and Barbuda");
        arrayList.add("Argentina");
        arrayList.add("Armenia");
        arrayList.add("Australia");
        arrayList.add("Austria");
        arrayList.add("The Bahamas");
        arrayList.add("Bahrain");
        arrayList.add("Bangladesh");
        arrayList.add("Barbados");
        arrayList.add("Belarus");
        arrayList.add("Belgium");
        arrayList.add("Belize");
        arrayList.add("Benin");
        arrayList.add("Bhutan");
        arrayList.add("Bolivia");
        arrayList.add("Bosnia and Herzegovina");
        arrayList.add("Botswana");
        arrayList.add("Brazil");
        arrayList.add("Bulgaria");
        arrayList.add("Cambodia");
        arrayList.add("Cameroon");
        arrayList.add("Canada");
        arrayList.add("Central African Republic");
        arrayList.add("Chad");
        arrayList.add("Chile");
        arrayList.add("China");
        arrayList.add("Colombia");
        arrayList.add("Comoros");
        arrayList.add("Cyprus");
        arrayList.add("Denmark");
        arrayList.add("Djibouti");
        arrayList.add("Dominica");
        arrayList.add("Dominican Republic");
        arrayList.add("East Timor (Timor-Leste)");
        arrayList.add("Ecuador");
        arrayList.add("Egypt");
        arrayList.add("El Salvador");
        arrayList.add("Eswatini");
        arrayList.add("Eritrea");
        arrayList.add("Estonia");
        arrayList.add("Finland");
        arrayList.add("France");
        arrayList.add("Fiji");
        arrayList.add("Gabon");
        arrayList.add("The Gambia");
        arrayList.add("Georgia");
        arrayList.add("Germany");
        arrayList.add("Ghana");
        arrayList.add("Greece");
        arrayList.add("Grenada");
        arrayList.add("Guatemala");
        arrayList.add("Guinea");
        arrayList.add("Guinea-Bissau");
        arrayList.add("Haiti");
        arrayList.add("Honduras");
        arrayList.add("Hungary");
        arrayList.add("Iceland");
        arrayList.add("India");
        arrayList.add("Indonesia");
        arrayList.add("Iran");
        arrayList.add("Iraq");
        arrayList.add("Ireland");
        arrayList.add("Italy");
        arrayList.add("Japan");
        arrayList.add("Jamaica");
        arrayList.add("Jordan");
        arrayList.add("Kazakhstan");
        arrayList.add("Kenya");
        arrayList.add("Kiribati");
        arrayList.add("Korea, North");
        arrayList.add("Korea, South");
        arrayList.add("Kosovo");
        arrayList.add("Kuwait");
        arrayList.add("Laos");
        arrayList.add("Latvia");
        arrayList.add("Lebanon");
        arrayList.add("Lesotho");
        arrayList.add("Liberia");
        arrayList.add("Libya");
        arrayList.add("Liechtenstein");
        arrayList.add("Lithuania");
        arrayList.add("Mexico");
        arrayList.add("Moldova");
        arrayList.add("Monaco");
        arrayList.add("Mongolia");
        arrayList.add("Mauritius");
        arrayList.add("Other.....");

        binding.txtcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadCityDialog();
            }
        });

        binding.sellersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateData();
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerRegisterActivity.this,LoginActivity.class));
            }
        });
    }

    private String username = "", email = "", mobile ="", city ="", address ="", shopname = "", password = "",con_password ="";
    private void ValidateData() {

        username = binding.txtusername.getText().toString().trim();
        email = binding.txtemail.getText().toString().trim();
        mobile = binding.txtmobile.getText().toString().trim();
        city = binding.txtcity.getText().toString().trim();
        address = binding.txtaddress.getText().toString().trim();
        shopname = binding.txtshopname.getText().toString().trim();
        password = binding.txtpassword.getText().toString().trim();
        con_password = binding.txtconPass.getText().toString().trim();


        if (TextUtils.isEmpty(username)){
            binding.txtusername.setError("Username Required");
        }
        else if (TextUtils.isEmpty(email)){
            binding.txtemail.setError("Email Required");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.txtemail.setError("Invalid Email Pattern");
        }
        else if (TextUtils.isEmpty(mobile)){
            binding.txtmobile.setError("Mobile Number Required");
        }
        else if (mobile.length() < 10){
            binding.txtmobile.setError("Invalid Number");
        }
        else if (TextUtils.isEmpty(city)){
            binding.txtcity.setError("City Required");
        }
        else if (TextUtils.isEmpty(address)){
            binding.txtaddress.setError("Address Required");
        }
        else if (TextUtils.isEmpty(shopname)){
            binding.txtshopname.setError("Shop Name Required");
        }
        else if (TextUtils.isEmpty(password)){
            binding.txtpassword.setError("Password Required");
        }
        else if (TextUtils.isEmpty(con_password)){
            binding.txtconPass.setError("Confirm Password Required");
        }
        else if (!password.equals(con_password)){

            Toast.makeText(SellerRegisterActivity.this,"Password doesn't match..!",Toast.LENGTH_SHORT).show();
        }
        else {
            createUserAccount();
        }

    }

    private void createUserAccount() {
        progressDialog.setMessage("Creating Account....");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                updateUserInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SellerRegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info....");

        long timestamp = System.currentTimeMillis();

        String uid = mAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username",username);
        hashMap.put("email",email);
        hashMap.put("mobile",mobile);
        hashMap.put("city",city);
        hashMap.put("address",address);
        hashMap.put("shopName",shopname);
        hashMap.put("password",password);
        hashMap.put("delivery_fee","Empty");
        hashMap.put("latitude", latitude);
        hashMap.put("longitude", longitude);
        hashMap.put("Shop_Status", "Closed");
        hashMap.put("photoUrl", "");
        hashMap.put("Uid", mAuth.getUid());
        hashMap.put("Is_approval","wait");
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("userType","seller");
        
        DatabaseReference reference = database.getReference("Users");
        reference.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                SubmitAminAdata(String.valueOf(timestamp));
                progressDialog.dismiss();
                Toast.makeText(SellerRegisterActivity.this,"Account Created....",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SellerRegisterActivity.this,LoginActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SellerRegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SubmitAminAdata(String timestamp) {

        DatabaseReference reference1 = database.getReference("AdminChecking");
        String key = reference1.push().getKey();

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("username",username);
        hashMap1.put("email",email);
        hashMap1.put("mobile",mobile);
        hashMap1.put("city",city);
        hashMap1.put("address",address);
        hashMap1.put("shopName",shopname);
        hashMap1.put("password",password);
        hashMap1.put("Shop_Status", "Closed");
        hashMap1.put("Uid", mAuth.getUid());
        hashMap1.put("Is_approval","wait");
        hashMap1.put("timestamp",""+timestamp);
        hashMap1.put("userType","seller");
        hashMap1.put("key",""+key);

        reference1.child(key).setValue(hashMap1);

    }

    private void LoadCityDialog() {

        dialog = new Dialog(SellerRegisterActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_city);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(SellerRegisterActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                binding.txtcity.setText(adapter.getItem(i));
                dialog.dismiss();
            }
        });

    }
}