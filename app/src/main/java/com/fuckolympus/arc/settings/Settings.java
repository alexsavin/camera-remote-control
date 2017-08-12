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

    public static final String DEF_TOTALITY_TIME = "10:19:30";
    public static final String DEF_TOTALITY_DURATION = "00:02:01";
    public static final String DEF_TIME_LAPSE_INTERVAL = "00:01:00";
    public static final String DEF_SHUT_SPEED_SET = "4\", 1\", 2, 4, 8, 15, 30, 60, 125, 250, 500, 1000, 2000";
    public static final String DEF_TOTALITY_FOCAL_VALUE = "11";
    public static final String DEF_SHUT_SPEED_VALUE = "500";
    public static final String[] INTERVALS = new String[] {"2500", "3000", "3500", "4000", "4500", "5000"};
    public static final String DEF_INTERVAL = "5000";

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
                sharedPreferences.getString(context.getString(R.string.partial_phase_shut_speed), DEF_SHUT_SPEED_VALUE));
        settingsMap.put(R.string.partial_phase_focal_value,
                sharedPreferences.getString(context.getString(R.string.partial_phase_focal_value), DEF_TOTALITY_FOCAL_VALUE));
        settingsMap.put(R.string.time_lapse_interval,
                sharedPreferences.getString(context.getString(R.string.time_lapse_interval), DEF_TIME_LAPSE_INTERVAL));
        settingsMap.put(R.string.totality_shut_speed_set,
                sharedPreferences.getString(context.getString(R.string.totality_shut_speed_set), DEF_SHUT_SPEED_SET));
        settingsMap.put(R.string.totality_focal_value,
                sharedPreferences.getString(context.getString(R.string.totality_focal_value), DEF_TOTALITY_FOCAL_VALUE));
        settingsMap.put(R.string.totality_interval, sharedPreferences.getString(context.getString(R.string.totality_interval), DEF_INTERVAL));
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

    public void reset() {
        updateByKey(R.string.totality_time, DEF_TOTALITY_TIME);
        updateByKey(R.string.totality_duration, DEF_TOTALITY_DURATION);
        updateByKey(R.string.partial_phase_shut_speed, DEF_SHUT_SPEED_VALUE);
        updateByKey(R.string.partial_phase_focal_value, DEF_TOTALITY_FOCAL_VALUE);
        updateByKey(R.string.time_lapse_interval, DEF_TIME_LAPSE_INTERVAL);
        updateByKey(R.string.totality_shut_speed_set, DEF_SHUT_SPEED_SET);
        updateByKey(R.string.totality_focal_value, DEF_TOTALITY_FOCAL_VALUE);
        updateByKey(R.string.totality_interval, DEF_INTERVAL);
    }
}
