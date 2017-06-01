package com.example.luismunoz.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luismunoz.movieapp.adapter.TrailerAdapter;
import com.example.luismunoz.movieapp.api.Client;
import com.example.luismunoz.movieapp.api.Service;
import com.example.luismunoz.movieapp.data.FavoriteDbHelper;
import com.example.luismunoz.movieapp.model.Movie;
import com.example.luismunoz.movieapp.model.Trailer;
import com.example.luismunoz.movieapp.model.TrailerResponse;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LuisMunoz on 21-05-17.
 */

public class DetailActivity extends AppCompatActivity {
    TextView nameOfMovie, plotSynopsis, userRating, releaseDate;
    ImageView imageView;
    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    private FavoriteDbHelper favoriteDbHelper;
    private Movie favorite;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        imageView =  (ImageView) findViewById(R.id.thumbnail_image_haeder);
        nameOfMovie = (TextView) findViewById(R.id.title);
        plotSynopsis = (TextView) findViewById(R.id.plot_synopsis);
        userRating = (TextView) findViewById(R.id.user_rating);
        releaseDate = (TextView) findViewById(R.id.release_date);

        Intent intentThatStartThisActivity = getIntent();

        if (intentThatStartThisActivity.hasExtra("original_title")) {
            String thumbnail =  getIntent().getExtras().getString("poster_path");
            String movieName = getIntent().getExtras().getString("original_title");
            String synopsis = getIntent().getExtras().getString("overview");
            String rating = getIntent().getExtras().getString("vote_average");
            String dateOfRelease = getIntent().getExtras().getString("release_date");

            String poster = "https://image.tmdb.org/t/p/w500" + thumbnail;

            Glide.with(this)
                    .load(poster)
                    .centerCrop()
                    .placeholder(R.color.placeholderColor)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);

        }else {
            Toast.makeText(this, "No API data", Toast.LENGTH_LONG).show();
        }

        MaterialFavoriteButton materialFavoriteButton = (MaterialFavoriteButton) findViewById(R.id.favorite_button);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favorite) {
                    SharedPreferences.Editor editor = getSharedPreferences
                            ("com.example.luismunoz.movieapp.DetailActivity", MODE_PRIVATE).edit();

                    editor.putBoolean("Favorite added", true);
                    editor.commit();
                    saveFavorite();
                    Snackbar.make(buttonView, "Added to favorite", Snackbar.LENGTH_LONG).show();

                }else {
                    int movie_id = getIntent().getExtras().getInt("id");
                    favoriteDbHelper = new FavoriteDbHelper(getApplicationContext());
                    favoriteDbHelper.deleteFavorite(movie_id)
                    ;
                    SharedPreferences.Editor editor = getSharedPreferences
                            ("com.example.luis.munoz.movieapp.DetailActivity", MODE_PRIVATE).edit();
                    editor.putBoolean("Favorite removed",  true);
                    editor.commit();
                    Snackbar.make(buttonView, "Removed from favorite", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        initViews();

    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.collapsion_toolbar);

        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isSlow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isSlow = true;
                } else if (isSlow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isSlow =false;

                }

            }
        });
    }

    private void initViews() {
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_trailer);
        RecyclerView.LayoutManager mLayoutManager =  new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();
    }

    private void loadJSON() {
        int movie_id = getIntent().getExtras().getInt("id");

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API key firstly from themoviedb.org",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Error Fetching Trailer", Toast.LENGTH_LONG).show();
                }
            });

        }catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void saveFavorite(){
        favoriteDbHelper = new FavoriteDbHelper(this);
        favorite = new Movie();
        int movie_id = getIntent().getExtras().getInt("id");

        String rate = getIntent().getExtras().getString("vote_average");
        String poster = getIntent().getExtras().getString("poster_path");


        favorite.setId(movie_id);
        favorite.setOriginalTitle(nameOfMovie.getText().toString().trim());
        favorite.setPosterPath(poster);
        favorite.setVoteAverage(Double.parseDouble(rate));
        favorite.setOverview(plotSynopsis.getText().toString().trim());

        favoriteDbHelper.addFavorite(favorite);

    }


}
