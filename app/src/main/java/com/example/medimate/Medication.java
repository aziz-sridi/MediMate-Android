package com.example.medimate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Medication {
    private int id;
    private String name;
    private final boolean isChronic;
    private final int dosesPerDay;
    private final List<String> setTimes;
    private String timeRemaining;

    public Medication(String name, boolean isChronic, int dosesPerDay, List<String> setTimes) {
        this.name = name;
        this.isChronic = isChronic;
        this.dosesPerDay = dosesPerDay;
        this.setTimes = setTimes;
        setTimeRemaining(); // Call the method to set the initial value for timeRemaining
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChronic() {
        return isChronic;
    }

    public int getDosesPerDay() {
        return dosesPerDay;
    }

    public List<String> getSetTimes() {
        return setTimes;
    }

    public String getTimeRemaining() {
        return timeRemaining;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimeRemaining() {
        long currentTimeMillis = System.currentTimeMillis();
        long nextPillTimeMillis = 0;

        for (String setTime : setTimes) {
            long setTimeMillis = convertTimeToMillis(setTime);

            if (setTimeMillis > currentTimeMillis && (nextPillTimeMillis == 0 || setTimeMillis < nextPillTimeMillis)) {
                nextPillTimeMillis = setTimeMillis;
            }
        }

        if (nextPillTimeMillis > 0) {
            long timeRemainingMillis = nextPillTimeMillis - currentTimeMillis;
            this.timeRemaining = formatTimeRemaining(timeRemainingMillis);
        } else {
            this.timeRemaining = "No upcoming pills";
        }
    }

    private static long convertTimeToMillis(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date date = sdf.parse(time);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static String formatTimeRemaining(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        return String.format(Locale.getDefault(), "%d hours %d mins", hours, minutes);
    }
}
