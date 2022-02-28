package com.app.appbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PdfDisplay extends AppCompatActivity {
    TextView courseTitle;

    RecyclerView recyclerView;
    private ArrayList<PdfModel> plist;

    private PdfDisplayAdapter adapter;

    private FirebaseDatabase root;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_display);

        getSupportActionBar().hide();

        courseTitle = findViewById(R.id.category_name_tv);
        courseTitle.setText(PassInfo.CourseNAme);

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("Pdf/"+PassInfo.CourseNAme.toUpperCase());

        recyclerView = findViewById(R.id.pdf_recyclerview);
        recyclerView.setHasFixedSize(true);

        plist = new ArrayList<>();
        adapter = new PdfDisplayAdapter(this,plist);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (snapshot.hasChildren()){
                        PdfModel pdfModel = new PdfModel();

                        pdfModel.setPdfUrl(Objects.requireNonNull(dataSnapshot.child("pdfUrl").getValue()).toString());
                        pdfModel.setPdfName(Objects.requireNonNull(dataSnapshot.child("pdfName").getValue()).toString());
                        pdfModel.setPdfFile(Objects.requireNonNull(dataSnapshot.child("pdfFile").getValue()).toString());
                        plist.add(pdfModel);

                    }else{
                        Toast.makeText(PdfDisplay.this, "No Available Books", Toast.LENGTH_SHORT).show();
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PdfDisplay.this, "DB ERROR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void AddPdf(View view) {
        Intent intent = new Intent(PdfDisplay.this,AddPdfFile.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}