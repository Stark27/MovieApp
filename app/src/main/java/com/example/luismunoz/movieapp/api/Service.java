package com.example.luismunoz.movieapp.api;

import com.example.luismunoz.movieapp.model.MoviesResponse;
import com.example.luismunoz.movieapp.model.Trailer;
import com.example.luismunoz.movieapp.model.TrailerResponse;

import okhttp3.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by LuisMunoz on 22-05-17.
 */

public interface Service {

    @GET("movie/popular")
    retrofit2.Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey,
                                                    @Query("language") String language);

    @GET("movie/top_rated")
    retrofit2.Call<MoviesResponse> getTopRatedMovie(@Query("api_key") String apiKey,
                                                    @Query("language") String language);

    @GET("movie/{movie_id}/videos")
    retrofit2.Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);

}
