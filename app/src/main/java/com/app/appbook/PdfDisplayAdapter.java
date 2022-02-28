package com.app.appbook;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class PdfDisplayAdapter extends RecyclerView.Adapter<PdfDisplayAdapter.MyViewHolder>{

    Context context;
    ArrayList<PdfModel> pList;

    FirebaseDatabase root;
    DatabaseReference ref;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;


    public PdfDisplayAdapter(Context context, ArrayList<PdfModel> pList) {
        this.context = context;
        this.pList = pList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_pdf, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PdfModel pdfModel = pList.get(position);
        holder.pdfTitle.setText(pdfModel.getPdfName());

        root = FirebaseDatabase.getInstance();

        holder.read.setOnClickListener(view -> {
            Intent intent = new Intent(context,ViewPdf.class);
            intent.putExtra("filename",pdfModel.getPdfName());
            intent.putExtra("fileurl",pdfModel.getPdfUrl());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


        });

        holder.download.setOnClickListener(view -> {
            ref = root.getReference("Pdf/"+PassInfo.CourseNAme.toUpperCase());
            ref.child(String.valueOf(pdfModel.getPdfFile())).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot mySnapshot = task.getResult();
                        String filename = String.valueOf(mySnapshot.child("pdfName").getValue());
                        String fileUrl = String.valueOf(mySnapshot.child("pdfUrl").getValue());
                        getFile(fileUrl,filename);
                    }
                }
            });




        });




    }

    private void getFile(String fileUrl,String name) {
            String url = fileUrl.toString();
          downloadFile(context.getApplicationContext(),name,".pdf",DIRECTORY_DOWNLOADS,url);
    }

    private void downloadFile(Context context, String fileName, String fileExtension,String directory,String url) {
        DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,directory,fileName+fileExtension);

        downloadManager.enqueue(request);
        Toast.makeText(context, "Downloading "+fileName+fileExtension, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return pList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView read,download,pdfTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            read = itemView.findViewById(R.id.read_pdf);
            download = itemView.findViewById(R.id.dl_pdf);
            pdfTitle = itemView.findViewById(R.id.name_of_pdf);



        }
    }
}
