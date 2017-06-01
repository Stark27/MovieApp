package com.example.luismunoz.movieapp.data;

import android.provider.BaseColumns;

/**
 * Created by LuisMunoz on 30-05-17.
 */

public class FavoriteContract {

    public static final class FavoriteEntry implements BaseColumns{

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIEID = "movieId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_USER_RATING = "userRating";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_PLOT_SYNOPSIS = "overView";

    }
}
