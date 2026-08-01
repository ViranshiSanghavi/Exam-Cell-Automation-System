package com.example.login;

public class LeaderboardItem {
    private int rank;
    private String name;
    private int points;
    private int imageResource;

    public LeaderboardItem(int rank, String name, int points, int imageResource) {
        this.rank = rank;
        this.name = name;
        this.points = points;
        this.imageResource = imageResource;
    }

    public int getRank() { return rank; }
    public String getName() { return name; }
    public int getPoints() { return points; }
    public int getImageResource() { return imageResource; }
}
