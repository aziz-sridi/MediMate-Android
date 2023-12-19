package com.example.medimate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    private List<Appointment> appointments;
    private final Context context;
    private final DataBaseHelper dbHelper;
    private OnItemClickListener onItemClickListener; // Add this line

    public AppointmentsAdapter(Context context, List<Appointment> appointments, DataBaseHelper dbHelper) {
        this.context = context;
        this.appointments = appointments;
        this.dbHelper = dbHelper;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_appointement, parent, false);
        return new AppointmentViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        holder.nameTextView.setText(appointment.getTitle());
        holder.dateTextView.setText("Date: " + appointment.getDate());
        holder.timeTextView.setText("Time: " + appointment.getTime());
        holder.locationTextView.setText("Location: " + appointment.getLocation());
        holder.dayRemainingTextView.setText("Days Remaining: " + appointment.getDaysRemaining());
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // In your AppointmentsAdapter class
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView locationTextView;
        TextView dayRemainingTextView;

        public AppointmentViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            dayRemainingTextView = itemView.findViewById(R.id.dayremainigTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
    public void updateList(List<Appointment> newList) {
        appointments = newList;
        notifyDataSetChanged();
    }
}
