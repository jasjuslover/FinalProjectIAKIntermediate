package com.husnikamal.movex.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by husni on 28/01/18.
 */

public class MovieResponse {
    @SerializedName("results")
    private List<Movie> results;

    public MovieResponse(List<Movie> results) {
        this.results = results;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
