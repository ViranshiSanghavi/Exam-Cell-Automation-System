package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<LeaderboardItem> leaderboardItems;

    public LeaderboardAdapter(List<LeaderboardItem> leaderboardItems) {
        this.leaderboardItems = leaderboardItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardItem item = leaderboardItems.get(position);

        holder.rankTextView.setText(item.getRank() + ".");
        holder.nameTextView.setText(item.getName());
        holder.pointsTextView.setText(item.getPoints() + " pts");
        holder.profileImageView.setImageResource(item.getImageResource());
    }

    @Override
    public int getItemCount() {
        return leaderboardItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView, nameTextView, pointsTextView;
        ImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            pointsTextView = itemView.findViewById(R.id.pointsTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
