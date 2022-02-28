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
import android.widget.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class AddCourseCategory extends AppCompatActivity {
    ImageView courseImg;
    EditText courseName;
    Button saveCourse,cancelCourse;
    public static Uri imageUri;

    private StorageReference storageReference;
    private FirebaseStorage storage;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CourseCategory");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_category);

        getSupportActionBar().hide();
        courseImg = findViewById(R.id.add_course_category_img);
        courseName = findViewById(R.id.name_of_category);
        saveCourse = findViewById(R.id.save_add_course);
        cancelCourse = findViewById(R.id.cancel_add_course);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        courseImg.setOnClickListener(view -> choosePicture());

        saveCourse.setOnClickListener(view -> {
            String category = courseName.getText().toString().trim();
            if(imageUri!=null && !TextUtils.isEmpty(category)){
                uploadPicture(imageUri,category);


            }else {
                Toast.makeText(AddCourseCategory.this,"Please Select Image and enter name of course",Toast.LENGTH_SHORT).show();
            }

        });

        cancelCourse.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(),CourseCategory.class);
            startActivity(intent);
            finishAffinity();
        });



    }



    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            courseImg.setImageURI(imageUri);
        }
    }

    private void uploadPicture(Uri uri, String category) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

// Create a reference to 'images/mountains.jpg'
        final String randomKey = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("courseImages/"+randomKey+"."+getfileExtension(uri));

        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        CategoryModel categoryModel = new CategoryModel(category,uri.toString(),randomKey);
                        reference.child(randomKey).setValue(categoryModel);

                        Intent intent = new Intent(AddCourseCategory.this,CourseCategory.class);
                        startActivity(intent);
                        finishAffinity();

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Error While Saving", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressDialog.setMessage("Progress: "+progressPercent +"%");
                    Snackbar.make(findViewById(android.R.id.content),"Saved", Snackbar.LENGTH_SHORT).show();


                });
    }

    private String getfileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(mUri));
    }

    public void Back(View view) {
        super.onBackPressed();
    }
}