package com.fuckolympus.arc.eclipse;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by alex on 1.8.17.
 */
public final class EclipseUtils {

    public static final char SEPARATOR_CHAR = ':';

    private EclipseUtils() {
    }

    public static long calculateFramesForPartialPhase(String totalityTime, String timeLapseInterval) {
        String[] totalityTimeArr = StringUtils.split(totalityTime, SEPARATOR_CHAR);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date currentDate = calendar.getTime();

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                Integer.valueOf(totalityTimeArr[0]), Integer.valueOf(totalityTimeArr[1]), Integer.valueOf(totalityTimeArr[2]));
        Date totalityDate = calendar.getTime();

        long secondsInterval = (totalityDate.getTime() - currentDate.getTime()) / 1000;

        String[] timeLapseIntArr = StringUtils.split(timeLapseInterval, SEPARATOR_CHAR);

        long timeLapseIntervalInSeconds = (Integer.valueOf(timeLapseIntArr[1]) * 60) + Integer.valueOf(timeLapseIntArr[2]);

        return secondsInterval / timeLapseIntervalInSeconds;
    }
}
