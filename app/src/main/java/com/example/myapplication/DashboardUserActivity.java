package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.databinding.ActivityDashboardAdminBinding;
import com.example.myapplication.databinding.ActivityDashboardUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardUserActivity extends AppCompatActivity {
    // view binding
    private ActivityDashboardUserBinding binding;
    private FirebaseAuth firebaseAuth;
    //firebase auth






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
        //handle click ,logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
        binding.listerPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (DashboardUserActivity.this,ListerActivity.class));
            }
        });
    }
    private void checkUser() {
        //getcurrent User
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if (firebaseUser==null){
            //not logged in
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        else {
            //logged in
            String email=firebaseUser.getEmail();
            //set in textview of toolbar
            binding.subTitleTv.setText(email);




        }
    }


}