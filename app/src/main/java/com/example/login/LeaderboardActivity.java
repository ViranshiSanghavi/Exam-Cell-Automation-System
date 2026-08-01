package com.example.login;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<LeaderboardItem> leaderboardItems;
    private MediaPlayer mediaPlayer; // MediaPlayer for sound

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Play cheerful sound when leaderboard opens
        playCheerfulSound();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewLeaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setBackgroundColor(Color.LTGRAY); // Debugging visibility

        // Initialize data list
        leaderboardItems = new ArrayList<>();

        // Initialize adapter and set it to RecyclerView
        adapter = new LeaderboardAdapter(leaderboardItems);
        recyclerView.setAdapter(adapter);

        // Populate the leaderboard with sample data
        populateLeaderboardData();
    }

    private void playCheerfulSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.cheer_up);
        if (mediaPlayer != null) {
            mediaPlayer.start(); // Play sound
        }
    }

    private void populateLeaderboardData() {
        leaderboardItems.clear(); // Ensure no duplicate entries

        leaderboardItems.add(new LeaderboardItem(1, "Devanshi", 456, R.drawable.default_avatar));
        leaderboardItems.add(new LeaderboardItem(2, "Viranshi", 450, R.drawable.default_avatar));
        leaderboardItems.add(new LeaderboardItem(3, "Deep", 440, R.drawable.default_avatar));
        leaderboardItems.add(new LeaderboardItem(4, "Hetvi", 400, R.drawable.default_avatar));
        leaderboardItems.add(new LeaderboardItem(5, "Aryan", 398, R.drawable.default_avatar));

        Log.d("LeaderboardActivity", "populateLeaderboardData: Added " + leaderboardItems.size() + " items");

        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Refresh RecyclerView
            Log.d("LeaderboardActivity", "notifyDataSetChanged called");
        } else {
            Log.e("LeaderboardActivity", "Adapter is NULL!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release media player resources
            mediaPlayer = null;
        }
    }
}
