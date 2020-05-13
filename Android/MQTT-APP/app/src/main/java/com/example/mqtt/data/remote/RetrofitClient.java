

package com.example.mqtt.data.remote;

import com.example.mqtt.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = Constants.API_SERVER_URL; // Localhost fails http://localhost:8080/

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public AllowedPlacesTypeService getAPI(){
        return retrofit.create(AllowedPlacesTypeService.class);
    }

}