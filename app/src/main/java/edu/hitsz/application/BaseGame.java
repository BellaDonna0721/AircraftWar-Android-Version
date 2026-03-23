package edu.hitsz.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

/**
 * 游戏基类 - SurfaceView实现
 * 包含核心游戏循环和渲染逻辑
 */
public abstract class BaseGame extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {

    // ===== 核心成员 =====
    protected SurfaceHolder mSurfaceHolder;
    protected Canvas mCanvas;
    protected Paint mPaint;
    protected boolean mbLoop = false;  // 绘制标志位
    private Thread mDrawThread;        // 绘制线程

    // 屏幕尺寸
    protected int screenWidth = 512;
    protected int screenHeight = 768;

    // ===== 游戏相关成员 =====
    // 这些字段会从原Game类迁移过来
    protected int backGroundTop = 0;
    protected int timeInterval = 40;
    protected int score = 0;
    protected int time = 0;

    public BaseGame(Context context) {
        super(context);

        // 初始化核心成员
        mPaint = new Paint();
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);

        // 设置触屏监听器 - Android触屏操作
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(android.view.View view, MotionEvent motionEvent) {
                // 触屏事件处理委托给子类实现
                onTouchEventHandle(motionEvent);
                return true;
            }
        });

        // 注意：游戏参数初始化由子类在其构造函数中负责
        // 这避免了在子类字段初始化完成前就调用的问题
    }

    /**
     * 触屏事件处理（由子类实现具体逻辑）
     */
    protected abstract void onTouchEventHandle(MotionEvent event);

    /**
     * 初始化游戏参数（由子类实现）
     */
    protected abstract void initializeGameParams();

    /**
     * Surface首次创建成功时调用
     * 加载图片，设置绘制标志位为true，启动游戏绘制线程
     */
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        System.out.println("Surface 创建成功");
        mbLoop = true;
        
        // 获取实际屏幕尺寸并更新
        screenWidth = getWidth();
        screenHeight = getHeight();
        Game.SCREEN_WIDTH = screenWidth;
        Game.SCREEN_HEIGHT = screenHeight;
        
        // 初始化游戏参数（需要子类实现，可能依赖于屏幕尺寸）
        initializeGameParams();
        
        // 启动游戏绘制线程
        try {
            mDrawThread = new Thread(this);
            mDrawThread.start();
            System.out.println("绘制线程启动成功");
        } catch (Exception e) {
            System.err.println("绘制线程启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Surface尺寸或格式发生变化时调用
     * 获取屏幕宽高，如屏幕旋转
     */
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        // 更新全局屏幕尺寸常量，以便飞行对象等逻辑使用
        Game.SCREEN_WIDTH = width;
        Game.SCREEN_HEIGHT = height;
    }

    /**
     * Surface被销毁前调用
     * 设置绘制标志位为false，终止游戏绘制
     */
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mbLoop = false;
        // 等待线程结束
        if (mDrawThread != null) {
            try {
                mDrawThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 实现Runnable的run()方法
     * 游戏主循环
     */
    @Override
    public void run() {
        while (mbLoop) {
            // 先更新逻辑，再进行绘制
            update();
            drawFrame();

            try {
                Thread.sleep(timeInterval);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 绘制单帧画面
     * 这个方法会在游戏主循环中被不断调用
     */
    protected void drawFrame() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas == null) {
                return;
            }

            // 清空屏幕，绘制背景
            mPaint.setColor(Color.BLACK);
            mCanvas.drawRect(0, 0, screenWidth, screenHeight, mPaint);

            // 调用游戏的绘制逻辑（由子类实现或调用来自Game的逻辑）
            drawGame(mCanvas);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 绘制游戏画面（由子类重写实现具体绘制逻辑）
     */
    protected abstract void drawGame(Canvas canvas);

    /**
     * 更新游戏逻辑（敌机、子弹、碰撞等）
     */
    protected abstract void update();

    /**
     * 启动游戏（由Activity调用）
     */
    public void startGame() {
        mbLoop = true;
    }

    /**
     * 停止游戏
     */
    public void stopGame() {
        mbLoop = false;
    }
}