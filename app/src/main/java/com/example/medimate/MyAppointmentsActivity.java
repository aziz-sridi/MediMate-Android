package com.example.medimate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medimate.databinding.ActivityMyappointemntsBinding;

import java.util.List;

public class MyAppointmentsActivity extends AppCompatActivity implements AppointmentsAdapter.OnItemClickListener {

    private ActivityMyappointemntsBinding binding;
    private RecyclerView appointmentRecyclerView;
    private AppointmentsAdapter appointmentsAdapter;
    private List<Appointment> appointments;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyappointemntsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize RecyclerView
        appointmentRecyclerView = binding.appointmentsRecyclerView;
        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve appointments from the database
        dbHelper = new DataBaseHelper(this);
        appointments = dbHelper.getAllAppointments();


        // Set up the RecyclerView
        appointmentsAdapter = new AppointmentsAdapter(this, appointments, dbHelper);
        appointmentsAdapter.setOnItemClickListener(this);
        appointmentRecyclerView.setAdapter(appointmentsAdapter);

        binding.addAppointmentButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AppointmentsFormActivity.class));
            finish();
        });x

        binding.backButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Homepage.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });
    }

    @Override
    public void onItemClick(int position) {
        showEditDialog(position);
    }

    private void showEditDialog(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int appointmentId = appointments.get(position).getId();
        EditAppointmentDialog editAppointmentDialog = EditAppointmentDialog.newInstance(appointmentId, appointmentsAdapter);
        editAppointmentDialog.show(fragmentManager, "edit_dialog");
    }
}
