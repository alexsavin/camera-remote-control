package com.fuckolympus.arc.settings;

import android.content.Context;
import android.content.SharedPreferences;
import com.fuckolympus.arc.R;
import com.fuckolympus.arc.camera.api.CameraState;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 28.7.17.
 */
public class Settings {

    public static final String DEF_TOTALITY_TIME = "10:18:00";
    public static final String DEF_TOTALITY_DURATION = "00:02:01";
    public static final String DEF_TIME_LAPSE_INTERVAL = "00:01:00";

    private Context context;

    private Map<Integer, String> settingsMap = new HashMap<>();

    public Settings(Context context, CameraState cameraState) {
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        settingsMap.put(R.string.totality_time,
                sharedPreferences.getString(context.getString(R.string.totality_time), DEF_TOTALITY_TIME));
        settingsMap.put(R.string.totality_duration,
                sharedPreferences.getString(context.getString(R.string.totality_duration), DEF_TOTALITY_DURATION));
        settingsMap.put(R.string.partial_phase_shut_speed,
                sharedPreferences.getString(context.getString(R.string.partial_phase_shut_speed), cameraState.shutterSpeedValue));
        settingsMap.put(R.string.partial_phase_focal_value,
                sharedPreferences.getString(context.getString(R.string.partial_phase_focal_value), cameraState.focalValue));
        settingsMap.put(R.string.time_lapse_interval,
                sharedPreferences.getString(context.getString(R.string.time_lapse_interval), DEF_TIME_LAPSE_INTERVAL));
        settingsMap.put(R.string.totality_min_shut_speed,
                sharedPreferences.getString(context.getString(R.string.totality_min_shut_speed), cameraState.shutterSpeedValue));
        settingsMap.put(R.string.totality_max_shut_speed,
                sharedPreferences.getString(context.getString(R.string.totality_max_shut_speed), cameraState.shutterSpeedValue));
        settingsMap.put(R.string.totality_focal_value,
                sharedPreferences.getString(context.getString(R.string.totality_focal_value), cameraState.focalValue));
    }

    public void updateByKey(int preferenceKey, String value) {
        applyPreferences(preferenceKey, value);
        settingsMap.put(preferenceKey, value);
    }

    public String getByKey(int preferenceKey) {
        return settingsMap.containsKey(preferenceKey) ? settingsMap.get(preferenceKey) : StringUtils.EMPTY;
    }

    private void applyPreferences(int preferencesKey, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(preferencesKey), value);
        editor.apply();
    }
}
