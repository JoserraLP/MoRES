

package com.example.mqtt.data.remote;

import com.example.mqtt.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesRetrofitClient {

    private static final String BASE_URL = Constants.PLACES_API_SERVER_URL;

    private static PlacesRetrofitClient mInstance;
    private Retrofit retrofit;

    private PlacesRetrofitClient(){

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized PlacesRetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new PlacesRetrofitClient();
        }
        return mInstance;
    }

    public AllowedPlacesService getAllowedPlacesServiceAPI(){
        return retrofit.create(AllowedPlacesService.class);
    }

}