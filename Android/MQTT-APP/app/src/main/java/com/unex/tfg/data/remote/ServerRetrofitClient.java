

package com.unex.tfg.data.remote;

import com.unex.tfg.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerRetrofitClient {

    // URL for the Retrofit Connection
    private static final String BASE_URL = Constants.API_SERVER_URL;

    // Singleton class instance
    private static ServerRetrofitClient mInstance;

    // Retrofit variable
    private Retrofit retrofit;

    /**
     * Create the retrofit connection to the URL
     */
    private ServerRetrofitClient(){

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Get the Server Retrofit Client Singleton instance
     * @return ServerRetrofitClient singleton instance
     */
    public static synchronized ServerRetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new ServerRetrofitClient();
        }
        return mInstance;
    }

    /**
     * Create the retrofit allowed places type service
     * @return Allowed Places Types Service
     */
    public AllowedPlacesTypeService getAllowedPlacesTypeServiceAPI(){
        return retrofit.create(AllowedPlacesTypeService.class);
    }

    /**
     * Create the retrofit device ID service
     * @return Device ID Service
     */
    public DeviceIDService getDeviceIDAPI(){
        return retrofit.create(DeviceIDService.class);
    }

    /**
     * Create the retrofit nearby devices service
     * @return Nearby Devices Service
     */
    public NearbyDevicesService getNearbyDevicesServiceAPI(){
        return retrofit.create(NearbyDevicesService.class);
    }
}