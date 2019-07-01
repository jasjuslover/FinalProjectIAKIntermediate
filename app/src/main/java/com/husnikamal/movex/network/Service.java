package com.husnikamal.movex.network;

import com.husnikamal.movex.model.CastResponse;
import com.husnikamal.movex.model.MovieResponse;
import com.husnikamal.movex.model.TrailerResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by husni on 28/01/18.
 */

public interface Service {

    @GET("movie/{type}")
    Observable<MovieResponse> getMovie(@Path("type") String type, @Query("api_key") String API_KEY);

    @GET("search/movie")
    Observable<MovieResponse> searchMovie(@Query("api_key") String API_KEY, @Query("query") String MOVIE_NAME);

    @GET("movie/{id}/credits")
    Observable<CastResponse> getCast(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Observable<TrailerResponse> getTrailers(@Path("movie_id") int mId, @Query("api_key") String apiKey);
}
