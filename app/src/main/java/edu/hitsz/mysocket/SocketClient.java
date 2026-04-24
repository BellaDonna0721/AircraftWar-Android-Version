package edu.hitsz.mysocket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Socket客户端，用于连接到游戏服务器
 */
public class SocketClient {
    private static final String TAG = "SocketClient";
    // 模拟器上使用10.0.2.2代替localhost，真实设备需要修改为服务器IP
    private static String SERVER_HOST = "10.0.2.2"; // 默认模拟器地址，可通过setServerHost()修改
    private static final int SERVER_PORT = 9999;
    private static final int CONNECT_TIMEOUT = 10000; // 连接超时10秒
    private static final int RECONNECT_INTERVAL = 3000; // 重连间隔3秒
    private static final int MAX_RECONNECT_ATTEMPTS = 5; // 最多重连5次

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String playerId;
    private String opponentId;
    private boolean isConnected;
    private boolean shouldReconnect = true; // 是否继续尝试重连
    private MessageCallback messageCallback;
    private Handler mainHandler;

    public interface MessageCallback {
        void onConnected(String playerId);
        void onMatched(String opponentId);
        void onScoreUpdate(String playerId, int score);
        void onOpponentScoreUpdate(String opponentId, int score);
        void onPlayerDead(String playerId);
        void onGameEnd(String winnerId, int myScore, int opponentScore, boolean isWinner);
        void onDisconnected();
        void onError(String message);
    }

    public SocketClient(MessageCallback callback) {
        this.messageCallback = callback;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.isConnected = false;
        this.playerId = null;
        this.opponentId = null;
    }

