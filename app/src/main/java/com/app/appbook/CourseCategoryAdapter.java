package com.app.appbook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CourseCategoryAdapter extends RecyclerView.Adapter<CourseCategoryAdapter.MyViewHolder> implements Filterable {


    Context context;
    private ArrayList<CategoryModel> cList;
    private ArrayList<CategoryModel> cListFull;

    public CourseCategoryAdapter(Context context,ArrayList<CategoryModel> cList) {
        this.context = context;
        this.cListFull = cList;
        this.cList = new ArrayList<>(cListFull);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_course_category, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoryModel categoryModel = cList.get(position);
        holder.cName.setText(categoryModel.getCategoryName());
        Glide.with(context).load(categoryModel.getImageUrl()).into(holder.cImg);
        String key = categoryModel.getKeyCategory();

        holder.categoryClicked.setOnClickListener(view -> {
            PassInfo.CourseNAme = categoryModel.getCategoryName();
            PassInfo.CourseKey = key;

            Intent intent = new Intent(view.getContext(),  PdfDisplay.class);
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<CategoryModel> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(cListFull);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (CategoryModel categoryModel : cListFull){
                    if (categoryModel.getCategoryName().toLowerCase().contains(filterPattern)){
                        filteredList.add(categoryModel);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            cList.clear();
            cList.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();

        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView cImg;
        TextView cName;
        LinearLayout categoryClicked;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cImg = itemView.findViewById(R.id.item_img);
            cName = itemView.findViewById(R.id.item_course_name);
            categoryClicked = itemView.findViewById(R.id.clicked_category);
        }
    }
}
