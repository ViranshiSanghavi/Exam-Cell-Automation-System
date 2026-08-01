package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExamScheduleAdapter extends RecyclerView.Adapter<ExamScheduleAdapter.ViewHolder> {
    private List<ExamScheduleItem> examList;

    public ExamScheduleAdapter(List<ExamScheduleItem> examList) {
        this.examList = new ArrayList<>(examList);  // Ensure encapsulation
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamScheduleItem exam = examList.get(position);
        holder.subjectTextView.setText(exam.getSubject());
        holder.timeTextView.setText(exam.getTime());
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    // ✅ **Fix: Add this method to update the exam list dynamically**
    public void updateExamList(List<ExamScheduleItem> newExamList) {
        examList.clear();  // Clear the existing list
        examList.addAll(newExamList);  // Add new data
        notifyDataSetChanged();  // Refresh RecyclerView
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;
        TextView timeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.textViewSubject);
            timeTextView = itemView.findViewById(R.id.textViewTime);
        }
    }
}
