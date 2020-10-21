package taxi.ratingen.retro;



import com.google.gson.JsonObject;

import taxi.ratingen.utilz.Constants;



import retrofit2.Call;

import retrofit2.http.GET;

import retrofit2.http.Query;


/**

 * Created by root on 10/12/17.

 */



public interface GitHubMapService {



    @GET("maps/api/geocode/json?")

    Call<JsonObject> GetAddressFromLatLng(@Query("latlng") String name, @Query("sensor") boolean a,@Query("key") String key);



 /*   @Query("components") String components,@Query("radius") String radius*/

    @GET("maps/api/place/autocomplete/json?radius=500")
    Call<JsonObject> GetPlaceApi(@Query("language") String language,@Query("location") String locat,@Query("input") String input, @Query("sensor") boolean a,@Query("key") String key);



    @GET("maps/api/place/autocomplete/json?radius=500")
    Call<JsonObject> GetPlaceApi(@Query("location") String locat,@Query("input") String input, @Query("sensor") boolean a,@Query("key") String key);

@GET("maps/api/place/autocomplete/json?")
    Call<JsonObject> GetPlaceApi(@Query("input") String input, @Query("sensor") boolean a,@Query("key") String key);



    @GET(Constants.URL.DirectionURL)

    Call<JsonObject> GetDrawpath(@Query("origin") String origin, @Query("destination") String destionation, @Query("sensor") boolean a, @Query("key") String key);





    @GET("maps/api/geocode/json?")

    Call<JsonObject> GetLatLngFromAddress(@Query("address") String name, @Query("sensor") boolean a,@Query("key") String key);





}