package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;
import edu.hitsz.application.GameManager;
import edu.hitsz.mysocket.SocketClient;
import edu.hitsz.mysocket.GameMessage;

/**
 * 联机对战Activity - 等待配对和管理游戏
 */
public class MultiplayerGameActivity extends AppCompatActivity implements SocketClient.MessageCallback {
    private static final String TAG = "MultiplayerGameActivity";

    private SocketClient socketClient;
    private TextView tvStatus;
    private ProgressBar progressBar;
    private Button btnCancel;
    private String opponentId;
    private String playerId;
    private boolean isMatched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game);

        tvStatus = findViewById(R.id.tv_status);
        progressBar = findViewById(R.id.progress_bar);
        btnCancel = findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(v -> {
            if (socketClient != null) {
                socketClient.disconnect();
            }
            finish();
        });

        // 提示用户可配置的服务器地址
        tvStatus.setText("正在连接服务器...\n\n(如果连接失败，请检查)\n" +
                "1. 模拟器: SocketClient.java中SERVER_HOST应为 10.0.2.2\n" +
                "2. 真实设备: SERVER_HOST应为服务器IP地址\n" +
                "3. 服务器必须在端口9999运行");

        // 初始化Socket客户端
        socketClient = new SocketClient(this);
        GameManager.getInstance().setSocketClient(socketClient);
        socketClient.connect();
    }

    @Override
    public void onConnected(String playerId) {
        Log.d(TAG, "已连接到服务器，玩家ID: " + playerId);
        this.playerId = playerId;
        GameManager.getInstance().setPlayerId(playerId);
        runOnUiThread(() -> {
            tvStatus.setText("已连接到服务器，正在等待配对...\n玩家ID: " + playerId);
        });
    }

    @Override
    public void onMatched(String opponentId) {
        Log.d(TAG, "配对成功，对手: " + opponentId);
        this.opponentId = opponentId;
        this.isMatched = true;
        GameManager.getInstance().setOpponentId(opponentId);

        runOnUiThread(() -> {
            tvStatus.setText("配对成功！\n对手: " + opponentId + "\n准备开始游戏...");
            Toast.makeText(this, "配对成功，准备开始游戏", Toast.LENGTH_SHORT).show();

            // 延迟2秒后启动游戏
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                startMultiplayerGame(opponentId);
            }, 2000);
        });
    }

    @Override
    public void onScoreUpdate(String playerId, int score) {
        Log.d(TAG, "分数更新: " + playerId + " = " + score);
    }

    @Override
    public void onOpponentScoreUpdate(String opponentId, int score) {
        Log.d(TAG, "对手分数更新: " + opponentId + " = " + score);
    }

    @Override
    public void onPlayerDead(String playerId) {
        Log.d(TAG, "玩家死亡: " + playerId);
    }

    @Override
    public void onGameEnd(String winnerId, int myScore, int opponentScore, boolean isWinner) {
        Log.d(TAG, "游戏结束，赢家: " + winnerId + ", 我的分数: " + myScore + ", 对手分数: " + opponentScore);
        
        runOnUiThread(() -> {
            String result = isWinner ? "胜利！" : "失败！";
            String message = result + "\n" +
                    "你的分数: " + myScore + "\n" +
                    "对手分数: " + opponentScore;
            
            tvStatus.setText(message);
            progressBar.setVisibility(View.GONE);
            btnCancel.setText("返回主菜单");
            
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "已断开连接");
        runOnUiThread(() -> {
            tvStatus.setText("已断开连接");
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "错误: " + message);
        runOnUiThread(() -> {
            tvStatus.setText("连接错误: " + message);
            progressBar.setVisibility(View.GONE);
            btnCancel.setText("返回");
            Toast.makeText(this, "连接错误: " + message, Toast.LENGTH_SHORT).show();
        });
    }

    private void startMultiplayerGame(String opponentId) {
        // 启动游戏Activity，并传递对手ID
        Intent intent = new Intent(this, GameActivityMultiplayer.class);
        intent.putExtra(GameActivityMultiplayer.EXTRA_OPPONENT_ID, opponentId);
        intent.putExtra(GameActivityMultiplayer.EXTRA_PLAYER_ID, playerId);
        startActivity(intent);
        Log.d(TAG, "启动游戏: " + opponentId);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isMatched && socketClient != null && socketClient.isConnected()) {
            socketClient.disconnect();
        }
    }
}
