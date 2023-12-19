package com.example.medimate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditMedDialog extends DialogFragment {

    private static final String ARG_MEDICATION_ID = "medId";

    private EditText etPillsPerDay;
    private TableLayout tableTiming;
    private int medId;
    private MedicationAdapter medicationAdapter;

    public EditMedDialog() {
    }

    public static EditMedDialog newInstance(int medicationId) {
        EditMedDialog fragment = new EditMedDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_MEDICATION_ID, medicationId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_edit_med_dialog, null);

        assert getArguments() != null;
        medId = getArguments().getInt(ARG_MEDICATION_ID);

        etPillsPerDay = view.findViewById(R.id.etPillsPerDay);
        Button btnOk = view.findViewById(R.id.btnOk);
        tableTiming = view.findViewById(R.id.tableTiming);

        btnOk.setOnClickListener(v -> generateTimeSettingButtons());
        Button updateButton = view.findViewById(R.id.updateMed);
        Button deleteButton = view.findViewById(R.id.deleteMed);

        deleteButton.setOnClickListener(v -> deleteMed());
        updateButton.setOnClickListener(v -> updateMed());

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void generateTimeSettingButtons() {
        tableTiming.removeAllViews(); // Clear existing rows

        int dosesPerDay = getDosesPerDay();

        for (int dose = 1; dose <= dosesPerDay; dose++) {
            TableRow row = new TableRow(requireContext());

            // Create Button for setting time
            Button btnSetTime = new Button(requireContext());
            btnSetTime.setText(String.format(Locale.getDefault(), "Set Time for Dose %d", dose));
            btnSetTime.setOnClickListener(v -> showTimePicker(btnSetTime));

            row.addView(btnSetTime);

            tableTiming.addView(row);
        }
    }

    private int getDosesPerDay() {
        String input = etPillsPerDay.getText().toString().trim();

        if (input.isEmpty()) {
            showToast("Please enter a value.");
            etPillsPerDay.setText("1");
            return 1;
        }

        try {
            int doses = Integer.parseInt(input);
            doses = Math.min(5, doses);
            etPillsPerDay.setText(String.valueOf(doses));
            return doses;
        } catch (NumberFormatException e) {
            showToast("Invalid input. Please enter a number.");
            etPillsPerDay.setText("1");
            return 1;
        }
    }

    private void showTimePicker(Button btnSetTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    showToast("Selected Time: " + selectedTime);
                    btnSetTime.setText(selectedTime);
                },
                12,
                0,
                false
        );

        timePickerDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void deleteMed() {
        try {
            DataBaseHelper dbHelper = new DataBaseHelper(requireContext());
            boolean deleted = dbHelper.deleteMedication(medId);
            Log.e("EditMedDialog", String.valueOf(medId));

            if (deleted) {
                Toast.makeText(requireContext(), "Medication Deleted Successfully", Toast.LENGTH_SHORT).show();
                dismiss();

                if (medicationAdapter != null) {
                    medicationAdapter.updateList( dbHelper.getAllMedicationsWithTimeRemaining());
                } else {
                    Log.e("EditMedDialog", "medicationAdapter is null");
                }
            } else {
                Toast.makeText(requireContext(), "Failed to delete medication. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EditMedDialog", "Exception in deleteMed: " + e.getMessage());
        }

    }

    private void updateMed() {
        try {
            DataBaseHelper dbHelper = new DataBaseHelper(requireContext());
            int dosesPerDay = getDosesPerDay();
             List<String> setTimes = getSetTimes();
            Medication updatedMedication = new Medication("", false, dosesPerDay, setTimes);
            updatedMedication.setId(medId);

            boolean updated = dbHelper.updateMedication(updatedMedication);

            if (updated) {
                Toast.makeText(requireContext(), "Medication updated Successfully", Toast.LENGTH_SHORT).show();
                dismiss();

                if (medicationAdapter != null) {
                    medicationAdapter.updateList( dbHelper.getAllMedicationsWithTimeRemaining());
                } else {
                    Log.e("EditMedDialog", "medicationAdapter is null");
                }
            } else {
                Toast.makeText(requireContext(), "Failed to update medication. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EditMedDialog", "Exception in updateMed: " + e.getMessage());
        }
    }

    private List<String> getSetTimes() {
        List<String> setTimes = new ArrayList<>();

        for (int i = 0; i < tableTiming.getChildCount(); i++) {
            TableRow row = (TableRow) tableTiming.getChildAt(i);
            Button btnSetTime = (Button) row.getChildAt(0);
            setTimes.add(btnSetTime.getText().toString());
        }

        return setTimes;

    }

}

