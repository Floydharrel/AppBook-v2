package com.app.appbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CourseCategory extends AppCompatActivity {

    RecyclerView recyclerView;
    private ArrayList<CategoryModel> clist;

    private CourseCategoryAdapter adapter;

    private FirebaseDatabase root;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_category);

        getSupportActionBar().setTitle("Course Category");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));

        root = FirebaseDatabase.getInstance();
        reference = root.getReference("CourseCategory");

        recyclerView = findViewById(R.id.course_category_recyclerview);
        recyclerView.setHasFixedSize(true);

        clist = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (snapshot.hasChildren()){
                        CategoryModel categoryModel = new CategoryModel();

                        categoryModel.setImageUrl(Objects.requireNonNull(dataSnapshot.child("imageUrl").getValue()).toString());
                        categoryModel.setCategoryName(Objects.requireNonNull(dataSnapshot.child("categoryName").getValue()).toString());
                        categoryModel.setKeyCategory(Objects.requireNonNull(dataSnapshot.child("keyCategory").getValue()).toString());
                        clist.add(categoryModel);

                    }else{
                        Toast.makeText(CourseCategory.this, "No Available Course", Toast.LENGTH_SHORT).show();
                    }

                }
                adapter = new CourseCategoryAdapter(CourseCategory.this,clist);
                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseCategory.this, "DB ERROR", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void Back(View view) {
    }

    @Override
    public void onBackPressed() {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Exit");
        //set message
        builder.setMessage("Are you sure you want to Exit?");
        //positive yes
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish activity
                Intent exit = new Intent(Intent.ACTION_MAIN);
                exit.addCategory(Intent.CATEGORY_HOME);
                exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exit);
                finishAffinity();
                System.exit(0);
            }
        });
        //negative no
        builder.setNegativeButton("NO", (dialog, which) -> {
            //dismiss dialog
            dialog.dismiss();
        });
        builder.show();
    }

    public void AddCourse(View view) {
        Intent intent = new Intent(CourseCategory.this,AddCourseCategory.class);
        startActivity(intent);
    }



    public void search(View view) {
        openOptionsMenu();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Category");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}