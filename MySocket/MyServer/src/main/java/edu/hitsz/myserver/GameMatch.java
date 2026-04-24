package edu.hitsz.myserver;

/**
 * 代表一场游戏配对（两个玩家之间的对战）
 */
public class GameMatch {
    private String matchId;            // 配对ID
    private String player1Id;          // 玩家1 ID
    private String player2Id;          // 玩家2 ID
    private ClientHandler player1Handler;  // 玩家1 的处理器
    private ClientHandler player2Handler;  // 玩家2 的处理器
    private int player1Score;          // 玩家1 分数
    private int player2Score;          // 玩家2 分数
    private boolean player1Dead;       // 玩家1 是否死亡
    private boolean player2Dead;       // 玩家2 是否死亡
    private boolean isGameEnded;       // 游戏是否结束
    private long createdTime;          // 创建时间

    public GameMatch(String matchId, String player1Id, String player2Id,
                     ClientHandler player1Handler, ClientHandler player2Handler) {
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player1Handler = player1Handler;
        this.player2Handler = player2Handler;
        this.player1Score = 0;
        this.player2Score = 0;
        this.player1Dead = false;
        this.player2Dead = false;
        this.isGameEnded = false;
        this.createdTime = System.currentTimeMillis();
    }

    public String getMatchId() {
        return matchId;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public ClientHandler getPlayer1Handler() {
        return player1Handler;
    }

    public ClientHandler getPlayer2Handler() {
        return player2Handler;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int score) {
        this.player1Score = score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int score) {
        this.player2Score = score;
    }

    public boolean isPlayer1Dead() {
        return player1Dead;
    }

    public void setPlayer1Dead(boolean dead) {
        this.player1Dead = dead;
    }

    public boolean isPlayer2Dead() {
        return player2Dead;
    }

    public void setPlayer2Dead(boolean dead) {
        this.player2Dead = dead;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        isGameEnded = gameEnded;
    }

    public boolean isBothPlayersDead() {
        return player1Dead && player2Dead;
    }

    public String getWinnerId() {
        if (player1Dead && !player2Dead) {
            return player2Id;
        } else if (player2Dead && !player1Dead) {
            return player1Id;
        }
        // 如果都死了，比较分数
        return player1Score >= player2Score ? player1Id : player2Id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return "GameMatch{" +
                "matchId='" + matchId + '\'' +
                ", player1Id='" + player1Id + '\'' +
                ", player2Id='" + player2Id + '\'' +
                ", player1Score=" + player1Score +
                ", player2Score=" + player2Score +
                ", player1Dead=" + player1Dead +
                ", player2Dead=" + player2Dead +
                '}';
    }
}
