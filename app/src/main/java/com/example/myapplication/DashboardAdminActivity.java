package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.databinding.ActivityDashboardAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {
    // view binding
    private ActivityDashboardAdminBinding binding;
    //firebase auth
    private FirebaseAuth  firebaseAuth;

    private ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardAdminBinding.inflate(getLayoutInflater());
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
                LoadCategories();
                
            }
        });


        //handle click, start category add screen
     // binding.addCategoryButton.setOnClickListener(new View.OnClickListener() {
       //   @Override
        //  public void onClick(View v) {
          //    startActivity(new Intent(DashboardAdminActivity.this,CategoryAddActivity.class));
          //  }
      // });
   //handle click  start pdf and screen
        binding.addPdfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (DashboardAdminActivity.this,PdfAddActivity.class));
            }
        });

        binding.listerPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (DashboardAdminActivity.this,ListerActivity.class));
            }
        });









    }

    private void LoadCategories() {
        categoryArrayList=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

   categoryArrayList.clear();
    for( DataSnapshot ds:datasnapshot.getChildren()){
        //get data
        ModelCategory model=ds.getValue(ModelCategory.class);
        categoryArrayList.add(model);
    }
    //setup adapter
       adapterCategory=new AdapterCategory(DashboardAdminActivity.this,categoryArrayList);
   // set adapter
    binding.categoriesTv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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