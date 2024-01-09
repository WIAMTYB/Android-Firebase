package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {
    private ActivityPdfAddBinding binding;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private Uri pdfUri=null;
    private static final int  PDF_PICK_CODE=1000;

    //tag for debugin
    private static final String  TAG="ADD_PDF_TAG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        //setup progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        // handle click  go to previous activity

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        // handle click attach pdf
        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              pdfPickIntent();
            }
        });
        //for categorie nop
        //handle click,upload pdf
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //validate data
                validateData();
            }
        });


    }
private String title="",description="";
    private void validateData() {
        // Step 1 validate data
        Log.d(TAG,"Validation Data ...");

        // get data
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        //validate data
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show();

        }
        else if (pdfUri==null){
            Toast.makeText(this, "Pick pdf", Toast.LENGTH_SHORT).show();

        }
        else{
            //all data is valid can u upload now
            uploadPdfToStorage();
        }
    }

    private void uploadPdfToStorage() {
        //step 2 upload file to firebase storage
        Log.d(TAG,"uploadPdfToStorage: uploading to storage...");
        //show progress
        progressDialog.setMessage("Uploading Pdf ...");
        progressDialog.show();

        //timestamp
        long timestamp=System.currentTimeMillis();

        //path of pdf in firebase storage
        String filePathAndName="Books/"+timestamp;
        //storage referce
        StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.d(TAG,"onSucces:PDF uploaded to storage ");
                        Log.d(TAG,"onSucces:getting pdf url" );
                        //get pdf url
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                         while(!uriTask.isSuccessful());
                         String uploadPdfUrl=""+uriTask.getResult();
                         // upload to firebase db
                        uploadPdfInfoToDb(uploadPdfUrl,timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure:PDF upload failed due to "+e.getMessage());
                   Toast.makeText(PdfAddActivity.this,"onFailure:PDF upload failed due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void uploadPdfInfoToDb(String uploadPdfUrl, long timestamp) {
        //step 2 upload file to db
        Log.d(TAG,"uploadPdfInfoToDb: uploading to db...");
        progressDialog.setMessage("Uploading pdf info ");
        String uid=firebaseAuth.getUid();
        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put ("uid",""+uid);
        hashMap.put ("id",""+title);
        hashMap.put ("description",""+description);
        hashMap.put ("url",""+uploadPdfUrl);
        hashMap.put ("timstamp",timestamp);

        //db reference :DB >Books
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onSucces: Succefully uploaded...");
                        Toast.makeText(PdfAddActivity.this,"succefuly uploaded ",Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.d(TAG,"onFailure:Failed to upload to db due to "+e.getMessage());
                    Toast.makeText(PdfAddActivity.this,"Failed to upload to db due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                });



    }

    private void pdfPickIntent() {
        Log.d(TAG,"pdfPickIntente:starting pdf pick intent");
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(Intent.createChooser(intent,"Select Pdf"),PDF_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==PDF_PICK_CODE){
                Log.d(TAG,"onActivityResult:PDF Picked");
                pdfUri =data.getData();
                Log.d(TAG,"onActivityResult:Uri:"+pdfUri);

            }
        }
        else {
            Log.d(TAG,"onActivityResult:Cncelled picking Pdf  ");
            Toast.makeText(this,"Cancelled picking pdf",Toast.LENGTH_SHORT).show();


        }
    }
}