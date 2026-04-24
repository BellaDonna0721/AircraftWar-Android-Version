package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.application.GameManager;
import edu.hitsz.application.ImageManager;
import edu.hitsz.mysocket.SocketClient;
import edu.hitsz.mysocket.GameMessage;
import edu.hitsz.application.GameExtend.MultiplayerGame;

/**
 * 多人游戏Activity - 显示游戏画面并处理Socket消息
 */
public class GameActivityMultiplayer extends AppCompatActivity implements SocketClient.MessageCallback {
    private static final String TAG = "GameActivityMultiplayer";

    public static final String EXTRA_SOCKET_CLIENT = "socket_client";
    public static final String EXTRA_OPPONENT_ID = "opponent_id";
    public static final String EXTRA_PLAYER_ID = "player_id";

    private SocketClient socketClient;
    private String opponentId;
    private String playerId;
    private MultiplayerGame game;
    private boolean gameEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // 初始化图片资源
            Log.d(TAG, "初始化图片资源...");
            ImageManager.init(this);
            Log.d(TAG, "图片资源初始化完成");
            
            // 从Intent中获取对手ID和玩家ID
            Intent intent = getIntent();
            opponentId = intent.getStringExtra(EXTRA_OPPONENT_ID);
            playerId = intent.getStringExtra(EXTRA_PLAYER_ID);

            if (opponentId == null || playerId == null) {
                Log.e(TAG, "缺少必要参数：opponentId=" + opponentId + ", playerId=" + playerId);
                Toast.makeText(this, "缺少必要参数", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d(TAG, "游戏开始，玩家ID: " + playerId + "，对手ID: " + opponentId);

            // 从GameManager获取SocketClient
            socketClient = GameManager.getInstance().getSocketClient();
            if (socketClient == null) {
                Log.e(TAG, "Socket客户端为null");
                Toast.makeText(this, "Socket连接错误", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d(TAG, "Socket客户端获取成功，连接状态: " + socketClient.isConnected());

            // 注册Socket消息回调
            socketClient.setMessageCallback(this);
            Log.d(TAG, "Socket回调已注册");

            // 创建多人游戏
            Log.d(TAG, "创建MultiplayerGame...");
            game = new MultiplayerGame(this, socketClient, opponentId);
            Log.d(TAG, "MultiplayerGame创建成功");
            
            setContentView(game);
            Log.d(TAG, "ContentView已设置为game");
            
        } catch (Exception e) {
            Log.e(TAG, "GameActivityMultiplayer.onCreate异常: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "游戏初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onConnected(String playerId) {
        Log.d(TAG, "已连接到服务器");
    }

    @Override
    public void onMatched(String opponentId) {
        Log.d(TAG, "配对成功");
    }

    @Override
    public void onScoreUpdate(String playerId, int score) {
        Log.d(TAG, "分数更新: " + playerId + " = " + score);
    }

    @Override
    public void onOpponentScoreUpdate(String opponentId, int score) {
        Log.d(TAG, "对手分数更新: " + opponentId + " = " + score);
        if (game != null) {
            game.updateOpponentScore(score);
        }
    }

    @Override
    public void onPlayerDead(String playerId) {
        Log.d(TAG, "玩家死亡: " + playerId);
        if (game != null && !playerId.equals(this.playerId)) {
            game.setOpponentDead(true);
        }
    }

    @Override
    public void onGameEnd(String winnerId, int myScore, int opponentScore, boolean isWinner) {
        Log.d(TAG, "游戏结束，赢家: " + winnerId);
        
        gameEnded = true;
        
        String result = isWinner ? "胜利！" : "失败！";
        String message = result + "\n" +
                "你的分数: " + myScore + "\n" +
                "对手分数: " + opponentScore;

        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("游戏结束")
                    .setMessage(message)
                    .setPositiveButton("返回主菜单", (dialog, which) -> {
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "已断开连接");
        if (!gameEnded) {
            runOnUiThread(() -> {
                Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "错误: " + message);
        if (!gameEnded) {
            runOnUiThread(() -> {
                Toast.makeText(this, "连接错误: " + message, Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameManager.getInstance().clear();
    }
}
