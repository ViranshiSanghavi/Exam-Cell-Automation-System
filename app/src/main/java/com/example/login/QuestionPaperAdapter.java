package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuestionPaperAdapter extends RecyclerView.Adapter<QuestionPaperAdapter.QuestionPaperViewHolder> {

    private List<QuestionPaper> questionPaperList;

    public QuestionPaperAdapter(List<QuestionPaper> questionPaperList) {
        this.questionPaperList = questionPaperList;
    }

    @NonNull
    @Override
    public QuestionPaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_paper, parent, false);
        return new QuestionPaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionPaperViewHolder holder, int position) {
        QuestionPaper questionPaper = questionPaperList.get(position);
        holder.titleTextView.setText(questionPaper.getTitle());
        holder.dateTextView.setText(questionPaper.getDate());
    }

    @Override
    public int getItemCount() {
        return questionPaperList.size();
    }

    static class QuestionPaperViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;

        public QuestionPaperViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}