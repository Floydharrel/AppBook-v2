package com.app.appbook;

public class CategoryModel {
    private String categoryName,imageUrl,keyCategory;



    public CategoryModel(){

    }

    public CategoryModel(String categoryName, String imageUrl, String keyCategory) {
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.keyCategory = keyCategory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKeyCategory() {
        return keyCategory;
    }

    public void setKeyCategory(String keyCategory) {
        this.keyCategory = keyCategory;
    }
}
