package com.example.dell.eatitserver.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dell on 2/23/2018.
 */

public interface IGeoCoordinates {

    @GET("maps/api/geocode/json")
    Call<String> getgeocode(@Query("address") String address);

    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin,@Query("destination") String destination);
}
