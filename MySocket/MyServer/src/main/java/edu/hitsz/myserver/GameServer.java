package edu.hitsz.myserver;

import edu.hitsz.mysocket.GameMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏服务器主类，管理所有玩家连接和游戏配对
 */
public class GameServer {
    private static final int PORT = 9999;
    
    // 等待匹配的玩家队列
    private Queue<ClientHandler> waitingQueue = new LinkedList<>();
    
    // 已配对的游戏
    private Map<String, GameMatch> activeMatches = new ConcurrentHashMap<>();
    
    // 所有连接的客户端
    private Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();
    
    // 匹配计数器
    private int matchCounter = 0;

    public GameServer() {
        start();
    }

    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("游戏服务器启动，监听端口: " + PORT);
            
            // 启动配对线程
            new Thread(this::matchingLoop).start();
            
            while (true) {
                System.out.println("等待客户端连接...");
                Socket socket = serverSocket.accept();
                System.out.println("新的客户端已连接: " + socket.getInetAddress());
                
                // 为每个客户端创建处理器
                ClientHandler handler = new ClientHandler(socket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 配对线程，定期检查等待队列并配对玩家
     */
    private void matchingLoop() {
        while (true) {
            try {
                Thread.sleep(1000); // 每秒检查一次
                
                // 如果有至少两个玩家在等待，进行配对
                synchronized (waitingQueue) {
                    while (waitingQueue.size() >= 2) {
                        ClientHandler player1 = waitingQueue.poll();
                        ClientHandler player2 = waitingQueue.poll();
                        
                        if (player1 != null && player2 != null) {
                            matchPlayers(player1, player2);
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("配对线程被中断: " + e.getMessage());
            }
        }
    }

    /**
     * 配对两个玩家
     */
    private void matchPlayers(ClientHandler player1, ClientHandler player2) {
        String matchId = "MATCH_" + (++matchCounter) + "_" + System.currentTimeMillis();
        
        System.out.println("\n=== 配对成功 ===");
        System.out.println("玩家1: " + player1.getPlayerId());
        System.out.println("玩家2: " + player2.getPlayerId());
        System.out.println("匹配ID: " + matchId);
        System.out.println("================\n");
        
        // 创建游戏配对
        GameMatch match = new GameMatch(
            matchId,
            player1.getPlayerId(),
            player2.getPlayerId(),
            player1,
            player2
        );
        
        // 保存配对
        activeMatches.put(matchId, match);
        
        // 为两个玩家设置当前配对
        player1.setCurrentMatch(match);
        player2.setCurrentMatch(match);
        
        // 发送配对成功消息给玩家1
        GameMessage msg1 = new GameMessage();
        msg1.setType(GameMessage.MSG_MATCHED);
        msg1.setPlayerId(player1.getPlayerId());
        msg1.setOpponentId(player2.getPlayerId());
        msg1.setMessage("配对成功！对手是: " + player2.getPlayerId());
        player1.sendMessage(msg1);
        
        // 发送配对成功消息给玩家2
        GameMessage msg2 = new GameMessage();
        msg2.setType(GameMessage.MSG_MATCHED);
        msg2.setPlayerId(player2.getPlayerId());
        msg2.setOpponentId(player1.getPlayerId());
        msg2.setMessage("配对成功！对手是: " + player1.getPlayerId());
        player2.sendMessage(msg2);
    }

    /**
     * 添加玩家到等待队列
     */
    public void addToWaitingQueue(ClientHandler handler) {
        synchronized (waitingQueue) {
            waitingQueue.offer(handler);
            connectedClients.put(handler.getPlayerId(), handler);
            System.out.println("玩家 " + handler.getPlayerId() + " 加入等待队列，当前等待人数: " + waitingQueue.size());
        }
    }

    /**
     * 从等待队列中移除玩家
     */
    public void removeFromWaitingQueue(ClientHandler handler) {
        synchronized (waitingQueue) {
            waitingQueue.remove(handler);
            connectedClients.remove(handler.getPlayerId());
            System.out.println("玩家 " + handler.getPlayerId() + " 从等待队列移除，当前等待人数: " + waitingQueue.size());
        }
    }

    /**
     * 结束一场游戏配对
     */
    public void endMatch(GameMatch match) {
        if (match != null) {
            activeMatches.remove(match.getMatchId());
            System.out.println("配对 " + match.getMatchId() + " 已结束");
        }
    }

    /**
     * 移除配对
     */
    public void removeMatch(GameMatch match) {
        endMatch(match);
    }

    /**
     * 获取服务器统计信息
     */
    public void printStats() {
        System.out.println("\n=== 服务器统计 ===");
        System.out.println("等待配对的玩家: " + waitingQueue.size());
        System.out.println("活跃的游戏: " + activeMatches.size());
        System.out.println("总连接数: " + connectedClients.size());
        System.out.println("==================\n");
    }

    public static void main(String[] args) {
        new GameServer();
    }
}
