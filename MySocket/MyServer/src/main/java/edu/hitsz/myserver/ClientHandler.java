package edu.hitsz.myserver;

import edu.hitsz.mysocket.GameMessage;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * 处理每个客户端连接的处理器类
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String playerId;
    private GameServer gameServer;
    private GameMatch currentMatch;
    private boolean isConnected;

    public ClientHandler(Socket socket, GameServer gameServer) {
        this.socket = socket;
        this.gameServer = gameServer;
        this.playerId = "Player_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000);
        this.isConnected = true;
        
        try {
            // 注意：必须先创建输出流，再创建输入流，避免握手死锁
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("创建流失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("客户端 " + playerId + " 已连接: " + socket.getInetAddress());
            
            // 发送欢迎消息和玩家ID
            GameMessage welcomeMsg = new GameMessage();
            welcomeMsg.setType("WELCOME");
            welcomeMsg.setPlayerId(playerId);
            welcomeMsg.setMessage("连接成功，您的ID是: " + playerId);
            sendMessage(welcomeMsg);
            
            // 添加到等待队列
            gameServer.addToWaitingQueue(this);
            
            // 监听客户端消息
            while (isConnected) {
                try {
                    GameMessage msg = (GameMessage) in.readObject();
                    if (msg == null) break;
                    
                    System.out.println("收到消息从 " + playerId + ": " + msg);
                    
                    handleMessage(msg);
                } catch (EOFException | SocketException e) {
                    System.out.println("客户端 " + playerId + " 断开连接");
                    isConnected = false;
                } catch (IOException e) {
                    if (isConnected) {
                        System.err.println("读取消息失败: " + e.getMessage());
                    }
                    isConnected = false;
                }
            }
        } catch (Exception e) {
            System.err.println("ClientHandler异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void handleMessage(GameMessage msg) {
        String type = msg.getType();
        
        switch (type) {
            case GameMessage.MSG_SCORE_UPDATE:
                // 处理分数更新
                if (currentMatch != null) {
                    if (currentMatch.getPlayer1Id().equals(playerId)) {
                        currentMatch.setPlayer1Score(msg.getScore());
                        // 发送给对手
                        GameMessage updateMsg = new GameMessage();
                        updateMsg.setType(GameMessage.MSG_SCORE_UPDATE);
                        updateMsg.setPlayerId(playerId);
                        updateMsg.setScore(msg.getScore());
                        currentMatch.getPlayer2Handler().sendMessage(updateMsg);
                    } else if (currentMatch.getPlayer2Id().equals(playerId)) {
                        currentMatch.setPlayer2Score(msg.getScore());
                        // 发送给对手
                        GameMessage updateMsg = new GameMessage();
                        updateMsg.setType(GameMessage.MSG_SCORE_UPDATE);
                        updateMsg.setPlayerId(playerId);
                        updateMsg.setScore(msg.getScore());
                        currentMatch.getPlayer1Handler().sendMessage(updateMsg);
                    }
                }
                break;
                
            case GameMessage.MSG_PLAYER_DEAD:
                // 处理玩家死亡
                if (currentMatch != null && !currentMatch.isGameEnded()) {
                    if (currentMatch.getPlayer1Id().equals(playerId)) {
                        currentMatch.setPlayer1Dead(true);
                    } else if (currentMatch.getPlayer2Id().equals(playerId)) {
                        currentMatch.setPlayer2Dead(true);
                    }
                    
                    // 如果两个玩家都死了，游戏结束
                    if (currentMatch.isBothPlayersDead()) {
                        endGame();
                    }
                }
                break;
                
            case GameMessage.MSG_DISCONNECT:
                // 处理断开连接
                isConnected = false;
                if (currentMatch != null) {
                    gameServer.endMatch(currentMatch);
                }
                break;
        }
    }

    private void endGame() {
        if (currentMatch == null || currentMatch.isGameEnded()) {
            return;
        }
        
        currentMatch.setGameEnded(true);
        
        String winnerId = currentMatch.getWinnerId();
        int player1Score = currentMatch.getPlayer1Score();
        int player2Score = currentMatch.getPlayer2Score();
        
        System.out.println("游戏结束 - 赢家: " + winnerId);
        System.out.println("玩家1分数: " + player1Score + ", 玩家2分数: " + player2Score);
        
        // 发送游戏结束消息给玩家1
        GameMessage endMsg1 = new GameMessage();
        endMsg1.setType(GameMessage.MSG_GAME_END);
        endMsg1.setPlayerId(currentMatch.getPlayer1Id());
        endMsg1.setScore(player1Score);
        endMsg1.setOpponentScore(player2Score);
        endMsg1.setOpponentId(currentMatch.getPlayer2Id());
        endMsg1.setWinner(currentMatch.getPlayer1Id().equals(winnerId));
        endMsg1.setBothDead(true);
        
        currentMatch.getPlayer1Handler().sendMessage(endMsg1);
        
        // 发送游戏结束消息给玩家2
        GameMessage endMsg2 = new GameMessage();
        endMsg2.setType(GameMessage.MSG_GAME_END);
        endMsg2.setPlayerId(currentMatch.getPlayer2Id());
        endMsg2.setScore(player2Score);
        endMsg2.setOpponentScore(player1Score);
        endMsg2.setOpponentId(currentMatch.getPlayer1Id());
        endMsg2.setWinner(currentMatch.getPlayer2Id().equals(winnerId));
        endMsg2.setBothDead(true);
        
        currentMatch.getPlayer2Handler().sendMessage(endMsg2);
        
        // 从服务器移除配对
        gameServer.removeMatch(currentMatch);
    }

    public void sendMessage(GameMessage msg) {
        try {
            if (out != null) {
                synchronized (out) {
                    out.writeObject(msg);
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("发送消息失败给 " + playerId + ": " + e.getMessage());
        }
    }

    public void setCurrentMatch(GameMatch match) {
        this.currentMatch = match;
    }

    public GameMatch getCurrentMatch() {
        return currentMatch;
    }

    public String getPlayerId() {
        return playerId;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void cleanup() {
        try {
            isConnected = false;
            
            // 从等待队列中移除
            gameServer.removeFromWaitingQueue(this);
            
            // 如果在配对中，结束配对
            if (currentMatch != null) {
                gameServer.endMatch(currentMatch);
            }
            
            // 关闭流和连接
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            
            System.out.println("客户端 " + playerId + " 资源已清理");
        } catch (IOException e) {
            System.err.println("清理资源失败: " + e.getMessage());
        }
    }
}
