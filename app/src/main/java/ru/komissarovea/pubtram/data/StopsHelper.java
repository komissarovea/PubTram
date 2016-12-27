package ru.komissarovea.pubtram.data;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.komissarovea.pubtram.R;

public class StopsHelper {

    private static ArrayList<Stop> actualStops;

    public static ArrayList<Stop> getActualStops() {
        return actualStops;
    }

    public static void setActualStops(ArrayList<Stop> actualStops) {
        StopsHelper.actualStops = actualStops;
    }

    public static ArrayList<Stop> getDefaultStops(Context context) {
        ArrayList<Stop> stops = new ArrayList<>();
        try {
            Resources res = context.getResources();
            InputStream inputStream = res.openRawResource(R.raw.stops);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(";");
                if (parts.length > 9) {
                    Stop stop = new Stop();
                    stop.setID(Integer.parseInt(parts[0]));
                    stop.setName(parts[4]);
                    stop.setStreet(parts[3]);
                    stop.setInfo(parts[5]);
                    stop.setStopsString(parts[8]);
                    stop.setStopNumber(parts[9]);

                    String slng = parts[6];
                    if (slng.length() > 3) {
                        slng = slng.substring(0, 2) + "."
                                + slng.substring(2, slng.length());
                        stop.setLongitude(Double.parseDouble(slng));
                    }

                    String slat = parts[7];
                    if (slat.length() > 3) {
                        slat = slat.substring(0, 2) + "."
                                + slat.substring(2, slat.length());
                        stop.setLatitude(Double.parseDouble(slat));
                    }
                    stops.add(stop);
                }
                line = reader.readLine();
                // Log.d("line", line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stops;
    }

    public static ArrayList<Stop> getNextStops(Location currentLocation,
                                               double distance) {
        ArrayList<Stop> nextStops = new ArrayList<>();
        if (currentLocation != null && distance > 0 && actualStops != null
                && actualStops.size() > 0) {
            for (Stop stop : actualStops) {
                double stopDistance = currentLocation.distanceTo(stop
                        .getLocation());
                if (stopDistance <= distance)
                    nextStops.add(stop);
                stop.setCurrentDistance(stopDistance);

                if (nextStops.size() > 1) {
                    Collections.sort(nextStops, new Comparator<Stop>() {
                        @Override
                        public int compare(Stop lhs, Stop rhs) {

                            return lhs.getCurrentDistance().compareTo(
                                    rhs.getCurrentDistance());
                        }
                    });
                }
            }
        }
        return nextStops;
    }

}