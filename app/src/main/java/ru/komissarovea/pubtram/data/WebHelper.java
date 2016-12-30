package ru.komissarovea.pubtram.data;

import java.util.Calendar;

public class WebHelper {

    public static String getUrl(int stopID) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK) - 1;
        String requestFormat = "http://www.minsktrans.by/pda/index.php?StopID=%d&m=1&day=%d";
        String url = String.format(requestFormat, stopID, day);
        return url;
    }
}
