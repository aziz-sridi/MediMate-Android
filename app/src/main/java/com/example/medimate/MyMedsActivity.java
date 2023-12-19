package com.example.medimate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medimate.databinding.ActivityMymedsBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyMedsActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "MedicationChannel";
    private static final int NOTIFICATION_ID = 1;

    private ActivityMymedsBinding binding;
    private RecyclerView medRecyclerView;

    private MedicationAdapter medicationAdapter;
    private List<Medication> medications;
    private DataBaseHelper dbHelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMymedsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize RecyclerView
        medRecyclerView = binding.medRecyclerView;
        medRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DataBaseHelper(this);

        medications = dbHelper.getAllMedicationsWithTimeRemaining();
        medicationAdapter = new MedicationAdapter(this, medications, this::showEditMedDialog);
        medRecyclerView.setAdapter(medicationAdapter);

        // Set the timeRemaining property for each medication
        for (Medication medication : medications) {
            String timeRemaining = calculateTimeRemaining(medication.getSetTimes());

            // Check if it's time to show a notification
            if (!timeRemaining.equals("No upcoming pills")) {
                showNotification(medication.getName(), timeRemaining);
            }

            medication.setTimeRemaining();
        }

        // Notify the adapter that the data has changed
        medicationAdapter.notifyDataSetChanged();

        binding.backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Homepage.class);
            startActivity(intent);
            finish();
        });
        binding.addMedButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddMedFormActivity.class);
            startActivity(intent);
            finish();
        });
    }
    



    private String calculateTimeRemaining(List<String> setTimes) {
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
            return formatTimeRemaining(timeRemainingMillis);
        } else {
            return "No upcoming pills";
        }
    }

    private long convertTimeToMillis(String time) {
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

    private String formatTimeRemaining(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        return String.format(Locale.getDefault(), "%d hours %d mins", hours, minutes);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(String medicationName, String timeRemaining) {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MedicationAlertActivity.class);
        notificationIntent.putExtra("medicationName", medicationName);
        notificationIntent.putExtra("timeRemaining", timeRemaining);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_medication_liquid_24)
                .setContentTitle("Medication Alert")
                .setContentText("It's time to take your medication: " + medicationName + ". Time remaining: " + timeRemaining)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medication Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showEditMedDialog(Medication medication) {
        EditMedDialog editMedDialog = EditMedDialog.newInstance(medication.getId());
        editMedDialog.show(getSupportFragmentManager(), "EditMedDialogTag");
    }
}
