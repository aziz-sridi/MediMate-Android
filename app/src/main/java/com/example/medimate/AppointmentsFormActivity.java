// AppointmentsFormActivity.java
package com.example.medimate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medimate.databinding.ActivityAppointmentsFormBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AppointmentsFormActivity extends AppCompatActivity {

    ActivityAppointmentsFormBinding binding;
    private Calendar selectedDate;
    private Calendar selectedTime;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentsFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DataBaseHelper(this);

        selectedDate = Calendar.getInstance();
        selectedTime = Calendar.getInstance();

        binding.dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        binding.buttonAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAppointmentToDatabase();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);
                        updateDateTextView();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        updateTimeTextView();
                    }
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                false);

        timePickerDialog.show();
    }

    private void updateDateTextView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        binding.dateTextView.setText("Selected Date: " + dateFormat.format(selectedDate.getTime()));
    }

    private void updateTimeTextView() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        binding.timeTextView.setText("Selected Time: " + timeFormat.format(selectedTime.getTime()));
    }

    private void addAppointmentToDatabase() {
        String title = binding.editTextTitle.getText().toString();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime());
        String time = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(selectedTime.getTime());
        String location = binding.editTextLocation.getText().toString();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {

            boolean success = dbHelper.insertAppointment(title, date, time, location);

            if (success) {
                Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MyAppointmentsActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to add appointment", Toast.LENGTH_SHORT).show();

            }
        }
    }


}

