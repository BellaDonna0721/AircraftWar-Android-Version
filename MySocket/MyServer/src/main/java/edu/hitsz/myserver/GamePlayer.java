package edu.hitsz.myserver;

import java.io.Serializable;

/**
 * 代表一个游戏玩家的类
 */
public class GamePlayer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerId;           // 玩家唯一ID
    private int score;                 // 玩家分数
    private boolean isDead;            // 是否死亡
    private long lastScoreUpdateTime;  // 最后一次分数更新时间
    private String matchId;            // 配对ID

    public GamePlayer(String playerId) {
        this.playerId = playerId;
        this.score = 0;
        this.isDead = false;
        this.lastScoreUpdateTime = System.currentTimeMillis();
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        this.lastScoreUpdateTime = System.currentTimeMillis();
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public long getLastScoreUpdateTime() {
        return lastScoreUpdateTime;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "playerId='" + playerId + '\'' +
                ", score=" + score +
                ", isDead=" + isDead +
                '}';
    }
}
