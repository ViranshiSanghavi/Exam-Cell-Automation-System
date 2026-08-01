package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DeadlineAdapter extends RecyclerView.Adapter<DeadlineAdapter.DeadlineViewHolder> {
    private List<Deadline> deadlines;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public DeadlineAdapter(List<Deadline> deadlines, OnDeleteClickListener listener) {
        this.deadlines = deadlines;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public DeadlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deadline, parent, false);
        return new DeadlineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeadlineViewHolder holder, int position) {
        Deadline deadline = deadlines.get(position);
        holder.titleText.setText(deadline.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        holder.dateText.setText(sdf.format(deadline.getDeadlineDate()));

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deadlines.size();
    }

    class DeadlineViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, dateText;
        ImageButton deleteButton;

        DeadlineViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            dateText = itemView.findViewById(R.id.dateText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
