package com.example.luismunoz.movieapp.api;

import com.example.luismunoz.movieapp.model.MoviesResponse;

import okhttp3.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by LuisMunoz on 22-05-17.
 */

public interface Service {

    @GET("movie/popular")
    retrofit2.Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    retrofit2.Call<MoviesResponse> getTopRatedMovie(@Query("api_key") String apiKey);

}
