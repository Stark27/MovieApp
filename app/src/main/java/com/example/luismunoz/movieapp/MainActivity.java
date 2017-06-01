package com.example.luismunoz.movieapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.luismunoz.movieapp.adapter.MoviesAdapter;
import com.example.luismunoz.movieapp.api.Client;
import com.example.luismunoz.movieapp.api.Service;
import com.example.luismunoz.movieapp.data.FavoriteDbHelper;
import com.example.luismunoz.movieapp.model.Movie;
import com.example.luismunoz.movieapp.model.MoviesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    FavoriteDbHelper favoriteDbHelper;
    public static final String LOG_TAG = MoviesAdapter.class.getName();

    private String language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    public Activity getActivity() {
        Context context =  this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void initViews() {
        //startProgressDialog();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();
        adapter =  new MoviesAdapter(this, movieList);

        if (getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        favoriteDbHelper = new FavoriteDbHelper(this);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeColors(getResources().getColor(android.R.color.holo_orange_dark));
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_LONG).show();
            }
        });


        checkSortOrder();
    }

    private void initViews2 (){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        favoriteDbHelper = new FavoriteDbHelper(this);

        getAllFavorite();
    }

    private void loadJSON() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API key  firstly  from themoviedb.org",
                        Toast.LENGTH_LONG).show();

                //stopProgressDialog();
                return;
            }

            language = getLanguage();

            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN, language);

            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);

                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                    //stopProgressDialog();
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Error Fetching Data!", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            Log.e("Error", e.getMessage());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void loadJSON1() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API key  firstly  from themoviedb.org",
                        Toast.LENGTH_LONG).show();

                //stopProgressDialog();
                return;
            }

            language = getLanguage();

            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN, language);

            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);

                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                    //stopProgressDialog();
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Error Fetching Data!", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            Log.e("Error", e.getMessage());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void startProgressDialog(){
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Movies...");
        pd.setCancelable(false);
        pd.show();
    }

    private void stopProgressDialog(){
        pd.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent =  new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(LOG_TAG, "Preferences Updated");
        checkSortOrder();
    }

    private void checkSortOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular));

        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            Log.d(LOG_TAG, "Sorting by most popular");
            loadJSON();
        }else if (sortOrder.equals(this.getString(R.string.favorite))) {
            Log.d(LOG_TAG, "Sorting by Favorite");
            initViews2();
        }else {
            Log.d(LOG_TAG, "Sorting by vote average");
            loadJSON1();
        }
    }

    private String getLanguage() {
        String languageType = Locale.getDefault().getLanguage();
        Log.d("LANGUAGE: ", languageType);
        return languageType;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (movieList.isEmpty()) {
            checkSortOrder();
        }
    }

    private void getAllFavorite() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                movieList.clear();
                movieList.addAll(favoriteDbHelper.getAllFavorite());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

}
