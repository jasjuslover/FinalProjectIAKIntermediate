package com.husnikamal.movex.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by husni on 29/01/18.
 */

public class CastResponse {
    @SerializedName("cast")
    private List<Cast> casts;

    public CastResponse(List<Cast> casts) {
        this.casts = casts;
    }

    public List<Cast> getCasts() {
        return casts;
    }

    public void setCasts(List<Cast> casts) {
        this.casts = casts;
    }
}
