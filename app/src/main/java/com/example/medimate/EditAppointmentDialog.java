package com.example.medimate;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.Locale;

public class EditAppointmentDialog extends DialogFragment {

    private static final String ARG_APPOINTMENT_ID = "appointmentId";

    public static EditAppointmentDialog newInstance(int appointmentId, AppointmentsAdapter adapter) {
        EditAppointmentDialog fragment = new EditAppointmentDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_APPOINTMENT_ID, appointmentId);
        fragment.setArguments(args);
        fragment.appointmentsAdapter = adapter; // Set the adapter
        return fragment;
    }

    private int appointmentId;
    private AppointmentsAdapter appointmentsAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_edit_appointment_dialog, null);

        assert getArguments() != null;
        appointmentId = getArguments().getInt(ARG_APPOINTMENT_ID);

        EditText editLocation = view.findViewById(R.id.editLocation);
        MaterialTextView editTitle = view.findViewById(R.id.appointmentTitle);
        MaterialTextView editDate = view.findViewById(R.id.editDate);
        MaterialTextView editTime = view.findViewById(R.id.editTime);

        Appointment appointment = getAppointmentDetailsForId(appointmentId);

        if (appointment != null) {
            editLocation.setText(appointment.getLocation());
            editTitle.setText(appointment.getTitle());
            editDate.setText(appointment.getDate());
            editTime.setText(appointment.getTime());
        }

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        Button updateButton = view.findViewById(R.id.updateAppointment);
        updateButton.setOnClickListener(v -> {
            String newLocation = editLocation.getText().toString();
            String newDate = editDate.getText().toString();
            String newTime = editTime.getText().toString();

            DataBaseHelper dbHelper = new DataBaseHelper(requireContext());
            boolean updated = dbHelper.updateAppointment(appointmentId, newLocation, newDate, newTime);

            if (updated) {
                Toast.makeText(requireContext(), "Appointment Updated Successfully", Toast.LENGTH_SHORT).show();
                dismiss();

                // Notify the adapter that the data has changed
                if (appointmentsAdapter != null) {
                    appointmentsAdapter.updateList(dbHelper.getAllAppointments());
                }
            } else {
                Toast.makeText(requireContext(), "Failed to update appointment. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        Button deleteButton = view.findViewById(R.id.deleteAppointment);
        deleteButton.setOnClickListener(v -> deleteAppointment());

        // Set click listeners for date and time
        setDatePicker(editDate);
        setTimePicker(editTime);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private Appointment getAppointmentDetailsForId(int appointmentId) {
        DataBaseHelper dbHelper = new DataBaseHelper(requireContext());
        return dbHelper.getAppointmentById(appointmentId);
    }

    private void deleteAppointment() {
        DataBaseHelper dbHelper = new DataBaseHelper(requireContext());
        boolean deleted = dbHelper.deleteAppointment(appointmentId);

        if (deleted) {
            Toast.makeText(requireContext(), "Appointment Deleted Successfully", Toast.LENGTH_SHORT).show();
            dismiss();

            // Notify the adapter that the data has changed
            if (appointmentsAdapter != null) {
                appointmentsAdapter.updateList(dbHelper.getAllAppointments());
            }
        } else {
            Toast.makeText(requireContext(), "Failed to delete appointment. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDatePicker(MaterialTextView editDate) {
        editDate.setOnClickListener(v -> showDatePicker(editDate));
    }

    private void showDatePicker(final MaterialTextView editDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                    editDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void setTimePicker(MaterialTextView editTime) {
        editTime.setOnClickListener(v -> showTimePicker(editTime));
    }

    private void showTimePicker(final MaterialTextView editTime) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    editTime.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        timePickerDialog.show();
    }
}
