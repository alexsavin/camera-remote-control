package com.fuckolympus.arc.camera.vo;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by alex on 17.7.17.
 */
public class Desclist {

    public static final String TAKEMODE_PROP = "takemode";

    @SerializedName("desc")
    public List<Desc> descs;

    public String getTakeMode() {
        for (Desc desc : descs) {
            if (TAKEMODE_PROP.equalsIgnoreCase(desc.propname)) {
                return desc.value;
            }
        }
        return StringUtils.EMPTY;
    }
}
