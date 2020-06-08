package com.unex.tfg.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.unex.tfg.model.AllowedPlaces;
import com.unex.tfg.model.AllowedPlacesType;
import com.unex.tfg.model.DeviceID;
import com.unex.tfg.model.News;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {News.class, AllowedPlacesType.class, AllowedPlaces.class, DeviceID.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Singleton class instance
    private static AppDatabase INSTANCE;

    // Number of threads for the database
    private static final int NUMBER_OF_THREADS = 4;

    // Database writer executor
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Database name
    private static final String DB_NAME = "application.db";

    // Callback for opening the db
    private static AppDatabase.Callback sAppDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    /**
     * Return the database connection
     * @param context Application context
     * @return The instance of the connection to the database
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DB_NAME)
                            .addCallback(sAppDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // NewsDAO
    abstract NewsDAO newsDAO();

    /**
     * Returns the NewsDAO
     * @return NewsDAO
     */
    public NewsDAO getNewsDAO (){
        return this.newsDAO();
    }

    // AllowedPlacesType
    abstract AllowedPlacesTypeDAO allowedPlacesTypeDAO();

    /**
     * Returns the AllowedPlacesTypeDAO
     * @return AllowedPlacesTypeDAO
     */
    public AllowedPlacesTypeDAO getAllowedPlacesTypeDAO (){
        return this.allowedPlacesTypeDAO();
    }

    // DeviceIDDAO
    abstract DeviceIDDAO deviceIDDAO();

    /**
     * Returns the DeviceIDDAO
     * @return DeviceIDDAO
     */
    public DeviceIDDAO getDeviceIDDAO (){
        return this.deviceIDDAO();
    }

    // AllowedPlacesDAO
    abstract AllowedPlacesDAO allowedPlacesDAO();

    /**
     * Returns the AllowedPlacesDAO
     * @return AllowedPlacesDAO
     */
    public AllowedPlacesDAO getAllowedPlacesDAO (){
        return this.allowedPlacesDAO();
    }



}
