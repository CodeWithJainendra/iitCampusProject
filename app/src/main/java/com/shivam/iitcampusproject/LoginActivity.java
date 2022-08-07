package com.shivam.iitcampusproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivam.iitcampusproject.Seller.SellerHomeActivity;
import com.shivam.iitcampusproject.User.UserHomeActivity;
import com.shivam.iitcampusproject.admin.AdminHomeActivity;
import com.shivam.iitcampusproject.databinding.ActivityLoginBinding;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {


    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;

    private static final int RC_SIGN_IN = 65;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // Configure Google Sign In
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        binding.btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, UserRegisterActivity.class));
            }
        });

        binding.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        binding.forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadForgotPassLayout();
            }
        });
    }

    private void LoadForgotPassLayout() {

        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.forgot_password);

        EditText emailEt = bottomSheetDialog.findViewById(R.id.txtemail);
        TextView btnforgetPass = bottomSheetDialog.findViewById(R.id.btnforgetPass);

        bottomSheetDialog.show();

        btnforgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                progressDialog.setMessage("Sending Email....");
                progressDialog.show();
                String email = emailEt.getText().toString();
                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this,"Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }

    private String email = "", password = "";
    private void validateData() {

        email = binding.txtemail.getText().toString().trim();
        password = binding.txtpassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            Toast.makeText(LoginActivity.this, "Invalid Email Pattern..", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {

            Toast.makeText(LoginActivity.this, "Enter Password..", Toast.LENGTH_SHORT).show();
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        progressDialog.setMessage("Logging In....");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUser() {
        progressDialog.setMessage("Checking User.....");

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        DatabaseReference reference = database.getReference("Users");
        reference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                String userType = "" + snapshot.child("userType").getValue();
                String approval = "" + snapshot.child("Is_approval").getValue();

                if (userType.equals("user")) {

                    startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                    finish();

                } else if (userType.equals("seller")) {

                    if (approval.equals("wait")) {
                        ShowWaitingMessage();
                    }
                    else if (approval.equals("notApproved")) {
                        ShownotApprovedMessage();
                    }
                    else if (approval.equals("approved")) {

                        startActivity(new Intent(LoginActivity.this, SellerHomeActivity.class));
                        finish();
                    }

                } else if (email.equals("appadmin@gmail.com") && password.equals("654321")){

                    startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShownotApprovedMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Not Approved")
                .setMessage("Your request is Not Approved. Please create new seller account with different email.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    private void ShowWaitingMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wait")
                .setMessage("Your request is pending. Please wait for admin approval...")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("GOOGLEFAILED",e.getMessage());
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            //if user is signing in first time then get and show user info from google account
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                String email  = user.getEmail();
                                String uid  = user.getUid();
                                String username = user.getDisplayName();
                                String image = user.getPhotoUrl().toString();
                                long timestamp = System.currentTimeMillis();

                                HashMap<Object,String> hashMap = new HashMap<>();
                                hashMap.put("username",username);
                                hashMap.put("email",email);
                                hashMap.put("mobile","Empty");
                                hashMap.put("city","Empty");
                                hashMap.put("photoUrl",""+image);
                                hashMap.put("timestamp",String.valueOf(timestamp));
                                hashMap.put("address_main","Empty");
                                hashMap.put("address_second","Empty");
                                hashMap.put("userType","user");

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);
                            }



                            startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                            finish();
                            //  updateUI(user);
                        } else {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}