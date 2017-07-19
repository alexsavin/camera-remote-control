package com.fuckolympus.arc.camera.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 17.7.17.
 */
public class Desc {

    public String propname;

    public String attribute;

    public String value;

    @SerializedName("enum")
    public String valueEnum;
}
