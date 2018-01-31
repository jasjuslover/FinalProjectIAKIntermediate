package com.husnikamal.movex.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by husni on 30/01/18.
 */

public class TrailerResponse {
    @SerializedName("results")
    private ArrayList<Trailer> results;

    public TrailerResponse(ArrayList<Trailer> results) {
        this.results = results;
    }

    public ArrayList<Trailer> getResults() {
        return results;
    }

    public void setResults(ArrayList<Trailer> results) {
        this.results = results;
    }
}
