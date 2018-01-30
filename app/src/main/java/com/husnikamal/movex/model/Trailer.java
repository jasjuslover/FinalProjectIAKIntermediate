package com.husnikamal.movex.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by husni on 30/01/18.
 */

public class Trailer {

    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String key;
    @SerializedName("type")
    private String type;

    public Trailer(String name, String key, String type) {
        this.name = name;
        this.key = key;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
