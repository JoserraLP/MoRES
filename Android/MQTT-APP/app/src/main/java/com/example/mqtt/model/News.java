package com.example.mqtt.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class News {

    @PrimaryKey
    private Integer id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "relevance")
    private String relevance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRelevance() {
        return relevance;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }

    @NonNull
    @Override
    public String toString() {
        return "News item {" +
                " id = " + id +
                " , title = '" + title + '\'' +
                " , date = " + date +
                " , description = '" + description + '\'' +
                " , image = '" + image + '\'' +
                " , location = '" + location + '\'' +
                " , relevance = '" + relevance + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object p1){
        if (!p1.getClass().isInstance(News.class)){
            return false;
        }
        else{
            if (p1 instanceof News){
                return ((News) p1).getId().equals(this.id) &&
                        ((News) p1).getTitle().equals(this.title) &&
                        ((News) p1).getDescription().equals(this.description) &&
                        ((News) p1).getDate().equals(this.date) &&
                        ((News) p1).getImage().equals(this.image) &&
                        ((News) p1).getLocation().equals(this.location) &&
                        ((News) p1).getRelevance().equals(this.relevance);
            }
        }
        return false;
    }
}
