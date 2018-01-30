package com.husnikamal.movex.network;

import com.husnikamal.movex.model.CastResponse;
import com.husnikamal.movex.model.MovieResponse;
import com.husnikamal.movex.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by husni on 28/01/18.
 */

public interface Service {

    @GET("movie/popular")
    Call<MovieResponse> getPopular(@Query("api_key") String API_KEY);

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlaying(@Query("api_key") String API_KEY);

    @GET("search/movie")
    Call<MovieResponse> searchMovie(@Query("api_key") String API_KEY, @Query("query") String MOVIE_NAME);

    @GET("movie/{id}/credits")
    Call<CastResponse> getCast(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getTrailers(@Path("movie_id") int mId, @Query("api_key") String apiKey);
}
