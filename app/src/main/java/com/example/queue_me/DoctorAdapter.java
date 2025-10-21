package com.example.queue_me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctorList;
    private List<Doctor> doctorListFull; // For search functionality

    public DoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
        this.doctorListFull = new ArrayList<>(doctorList);
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        holder.txtDoctorName.setText(doctor.getName());
        holder.txtDepartment.setText(doctor.getDepartment());
        holder.txtQueueCount.setText(doctor.getQueueCount() + " patients");

        // Click listener for entire item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DoctorDetailsActivity.class);
                intent.putExtra("doctor_id", doctor.getId());
                intent.putExtra("doctor_name", doctor.getName());
                intent.putExtra("doctor_department", doctor.getDepartment());
                intent.putExtra("queue_count", doctor.getQueueCount());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    // Search/Filter method
    public void filter(String searchText) {
        doctorList.clear();
        if (searchText.isEmpty()) {
            doctorList.addAll(doctorListFull);
        } else {
            searchText = searchText.toLowerCase();
            for (Doctor doctor : doctorListFull) {
                if (doctor.getName().toLowerCase().contains(searchText) ||
                        doctor.getDepartment().toLowerCase().contains(searchText)) {
                    doctorList.add(doctor);
                }
            }
        }
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class DoctorViewHolder extends RecyclerView.ViewHolder {

        TextView txtDoctorName;
        TextView txtDepartment;
        TextView txtQueueCount;
        TextView btnJoinQueue;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDoctorName = itemView.findViewById(R.id.txtDoctorName);
            txtDepartment = itemView.findViewById(R.id.txtDepartment);
            txtQueueCount = itemView.findViewById(R.id.txtQueueCount);
            btnJoinQueue = itemView.findViewById(R.id.btnJoinQueue);
        }
    }
}
