package edu.hitsz.mysocket;

import java.io.Serializable;

/**
 * 游戏消息类，用于服务器和客户端之间的通信
 */
public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    // 消息类型常量
    public static final String MSG_JOIN = "JOIN";                    // 玩家加入等待队列
    public static final String MSG_MATCHED = "MATCHED";              // 匹配成功
    public static final String MSG_SCORE_UPDATE = "SCORE_UPDATE";    // 分数更新
    public static final String MSG_PLAYER_DEAD = "PLAYER_DEAD";      // 玩家死亡
    public static final String MSG_GAME_END = "GAME_END";            // 游戏结束
    public static final String MSG_DISCONNECT = "DISCONNECT";        // 玩家断开连接

    private String type;              // 消息类型
    private String playerId;          // 玩家ID
    private int score;                // 分数
    private int opponentScore;        // 对手分数
    private String opponentId;        // 对手ID
    private String message;           // 附加消息
    private boolean isWinner;         // 是否是赢家
    private boolean bothDead;         // 两方是否都死亡

    public GameMessage() {
    }

    public GameMessage(String type, String playerId) {
        this.type = type;
        this.playerId = playerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    public String getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(String opponentId) {
        this.opponentId = opponentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public boolean isBothDead() {
        return bothDead;
    }

    public void setBothDead(boolean bothDead) {
        this.bothDead = bothDead;
    }

    @Override
    public String toString() {
        return "GameMessage{" +
                "type='" + type + '\'' +
                ", playerId='" + playerId + '\'' +
                ", score=" + score +
                ", opponentScore=" + opponentScore +
                ", opponentId='" + opponentId + '\'' +
                '}';
    }
}
