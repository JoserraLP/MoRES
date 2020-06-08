

package com.unex.tfg.data.remote;

import com.unex.tfg.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesRetrofitClient {

    // URL for the Retrofit Connection
    private static final String BASE_URL = Constants.PLACES_API_SERVER_URL;

    // Singleton class instance
    private static PlacesRetrofitClient mInstance;

    // Retrofit variable
    private Retrofit retrofit;

    /**
     * Create the retrofit connection to the URL
     */
    private PlacesRetrofitClient(){

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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