package edu.hitsz.application;

import edu.hitsz.mysocket.SocketClient;

/**
 * 游戏全局管理器 - 管理Socket连接等全局资源
 */
public class GameManager {
    private static GameManager instance;
    private SocketClient socketClient;
    private String playerId;
    private String opponentId;

    private GameManager() {
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    public void setSocketClient(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(String opponentId) {
        this.opponentId = opponentId;
    }

    public void clear() {
        socketClient = null;
        playerId = null;
        opponentId = null;
    }
}
