package edu.hitsz.scorerecord;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreRecord {
    private String playerName;
    private int score;
    private Date playTime;

    public ScoreRecord(String playerName, int score, Date playTime) {
        this.playerName = playerName;
        this.score = score;
        this.playTime = playTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Date playTime) {
        this.playTime = playTime;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return String.format("%s,%d,%s", playerName, score, sdf.format(playTime));
    }
}