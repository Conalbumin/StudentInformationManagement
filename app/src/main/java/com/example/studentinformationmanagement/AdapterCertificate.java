package com.example.studentinformationmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCertificate extends RecyclerView.Adapter<AdapterCertificate.CertificateViewHolder> {

    private ArrayList<Certificate> certificateArrayList;
    private Context context;

    public interface OnItemClickListener {
        void onDeleteClick(int position);

        void onModifyClick(int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdapterCertificate(Context context, ArrayList<Certificate> certificateArrayList) {
        this.context = context;
        this.certificateArrayList = certificateArrayList;
    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate, parent, false);
        return new CertificateViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        Certificate certificate = certificateArrayList.get(position);
        // Set the certificate name directly
        holder.certificateName.setText(certificate.getName());
    }

    @Override
    public int getItemCount() {
        return certificateArrayList.size();
    }

    public void setStudentList(ArrayList<Certificate> certificateArrayList) {
        this.certificateArrayList = certificateArrayList;
        notifyDataSetChanged();
    }

    public void removeCertificate(int position) {
        certificateArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public Certificate getItem(int position) {
        return certificateArrayList.get(position);
    }

    public void updateCertificateName(int position, String newCertificateName) {
        Certificate certificate = certificateArrayList.get(position);
        certificate.setName(newCertificateName);
        notifyItemChanged(position);
    }

    public static class CertificateViewHolder extends RecyclerView.ViewHolder {
        TextView certificateName;
        ImageView deleteButton;

        public CertificateViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            certificateName = itemView.findViewById(R.id.certificate);
            deleteButton = itemView.findViewById(R.id.ic_delete_certificate);

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });

            certificateName.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onModifyClick(position);
                    }
                }
            });
        }
    }

}
