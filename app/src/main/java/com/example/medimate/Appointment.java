package com.example.medimate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Appointment {
    private int id;
    private String title;
    private String date;
    private String time;
    private String location;
    public Appointment(String title, String date, String time, String location) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
    }
    public int getId() {
        return id;
    }
    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDaysRemaining() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date appointmentDate = dateFormat.parse(date);
            Date currentDate = new Date();

            assert appointmentDate != null;
            long timeDiff = appointmentDate.getTime() - currentDate.getTime();
            long daysRemaining = TimeUnit.MILLISECONDS.toDays(timeDiff);

            return String.valueOf(daysRemaining);
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}
