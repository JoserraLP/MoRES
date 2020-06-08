package com.unex.tfg.data.remote;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

public class AllowedPlacesResponseItem {

    // Allowed place position (lat, lng)
    private ArrayList<Double> position;

    // Allowed place ID
    private String id;

    // Allowed place title
    private String title;

    // Allowed place category
    private Category category;

    // Allowed place icon
    private String icon;

    /**
     * AllowedPlacesResponseItem
     * @param position Allowed place position (lat, lng)
     * @param id Allowed place id
     * @param title Allowed place title
     * @param category Allowed place category
     * @param icon Allowed place icon
     */
    public AllowedPlacesResponseItem(ArrayList<Double> position, String id, String title, Category category, String icon) {
        this.position = position;
        this.id = id;
        this.title = title;
        this.category = category;
        this.icon = icon;
    }

    /**
     * Getter of Allowed place position (lat, lng)
     * @return Allowed place position (lat, lng)
     */
    public ArrayList<Double> getPosition() {
        return position;
    }

    /**
     * Setter of Allowed place position (lat, lng)
     * @param position Allowed place position (lat, lng)
     */
    @SuppressWarnings("unused")
    public void setPosition(ArrayList<Double> position) {
        this.position = position;
    }

    /**
     * Getter of Allowed place ID
     * @return Allowed place ID
     */
    public String getId() {
        return id;
    }

    /**
     * Setter of Allowed place ID
     * @param id Allowed place ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter of Allowed place title
     * @return Allowed place title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter of Allowed place title
     * @param title Allowed place title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter of Allowed place category
     * @return Allowed place category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Setter of Allowed place category
     * @param category Allowed place category
     */
    @SuppressWarnings("unused")
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Getter of Allowed place icon
     * @return Allowed place icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Setter of Allowed place icon
     * @param icon Allowed place icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * equals method
     * @param o AllowedPlacesResponseItem
     * @return true if both objects are equals, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllowedPlacesResponseItem that = (AllowedPlacesResponseItem) o;
        return Objects.equals(position, that.position) &&
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(category, that.category) &&
                Objects.equals(icon, that.icon);
    }

    /**
     * toString method
     * @return String representing the object
     */
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

    public static class Category {

        // Category id
        private String id;

        // Category title
        private String title;

        /**
         * Category constructor
         * @param id Category ID
         * @param title Category title
         */
        public Category(String id, String title) {
            this.id = id;
            this.title = title;
        }

        /**
         * Getter of Category ID
         * @return Category ID
         */
        public String getId() {
            return id;
        }

        /**
         * Setter of Category ID
         * @param id Category ID
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Getter of Category title
         * @return Category title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Setter of Category title
         * @param title Category title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * equals method
         * @param o Category
         * @return true if both objects are equals, false if not
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Category category = (Category) o;
            return Objects.equals(id, category.id) &&
                    Objects.equals(title, category.title);
        }

        /**
         * toString method
         * @return String representing the object
         */
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
