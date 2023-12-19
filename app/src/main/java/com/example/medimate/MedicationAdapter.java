package com.example.medimate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {

    private List<Medication> medications;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MedicationAdapter(Context context, List<Medication> medications, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.medications = medications;
        this.onItemClickListener = onItemClickListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.iteam_medication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medication medication = medications.get(position);
        holder.nameTextView.setText(medication.getName());
        StringBuilder setTimesString = new StringBuilder();
        List<String> setTimes = medication.getSetTimes();
        for (String time : setTimes) {
            setTimesString.append(time).append(", ");
        }
        if (setTimesString.length() > 1) {
            setTimesString.setLength(setTimesString.length() - 2);
        }

        holder.timeTextView.setText("Times: " + setTimesString.toString());
        holder.doseTextView.setText("Doses per day: " + medication.getDosesPerDay());
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView;
        TextView timeTextView;
        TextView doseTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            doseTextView = itemView.findViewById(R.id.doseTextView);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(medications.get(position));
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Medication medication);
    }
    public void updateList(List<Medication> updatedList) {
        medications.clear();
        medications.addAll(updatedList);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        // Return the unique ID of the Medication at the given position
        return medications.get(position).getId();
    }
    public void setMedications(List<Medication> medications) {
        this.medications = medications;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        medications.remove(position);
        notifyItemRemoved(position);
    }

}
