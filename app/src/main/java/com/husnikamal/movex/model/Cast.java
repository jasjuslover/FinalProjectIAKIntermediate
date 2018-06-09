package com.husnikamal.movex.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by husni on 29/01/18.
 */

public class Cast {
    @SerializedName("character")
    private String character;
    @SerializedName("name")
    private String name;
    @SerializedName("profile_path")
    private String closeUp;

    String baseImageUrl = "https://image.tmdb.org/t/p/w500";

    public Cast(String name, String closeUp) {
        this.name = name;
        this.closeUp = closeUp;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCloseUp() {
        return closeUp;
    }

    public void setCloseUp(String closeUp) {
        this.closeUp = closeUp;
    }
}
