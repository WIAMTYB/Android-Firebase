package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityLoginBinding;
import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

//firebase
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth= FirebaseAuth.getInstance();
        // setup progress
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("please wait ");
        progressDialog.setCanceledOnTouchOutside(false);




        //handle click,go to register screen
        binding.noAccountTv.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        //handle click,go to register screen
        binding.loginBtn.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
        validateDate();            }
        });



    }
    private String name = "",email="",password="";
    private void validateDate() {
        //before login so some data validation
        email=binding.emailEt.getText().toString().trim();
        password=binding.passwordEt.getText().toString().trim();


        //validate
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid email Patter ...!",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter Password ...!",Toast.LENGTH_SHORT).show();
        }
        else
            loginUser();

    }

    private void loginUser() {
        //show progress
       progressDialog.setMessage("Loggin in ...");
       progressDialog.show();
       //login user
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                    //login succes check if user is user or admin
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                  //login failure
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });



    }

    private void checkUser() {
        progressDialog.setMessage("Checking user ...");
        //from realtime database
        //get current user
        FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        //getUser type
                        String userType= ""+snapshot.child("userType").getValue();
                        // check user type
                        if (userType.equals("user")){
                            //this is simple user ,open user dashboard
                            startActivity(new Intent (LoginActivity.this,DashboardUserActivity.class));
                            finish();

                        }
                        else if(userType.equals("admin")){
                            //this is admin user ,open admin dashboard
                            startActivity(new Intent (LoginActivity.this,DashboardAdminActivity.class));
                            finish();
                        }

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}