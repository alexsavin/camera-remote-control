package com.fuckolympus.arc.camera.vo;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * Created by alex on 17.7.17.
 */
public class Desclist {

    public static final String TAKEMODE_PROP = "takemode";
    public static final String FOCALVALUE_PROP = "focalvalue";
    public static final String SHUTSPEEDVALUE_PROP = "shutspeedvalue";

    @SerializedName("desc")
    public List<Desc> desclist;

    public String getTakeMode() {
        return getPropValue(TAKEMODE_PROP);
    }

    public double getFocalValue() {
        return NumberUtils.toDouble(getPropValue(FOCALVALUE_PROP));
    }

    public String getShutterSpeedValue() {
        return getPropValue(SHUTSPEEDVALUE_PROP);
    }

    private String getPropValue(String propname) {
        for (Desc desc : desclist) {
            if (propname.equalsIgnoreCase(desc.propname)) {
                return desc.value;
            }
        }
        return StringUtils.EMPTY;
    }
}
