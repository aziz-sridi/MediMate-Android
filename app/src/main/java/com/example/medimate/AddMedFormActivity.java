package com.example.medimate;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medimate.databinding.ActivityAddMedFromBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddMedFormActivity extends AppCompatActivity {

    public static final String EXTRA_MEDICATION_SAVED = "com.example.medimate.EXTRA_MEDICATION_SAVED";

    private ActivityAddMedFromBinding binding;
    private TextView textViewYear, textViewMonth, textViewDay;
    private int selectedYear = 0, selectedMonth = 0, selectedDay = 0;
    private Map<Integer, String> doseTimes = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMedFromBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize TextViews
        textViewYear = findViewById(R.id.textViewYear);
        textViewMonth = findViewById(R.id.textViewMonth);
        textViewDay = findViewById(R.id.textViewDay);

        // Set an OnClickListener on the TextViews
        textViewYear.setOnClickListener(v -> showPopup(v, getYearData(), textViewYear));
        textViewMonth.setOnClickListener(v -> showPopup(v, getMonthData(), textViewMonth));
        textViewDay.setOnClickListener(v -> showPopup(v, getDayData(), textViewDay));
        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateMedicationTimingTable();
            }
        });
        initConfirmButton();
    }

    private void showPopup(View anchorView, String[] data, TextView targetTextView) {
        // Inflate the layout for the PopupWindow
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_list, null);

        // Create a new PopupWindow with a maximum height
        PopupWindow popupWindow = new PopupWindow(popupView, anchorView.getWidth(), getMaxPopupHeight(), true);
        popupWindow.setContentView(popupView);

        // Set up the ListView
        ListView listView = popupView.findViewById(R.id.listViewPopup);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);

        // Set up the OnItemClickListener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = adapter.getItem(position);
            targetTextView.setText(selectedItem);
            popupWindow.dismiss();
        });

        // Show the PopupWindow below the anchorView
        popupWindow.showAsDropDown(anchorView);

    }

    private int getMaxPopupHeight() {
        // Set your desired maximum height for the popup
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int maxHeight = displayMetrics.heightPixels / 4; // Adjust as needed
        return maxHeight;
    }

    private String[] getYearData() {
        return new String[]{"1", "2", "3"}; // Replace with your data
    }

    private String[] getMonthData() {
        return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    }

    private String[] getDayData() {
        // Example: Generate days from 1 to 31
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = String.valueOf(i + 1);
        }
        return days;
    }
    private void populateMedicationTimingTable() {
        // Clear existing rows
        binding.tableTiming.removeAllViews();

        // Get the number of doses per day
        int dosesPerDay = getDosesPerDay();

        // Create a new row for each dose
        for (int dose = 1; dose <= dosesPerDay; dose++) {
            TableRow row = new TableRow(this);

            // Create TextView for Dose
            TextView tvDose = new TextView(this);
            tvDose.setText(String.format("Dose %d", dose));

            // Create Button for Set Time
            Button btnSetTime = new Button(this);
            btnSetTime.setText("Set Time");
            btnSetTime.setOnClickListener(v -> showTimePicker(btnSetTime));

            // Add TextView and Button to the row
            row.addView(tvDose);
            row.addView(btnSetTime);

            // Add the row to the table
            binding.tableTiming.addView(row);
        }
    }

    private int getDosesPerDay() {
        EditText etPillsPerDay = findViewById(R.id.etPillsPerDay);
        String input = etPillsPerDay.getText().toString().trim();

        if (input.isEmpty()) {
            showToast("Please enter a value.");
            etPillsPerDay.setText("1");
            return 1;
        }

        try {
            int doses = Integer.parseInt(input);

            // Cap the value at 5
            doses = Math.min(5, doses);

            // Update the EditText with the modified value
            etPillsPerDay.setText(String.valueOf(doses));

            return doses;
        } catch (NumberFormatException e) {
            showToast("Invalid input. Please enter a number.");
            etPillsPerDay.setText("1");
            return 1; // Default to 1 if parsing fails
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }






    private void showTimePicker(Button btnSetTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    // Handle the selected time (hourOfDay and minute)
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    showToast("Selected Time: " + selectedTime);

                    // Set the selected time as the text of the button
                    btnSetTime.setText(selectedTime);
                },
                // Set initial time to 12:00 PM
                12,
                0,
                false
        );

        timePickerDialog.show();
    }
    private void initConfirmButton() {
        Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicationDataToDatabase();
                startActivity(new Intent(getApplicationContext(), MyMedsActivity.class));
                finish();
            }
        });
    }

    private void saveMedicationDataToDatabase() {
        // Collect medication information from the UI
        String medicationName = binding.etMedName.getText().toString();
        boolean isChronic = binding.rbChronic.isChecked();
        int dosesPerDay = getDosesPerDay();
        List<String> setTimes = getSetTimesFromTable();

        // Check if any of the fields are empty
        if (medicationName.isEmpty() || (setTimes.isEmpty() && dosesPerDay == 0)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            Medication medication = new Medication(medicationName, isChronic, dosesPerDay, setTimes);

            DataBaseHelper dbHelper = new DataBaseHelper(this);
            boolean isSaved = dbHelper.saveMedication(medication);
            if (isSaved) {
                Toast.makeText(this, "Medication saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save medication. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private List<String> getSetTimesFromTable() {
        List<String> setTimes = new ArrayList<>();

        // Iterate over the rows in the table and extract set times
        for (int i = 0; i < binding.tableTiming.getChildCount(); i++) {
            TableRow row = (TableRow) binding.tableTiming.getChildAt(i);
            Button btnSetTime = (Button) row.getChildAt(1);

            // Extract set time from the button text
            String setTime = btnSetTime.getText().toString();
            setTimes.add(setTime);
        }

        return setTimes;
    }
}
