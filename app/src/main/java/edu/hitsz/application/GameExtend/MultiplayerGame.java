package edu.hitsz.application.GameExtend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.application.Game;
import edu.hitsz.mysocket.SocketClient;
import edu.hitsz.mysocket.GameMessage;

/**
 * 多人游戏模式 - 支持Socket联机对战
 */
public class MultiplayerGame extends NormalGame {
    private static final String TAG = "MultiplayerGame";

    private SocketClient socketClient;
    private String opponentId;
    private int opponentScore = 0;
    private boolean opponentDead = false;
    private int lastSentScore = -1;
    private boolean heroDeadSent = false;
    private boolean matchEnded = false;

    public MultiplayerGame(Context context, SocketClient socketClient, String opponentId) {
        super(context);
        this.socketClient = socketClient;
        this.opponentId = opponentId;
        
        Log.d(TAG, "MultiplayerGame 初始化，对手ID: " + opponentId);
    }

    @Override
    public void action() {
        // 调用父类的action方法
        super.action();

        // 检查分数是否变化，如果变化则发送给服务器
        if (socketClient != null && socketClient.isConnected()) {
            int currentScore = getScore();
            if (currentScore != lastSentScore) {
                lastSentScore = currentScore;
                socketClient.sendScoreUpdate(currentScore);
                Log.d(TAG, "发送分数更新: " + currentScore);
            }

            // 检查英雄是否死亡
            if (getHeroAircraft() != null && getHeroAircraft().getHp() <= 0 && !heroDeadSent) {
                heroDeadSent = true;
                socketClient.sendPlayerDead();
                Log.d(TAG, "发送玩家死亡消息");
            }

            // 检查游戏是否结束
            if (isGameOver() && !opponentDead) {
                // 等待对手也死亡
                // 这会在SocketClient的onGameEnd回调中处理
            }
        }
    }

    @Override
    protected void drawGame(Canvas canvas) {
        // 调用父类的drawGame方法绘制游戏画面
        super.drawGame(canvas);

        // 绘制对手分数
        if (canvas != null) {
            drawOpponentScore(canvas);
            drawWaitingOverlayIfNeeded(canvas);
        }
    }

    @Override
    protected boolean shouldShowGameOverDialog() {
        // 联机模式由服务器统一下发结算消息，不使用单人弹窗
        return false;
    }

    /**
     * 绘制对手分数
     */
    private void drawOpponentScore(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xFFFFFFFF);
        paint.setTextSize(28);
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        int x = 280;
        int y = 50;

        String opponentInfo = "对手分数: " + opponentScore;
        if (opponentDead) {
            opponentInfo = "对手: 已死亡";
        }

        canvas.drawText(opponentInfo, x, y, paint);
    }

    /**
     * 本地死亡后展示等待界面，直到服务端统一发送结算。
     */
    private void drawWaitingOverlayIfNeeded(Canvas canvas) {
        if (!heroDeadSent || matchEnded) {
            return;
        }

        Paint maskPaint = new Paint();
        maskPaint.setColor(0xAA000000);
        canvas.drawRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, maskPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(42);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        float centerX = Game.SCREEN_WIDTH / 2.0f;
        float centerY = Game.SCREEN_HEIGHT / 2.0f;
        canvas.drawText("你已失败", centerX, centerY - 20, textPaint);

        textPaint.setTextSize(30);
        canvas.drawText("正在等待对手结束游戏...", centerX, centerY + 40, textPaint);
    }

    /**
     * 更新对手分数
     */
    public void updateOpponentScore(int score) {
        this.opponentScore = score;
        Log.d(TAG, "对手分数已更新: " + score);
    }

    /**
     * 标记对手已死亡
     */
    public void setOpponentDead(boolean dead) {
        this.opponentDead = dead;
        Log.d(TAG, "对手死亡状态: " + dead);
    }

    /**
     * 标记本局联机对战已经结束（已收到统一结算）。
     */
    public void setMatchEnded(boolean ended) {
        this.matchEnded = ended;
    }

    /**
     * 获取对手分数
     */
    public int getOpponentScore() {
        return opponentScore;
    }

    /**
     * 获取对手ID
     */
    public String getOpponentId() {
        return opponentId;
    }

    /**
     * 获取Socket客户端
     */
    public SocketClient getSocketClient() {
        return socketClient;
    }
}
