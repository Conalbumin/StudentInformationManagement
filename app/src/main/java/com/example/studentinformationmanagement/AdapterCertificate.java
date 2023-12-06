package com.example.studentinformationmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCertificate extends RecyclerView.Adapter<AdapterCertificate.CertificateViewHolder> {

    private ArrayList<Certificate> certificateArrayList;
    private Context context;

    public AdapterCertificate(Context context, ArrayList<Certificate> certificateArrayList) {
        this.context = context;
        this.certificateArrayList = certificateArrayList;
    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        Certificate certificate = certificateArrayList.get(position);
        holder.certificateName.setText(certificate.toString());
    }

    @Override
    public int getItemCount() {
        return certificateArrayList.size();
    }

    public void setStudentList(ArrayList<Certificate> certificateArrayList) {
        this.certificateArrayList = certificateArrayList;
        notifyDataSetChanged();
    }

    public static class CertificateViewHolder extends RecyclerView.ViewHolder {
        TextView certificateName;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            certificateName = itemView.findViewById(R.id.certificate);
        }
    }
}
