package com.example.mqtt.data.remote;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.example.mqtt.model.AllowedPlaces;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AllowedPlacesResponseItem {

    private ArrayList<Double> position;

    private String id;

    private String title;

    private Category category;

    private String icon;

    public AllowedPlacesResponseItem(ArrayList<Double> position, String id, String title, Category category, String icon) {
        this.position = position;
        this.id = id;
        this.title = title;
        this.category = category;
        this.icon = icon;
    }

    public ArrayList<Double> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Double> position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @NonNull
    @Override
    public String toString() {
        return "AllowedPlacesResponseItem{" +
                "position=" + position +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", icon=" + icon +
                '}';
    }

    public class Category { //TODO to retreive all the possible types https://places.demo.api.here.com/places/v1/categories/places/?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg#

        private String id;

        private String title;

        public Category(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @NonNull
        @Override
        public String toString() {
            return "Category{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

}
