package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

public class ScoreInfo {

    public String username;

    public ScoreInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ScoreInfo(String username) {
        this.username = username;
    }

}
