package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DatabaseMetaData;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    //firebase Auth
    private FirebaseAuth firebaseAuth;

//progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth= FirebaseAuth.getInstance();
     // setup progress
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("please wait ");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click,go back
        binding.backBtn.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

      //handle click ,began register
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            validateDate();
            }
        });
    }

    private String name = "",email="",password="";
    private void validateDate() {
// get data
        name=binding.nameEt.getText().toString().trim();
        email=binding.emailEt.getText().toString().trim();
        password=binding.passwordEt.getText().toString().trim();
        String cPassword=binding.cPasswordEt.getText().toString().trim();
//validate
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"enter your name",Toast.LENGTH_SHORT).show();

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid email Patter ...!",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter Password ...!",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cPassword)){
            Toast.makeText(this,"Confirm Password ...!",Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(cPassword)){
            Toast.makeText(this,"password dosent match...!",Toast.LENGTH_SHORT).show();
        }
        else {
           createUserAccount();

        }




    }

    private void createUserAccount() {
//show progress
        progressDialog.setMessage("creating account ...");
        progressDialog.show();

        //create user in auth
        firebaseAuth.createUserWithEmailAndPassword(email,password)
        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
       //account creation succes ,now add in firebase realtime
                updateUserInfo();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
             // account cration failed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateUserInfo() {
         progressDialog.setMessage("Saving user info ...");
         //timetap
        long timestamp=System.currentTimeMillis();
        //get current user uid,since user is registred
        String uid=firebaseAuth.getUid();
        // setup data to add in db
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("name",name);
        hashMap.put("email",email);
        hashMap.put("profileImage","");
        hashMap.put("userType","user");
        hashMap.put("timestamp",timestamp);
        // set data to db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                 //data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,"Account created ...",Toast.LENGTH_SHORT).show();
                        //since user is created start dashboard activity
                        startActivity(new Intent(RegisterActivity.this,DashboardUserActivity.class));
                      finish();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                     //data failed adding ton db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });









    }
}