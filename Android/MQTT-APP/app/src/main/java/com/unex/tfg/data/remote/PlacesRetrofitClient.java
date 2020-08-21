

package com.unex.tfg.data.remote;

import com.unex.tfg.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesRetrofitClient {

    // URL for the Retrofit Connection
    private static final String BASE_URL = Constants.PLACES_API_SERVER_URL;

    // Singleton class instance
    private static PlacesRetrofitClient mInstance;

    // okHttpClient
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    // Retrofit variable
    private Retrofit retrofit;

    /**
     * Create the retrofit connection to the URL
     */
    private PlacesRetrofitClient(){

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Get the Places Retrofit Client Singleton instance
     * @return PlacesRetrofitClient singleton instance
     */
    public static synchronized PlacesRetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new PlacesRetrofitClient();
        }
        return mInstance;
    }

    /**
     * Create the retrofit allowed places service
     * @return Allowed Places Service
     */
    public AllowedPlacesService getAllowedPlacesServiceAPI(){
        return retrofit.create(AllowedPlacesService.class);
    }

}