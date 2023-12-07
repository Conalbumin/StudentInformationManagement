package com.example.studentinformationmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.HistoryViewHolder> {
    private ArrayList<LoginHistoryItem> historyList;
    private Context context;

    public AdapterHistory(Context context, ArrayList<LoginHistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public AdapterHistory.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_login, parent, false);
        return new AdapterHistory.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistory.HistoryViewHolder holder, int position) {
        LoginHistoryItem historyItem = historyList.get(position);

        // Set the login time to the TextView
        holder.loginTime.setText(historyItem.getTimeLogin());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setHistoryList(ArrayList<LoginHistoryItem> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView loginTime;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            loginTime = itemView.findViewById(R.id.loginTime);
        }
    }
}
