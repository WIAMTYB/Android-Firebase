package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.RowPdfAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>  implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList , filterList;
    private RowPdfAdminBinding binding;

    private FilterPdfAdmin filter;
    private static final String TAG="PDF_ADAPTER_TAG";





    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList=pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
// bind logout using view binding
        binding=RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
    //get data set data
        ModelPdf model=pdfArrayList.get(position);
        String title=model.getTitle();
        String description= model.getDescription();
        long timestamp= model.getTimestamp();

        String formattedDate =MyApp.formatTimesTamp(timestamp);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        loadPdfFromUrl(model,holder);
        loadPdfSize(model,holder);



    }

    private void loadPdfSize(ModelPdf model, HolderPdfAdmin holder) {
        String pdfUrl =model.getUrl();
        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
    ref.getMetadata()
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"onFailed"+e.getMessage());

                }
            })

            .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                 double bytes=storageMetadata.getSizeBytes();
                    Log.d(TAG,"onsucces"+model.getTitle()+""+bytes);

                 double kb=bytes/1024;
                 double mb=kb/1024;


                 if(mb>=1){
                     holder.sizeTv.setText(String.format("%.2f",mb)+"MB");
                 }
                 else if(kb>=1){
                     holder.sizeTv.setText(String.format("%.2f",kb)+"KB");


                 }
                  else {
                     holder.sizeTv.setText(String.format("%.2f",bytes)+"bytes");

                 }
                }
            });
    }

    private void loadPdfFromUrl(ModelPdf model, HolderPdfAdmin holder) {
     String pdfUrl=model.getUrl();
     StorageReference ref=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
     ref.getBytes(50000000)
             .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                 @Override
                 public void onSuccess(byte[] bytes) {
                     Log.d(TAG,"onsucces"+model.getTitle()+"Succefuully got the file ");



                 }
             })
             .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Log.d(TAG,"onFailure :failed"+e.getMessage());

                 }
             });








    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter=new FilterPdfAdmin(filterList,this);

        }
        return filter ;
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder{
        TextView titleTv,descriptionTv,sizeTv,dateTv;

        public HolderPdfAdmin(View itemView ){
            super(itemView );
           titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
            sizeTv=binding.sizeTv;
            dateTv=binding.dateTv;


        }
    }






}
