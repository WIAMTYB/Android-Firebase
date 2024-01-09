package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.RowCategoryBinding;
import com.example.myapplication.databinding.RowPdfAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterCategory  extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {


    private Context context;
    public ArrayList<ModelCategory> categoryArrayList, filterList;
    private RowCategoryBinding binding;

    private FilterPdfAdmin filter;
    private static final String TAG="PDF_ADAPTER_TAG";


   public  AdapterCategory(Context context,ArrayList<ModelCategory> categoryArrayList){
       this.context=context;
       this.categoryArrayList=categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        //get data
        ModelCategory model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        //set data
        holder.categoryTv.setText(category);

        //handle click, delete category
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context,""+category,Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("are u want to delete this category")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context,"Deleting",Toast.LENGTH_SHORT).show();

                                deleteCategory(model,holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });
    }

    private void deleteCategory(ModelCategory model, HolderCategory holder) {
        String id=model.getId();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Succefully deleted ..",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });



   }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class HolderCategory extends RecyclerView.ViewHolder{
         TextView categoryTv;
         ImageButton deleteBtn;
         public HolderCategory(@NonNull View itemView) {
             super(itemView);

    categoryTv=binding.categoryTv;
    deleteBtn=binding.deleteBtn;

         }
     }



}



