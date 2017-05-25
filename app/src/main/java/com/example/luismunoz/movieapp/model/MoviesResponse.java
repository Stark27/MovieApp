package com.example.luismunoz.movieapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by LuisMunoz on 22-05-17.
 */

public class MoviesResponse {

    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<Movie> results;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_page")
    private int totalPage;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public List<Movie> getMovies() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public void setMovies(List<Movie> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