    /**
     * 设置消息回调
     */
    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
    }

    /**
     * 设置服务器地址（连接前调用）
     */
    public static void setServerHost(String host) {
        SERVER_HOST = host;
        Log.d(TAG, "服务器地址已设置为: " + SERVER_HOST);
    }

    /**
     * 连接到服务器（带重试机制）
     */
    public void connect() {
        shouldReconnect = true;
        attemptConnect(0);
    }

    /**
     * 尝试连接（支持重试）
     */
    private void attemptConnect(int attemptNumber) {
        if (!shouldReconnect) {
            Log.d(TAG, "用户已取消连接");
            return;
        }

        new Thread(() -> {
            try {
                Log.d(TAG, "连接尝试 " + (attemptNumber + 1) + "/" + MAX_RECONNECT_ATTEMPTS + 
                      " - 正在连接到: " + SERVER_HOST + ":" + SERVER_PORT);
                
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                socket.setSoTimeout(0); // 避免读取超时
                
                // 注意：必须先创建输出流，再创建输入流，避免握手死锁
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
                
                isConnected = true;
                shouldReconnect = true;
                Log.d(TAG, "已连接到服务器");
                
                // 开始监听消息
                listenForMessages();
                
            } catch (SocketTimeoutException e) {
                Log.e(TAG, "连接超时: " + e.getMessage());
                handleConnectionFailure("连接超时，请检查服务器是否运行", attemptNumber);
                
            } catch (IOException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "连接失败: " + e.getClass().getSimpleName();
                }
                Log.e(TAG, "连接异常: " + errorMsg);
                handleConnectionFailure("连接失败: " + errorMsg, attemptNumber);
            }
        }).start();
    }

    /**
     * 处理连接失败，判断是否重试
     */
    private void handleConnectionFailure(String errorMsg, int attemptNumber) {
        if (!shouldReconnect) {
            Log.d(TAG, "用户已取消，不再重试");
            handleError(errorMsg);
            return;
        }

        if (attemptNumber < MAX_RECONNECT_ATTEMPTS - 1) {
            // 还有重试次数
            String retryMsg = errorMsg + " (第" + (attemptNumber + 1) + "次失败，" + (MAX_RECONNECT_ATTEMPTS - attemptNumber - 1) + "秒后重试...)";
            
            mainHandler.post(() -> {
                if (messageCallback != null) {
                    messageCallback.onError(retryMsg);
                }
            });

            // 等待后重试
            try {
                Thread.sleep(RECONNECT_INTERVAL);
            } catch (InterruptedException e) {
                Log.e(TAG, "重连等待被打断: " + e.getMessage());
            }
            
            if (shouldReconnect) {
                attemptConnect(attemptNumber + 1);
            }
        } else {
            // 已达到最大重试次数
            String finalMsg = errorMsg + " (已重试" + MAX_RECONNECT_ATTEMPTS + "次，请检查网络连接)";
            Log.e(TAG, "已达最大重试次数，放弃连接");
            handleError(finalMsg);
        }
    }

    /**
     * 监听来自服务器的消息
     */
    private void listenForMessages() {
        try {
            while (isConnected) {
                try {
                    GameMessage msg = (GameMessage) in.readObject();
                    if (msg != null) {
                        handleMessage(msg);
                    }
                } catch (EOFException e) {
                    Log.d(TAG, "连接已关闭");
                    isConnected = false;
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "消息类型未找到: " + e.getMessage());
                    isConnected = false;
                } catch (IOException e) {
                    if (isConnected) {
                        Log.e(TAG, "读取消息失败: " + e.getMessage());
                    }
                    isConnected = false;
                }
            }
        } finally {
            handleDisconnected();
        }
    }

    /**
     * 处理来自服务器的消息
     */
    private void handleMessage(GameMessage msg) {
        Log.d(TAG, "收到消息: " + msg);
        
        String type = msg.getType();
        
        switch (type) {
            case "WELCOME":
                playerId = msg.getPlayerId();
                mainHandler.post(() -> {
                    if (messageCallback != null) {
                        messageCallback.onConnected(playerId);
                    }
                });
                break;
                
            case GameMessage.MSG_MATCHED:
                opponentId = msg.getOpponentId();
                mainHandler.post(() -> {
                    if (messageCallback != null) {
                        messageCallback.onMatched(opponentId);
                    }
                });
                break;
                
            case GameMessage.MSG_SCORE_UPDATE:
                int score = msg.getScore();
                String msgPlayerId = msg.getPlayerId();
                if (msgPlayerId.equals(this.playerId)) {
                    mainHandler.post(() -> {
                        if (messageCallback != null) {
                            messageCallback.onScoreUpdate(msgPlayerId, score);
                        }
                    });
                } else {
                    mainHandler.post(() -> {
                        if (messageCallback != null) {
                            messageCallback.onOpponentScoreUpdate(msgPlayerId, score);
                        }
                    });
                }
                break;
                
            case GameMessage.MSG_PLAYER_DEAD:
                mainHandler.post(() -> {
                    if (messageCallback != null) {
                        messageCallback.onPlayerDead(msg.getPlayerId());
                    }
                });
                break;
                
            case GameMessage.MSG_GAME_END:
                boolean isWinner = msg.isWinner();
                int myScore = msg.getScore();
                int opponentScore = msg.getOpponentScore();
                String winnerId = (playerId != null && isWinner) ? playerId : (opponentId != null ? opponentId : "");
                
                mainHandler.post(() -> {
                    if (messageCallback != null) {
                        messageCallback.onGameEnd(winnerId, myScore, opponentScore, isWinner);
                    }
                });
                break;
        }
    }

    /**
     * 发送分数更新
     */
    public void sendScoreUpdate(int score) {
        if (!isConnected || out == null) {
            Log.w(TAG, "Socket未连接，无法发送分数");
            return;
        }
        
        new Thread(() -> {
            try {
                GameMessage msg = new GameMessage();
                msg.setType(GameMessage.MSG_SCORE_UPDATE);
                msg.setPlayerId(playerId);
                msg.setScore(score);
                
                synchronized (out) {
                    out.writeObject(msg);
                    out.flush();
                }
                Log.d(TAG, "已发送分数更新: " + score);
            } catch (IOException e) {
                Log.e(TAG, "发送分数失败: " + e.getMessage());
            }
        }).start();
    }

    /**
     * 发送玩家死亡消息
     */
    public void sendPlayerDead() {
        if (!isConnected || out == null) {
            Log.w(TAG, "Socket未连接，无法发送死亡消息");
            return;
        }
        
        new Thread(() -> {
            try {
                GameMessage msg = new GameMessage();
                msg.setType(GameMessage.MSG_PLAYER_DEAD);
                msg.setPlayerId(playerId);
                
                synchronized (out) {
                    out.writeObject(msg);
                    out.flush();
                }
                Log.d(TAG, "已发送死亡消息");
            } catch (IOException e) {
                Log.e(TAG, "发送死亡消息失败: " + e.getMessage());
            }
        }).start();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        shouldReconnect = false; // 停止重连尝试
        
        if (!isConnected) {
            return;
        }
        
        new Thread(() -> {
            try {
                isConnected = false;
                
                GameMessage msg = new GameMessage();
                msg.setType(GameMessage.MSG_DISCONNECT);
                msg.setPlayerId(playerId);
                
                if (out != null) {
                    synchronized (out) {
                        out.writeObject(msg);
                        out.flush();
                    }
                }
                
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) socket.close();
                
                Log.d(TAG, "已断开连接");
            } catch (IOException e) {
                Log.e(TAG, "断开连接失败: " + e.getMessage());
            }
        }).start();
    }

    private void handleError(String message) {
        isConnected = false;
        mainHandler.post(() -> {
            if (messageCallback != null) {
                messageCallback.onError(message);
            }
        });
    }

    private void handleDisconnected() {
        isConnected = false;
        mainHandler.post(() -> {
            if (messageCallback != null) {
                messageCallback.onDisconnected();
            }
        });
        
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            Log.e(TAG, "关闭资源失败: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getOpponentId() {
        return opponentId;
    }
}
