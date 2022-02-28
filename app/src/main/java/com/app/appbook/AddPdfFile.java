package com.app.appbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddPdfFile extends AppCompatActivity {

    ImageView pdfIndicatorImg;
    EditText pdfName;
    Button savePdf,cancelPdf;
    Button choosePdf;
    public static Uri pdfUri;

    private StorageReference storageReference;
    private FirebaseStorage storage;

    private DatabaseReference reference;
    private FirebaseDatabase root;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdf_file);
        getSupportActionBar().hide();

        pdfIndicatorImg = findViewById(R.id.pdfIndicator);
        pdfName = findViewById(R.id.name_of_file_et);
        savePdf = findViewById(R.id.save_file);
        cancelPdf = findViewById(R.id.cancel_file);
        choosePdf = findViewById(R.id.choose_pdf_btn);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        root = FirebaseDatabase.getInstance();

        choosePdf.setOnClickListener(view -> choosePDF());

        savePdf.setOnClickListener(view ->{
            String nameFile = pdfName.getText().toString().trim();
            if(pdfUri!=null && !TextUtils.isEmpty(nameFile)){
                uploadPdf(pdfUri,nameFile);
            }else {
                Toast.makeText(AddPdfFile.this,"Please Select PDF file",Toast.LENGTH_SHORT).show();
            }
        });

        cancelPdf.setOnClickListener(view -> super.onBackPressed());
    }



    private void choosePDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            pdfUri = data.getData();
            pdfIndicatorImg.setImageResource(R.drawable.ic_pdf);
        }
    }
    private void uploadPdf(Uri pdfUri, String nameFile) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();

        // Create a reference to 'pdfFiles/'
        final String randomKey = UUID.randomUUID().toString();
        StorageReference pdfRef = storageReference.child("pdfFiles/"+randomKey+"."+getfileExtension(pdfUri));

        pdfRef.putFile(pdfUri).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                reference = root.getReference("Pdf/"+PassInfo.CourseNAme.toUpperCase());
                progressDialog.dismiss();
                String file = randomKey+".pdf";
                PdfModel pdfModel = new PdfModel(uri.toString(),nameFile,randomKey);
                reference.child(randomKey).setValue(pdfModel);

                Intent intent = new Intent(AddPdfFile.this,PdfDisplay.class);
                startActivity(intent);
                finish();

            });


        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Snackbar.make(findViewById(android.R.id.content),"Error While Saving", Snackbar.LENGTH_SHORT).show();
        })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressDialog.setMessage("Progress: "+progressPercent +"%");
                    Snackbar.make(findViewById(android.R.id.content),"Saved", Snackbar.LENGTH_SHORT).show();


                });
    }

    private String getfileExtension(Uri pdfUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(pdfUri));

    }

    public void Back(View view) {
        super.onBackPressed();
    }
}