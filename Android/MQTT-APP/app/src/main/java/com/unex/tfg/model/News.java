package com.unex.tfg.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class News {

    @PrimaryKey
    private Integer id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "expansion")
    private String expansion;

    @ColumnInfo(name = "relevance")
    private int relevance;

    /**
     * News empty constructor
     */
    public News() {
        this.id = -1;
        this.title = "";
        this.author = "";
        this.date = "";
        this.description = "";
        this.image = "";
        this.location = "";
        this.expansion = "";
        this.relevance = -1;
    }

    /**
     * News parametrized constructor
     * @param id News ID
     * @param title News title
     * @param author News author
     * @param date News date
     * @param description News description
     * @param image News image
     * @param location News location
     * @param expansion News expansion
     * @param relevance News relevance
     */
    @Ignore
    public News(Integer id, String title, String author, String date, String description, String image, String location, String expansion, int relevance) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = date;
        this.description = description;
        this.image = image;
        this.location = location;
        this.expansion = expansion;
        this.relevance = relevance;
    }

    /**
     * Getter of News ID
     * @return News ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter of News ID
     * @param id News ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter of News title
     * @return News title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter of News title
     * @param title News title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter of News author
     * @return News author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Setter of News author
     * @param author News author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Getter of News date
     * @return News date
     */
    public String getDate() {
        return date;
    }

    /**
     * Setter of News date
     * @param date News date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Getter of News description
     * @return News description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter of News description
     * @param description News description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter of News image
     * @return News image
     */
    public String getImage() {
        return image;
    }

    /**
     * Setter of News image
     * @param image News image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Getter of News location
     * @return News location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter of News location
     * @param location News location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter of News expansion
     * @return News expansion
     */
    public String getExpansion() {
        return expansion;
    }

    /**
     * Setter of News expansion
     * @param expansion News expansion
     */
    public void setExpansion(String expansion) {
        this.expansion = expansion;
    }

    /**
     * Getter of News relevance
     * @return News relevance
     */
    public int getRelevance() {
        return relevance;
    }

    /**
     * Setter of News relevance
     * @param relevance News relevance
     */
    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

    /**
     * equals method
     * @param p1 News
     * @return true if both objects are equals, false if not
     */
    @Override
    public boolean equals(Object p1){
        if (!p1.getClass().isInstance(News.class)) {
            return false;
        } else {
            if (p1 instanceof News){
                return ((News) p1).getId().equals(this.id) &&
                        ((News) p1).getAuthor().equals(this.author) &&
                        ((News) p1).getTitle().equals(this.title) &&
                        ((News) p1).getDescription().equals(this.description) &&
                        ((News) p1).getDate().equals(this.date) &&
                        ((News) p1).getImage().equals(this.image) &&
                        ((News) p1).getLocation().equals(this.location) &&
                        ((News) p1).getExpansion().equals(this.expansion) &&
                        ((News) p1).getRelevance() == this.relevance;
            }
        }
        return false;
    }

    /**
     * toString method
     * @return String representing the object
     */
    @NonNull
    @Override
    public String toString() {
        return "News item {" +
                " id = " + id +
                " , author = '" + author + '\'' +
                " , title = '" + title + '\'' +
                " , date = " + date +
                " , description = '" + description + '\'' +
                " , image = '" + image + '\'' +
                " , location = '" + location + '\'' +
                " , expansion = '" + expansion + '\'' +
                " , relevance = '" + relevance + '\'' +
                '}';
    }
}
