package edu.hitsz.application;

import android.content.Context;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.observer.BombPublisher;
import edu.hitsz.observer.BombSubscriber;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBlood;
import edu.hitsz.prop.PropBomb;
import edu.hitsz.prop.PropBullet;
import edu.hitsz.scorerecord.*;

import java.util.Date;

import edu.hitsz.aircraft.factory.*;
import edu.hitsz.prop.factory.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.AudioAttributes;
import android.util.SparseIntArray;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class Game extends BaseGame {

    // ===== 屏幕尺寸常量（替代原Main类） =====
    public static int SCREEN_WIDTH = 512;
    public static int SCREEN_HEIGHT = 768;

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    protected final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;

    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 当前时刻
     */
    private int time = 0;


    // 难度相关参数（子类会调整这些）
    protected int heroShootCycle; // 英雄射击周期
    protected int eliteShootCycle; // 精英射击周期
    protected int bossShootCycle; // Boss射击周期
    protected int cycleDuration; // 敌机生成周期
    protected int heroInitialHp;

    protected int heroShootTime = 0;
    protected int eliteShootTime = 0;
    protected int bossShootTime = 0;
    protected int cycleTime = 0;
    protected String difficulty ;

    /**
     * BombPublisher 观察者模式
     */
    private BombPublisher bombPublisher = new BombPublisher();

    /**
     * 游戏结束标志
     */
    private boolean gameOverFlag = false;

    // 敌机生成器
    protected final EnemyGenerator enemyGenerator = new EnemyGenerator();

    // Android 音频播放器：MediaPlayer用于BGM，SoundPool用于短音效
    private MediaPlayer bgmPlayer;
    private MediaPlayer bossBgmPlayer;
    private SoundPool soundPool;
    private final SparseIntArray soundIdMap = new SparseIntArray();
    private final Context appContext;


    public Game(Context context) {
        super(context);
        
        try {
            System.out.println("Game 构造函数开始...");
            
            enemyAircrafts = new LinkedList<>();
            heroBullets = new LinkedList<>();
            enemyBullets = new LinkedList<>();
            props = new LinkedList<>();

            /**
             * Scheduled 线程池，用于定时任务调度
             * 使用简单的 ThreadFactory 实现（兼容 Android）
             */
            final int[] threadCount = {0};
            ThreadFactory namedThreadFactory = r -> {
                Thread t = new Thread(r);
                t.setName("game-action-" + (++threadCount[0]));
                t.setDaemon(true);
                return t;
            };
            this.executorService = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
            System.out.println("线程池创建成功");

            // 初始化难度参数（模板方法：子类实现）
            System.out.println("初始化难度参数...");
            initDifficultyParams();
            System.out.println("难度参数初始化完成: " + difficulty);

            System.out.println("获取英雄机单例...");
            heroAircraft = HeroAircraftSingleton.getInstance();
            if (heroAircraft != null) {
                System.out.println("英雄机单例获取成功");
                // 设置英雄初始血量
                heroAircraft.setHp(heroInitialHp);
                System.out.println("英雄机初始血量设置: " + heroInitialHp);
            } else {
                System.err.println("英雄机单例为null！");
            }

            System.out.println("Game 构造函数完成");
            // 保存应用级 Context 并初始化短音效池
            this.appContext = context.getApplicationContext();
            initSoundPool();
            // 启动普通背景音乐
            playBgm();
            
        } catch (Exception e) {
            System.err.println("Game 构造函数异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Game 初始化失败", e);
        }
    }

    // === 抽象方法：子类实现难度差异 ===
    protected abstract void initDifficultyParams(); // 初始化难度参数


    protected abstract AbstractEnemy generateEnemyLogic(int enemyCount, int score, int timeInterval); // 敌机生成逻辑（可调整概率等）

    // === 模板方法：游戏主流程 ===
    public final void action() {
        System.out.println("action执行");
        try {
            // 检查基本条件
            if (heroAircraft == null || gameOverFlag) {
                return;
            }

            time += timeInterval;

            if (timeCountAndNewCycleJudge()) {
                AbstractEnemy newEnemy = generateEnemyLogic(enemyAircrafts.size(), score, timeInterval);
                System.out.println("newEnemy = " + newEnemy);
                if (newEnemy != null) {
                    System.out.println("enemy image = " + newEnemy.getImage());
                    enemyAircrafts.add(newEnemy);
                    bombPublisher.addSubscriber((BombSubscriber) newEnemy);
                    if (newEnemy instanceof Boss) {
                        // 切换到Boss背景音乐
                        playBossBgm();
                    }
                }
            }
            // 敌机射出子弹
            enemyShootAction();

            //单独控制英雄机出子弹
            heroShootAction();

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 英雄机碰撞道具
            if (heroAircraft != null) {
                for (AbstractProp prop : props) {
                    prop.forward();
                    if (!prop.notValid() && heroAircraft.crash(prop)) {
                        // 播放道具音效
                        // if (DifficultySelection.isMusicOn()) {
                        //     new MusicThread("src/videos/get_supply.wav").start();
                        // }
                        prop.effect(heroAircraft);
                    }
                }
            }
            // 处理被炸弹道具击毁的敌机的加分
            processBombKilledEnemies();

            // 后处理
            postProcessAction();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft != null && heroAircraft.getHp() <= 0) {
                // 游戏结束
                gameOverFlag = true;
                System.out.println("Game Over!");

                // 停止所有音乐并播放游戏结束音效
                stopAllMusic();
                // if (DifficultySelection.isMusicOn()) {
                //     new MusicThread("src/videos/game_over.wav").start();
                // }
            }
        } catch (Exception e) {
            System.err.println("游戏逻辑出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void enemyShootAction() {
        bossShootTime += timeInterval;  // Boss射击计时器
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy instanceof Boss) {
                // Boss射击控制
                if (bossShootTime >= bossShootCycle) {
                    bossShootTime = 0;
                    List<BaseBullet> newBullets = enemy.shoot();
                    enemyBullets.addAll(newBullets);
                    for (BaseBullet bullet : newBullets) {
                        bombPublisher.addSubscriber((BombSubscriber) bullet);
                    }
                }
            } else if (enemy instanceof EliteEnemy || enemy instanceof ElitePlusEnemy) {
                // 所有精英敌机（包括ElitePlus）独立计时
                AbstractEnemy eliteEnemy = (AbstractEnemy) enemy;
                eliteEnemy.increaseShootTime(timeInterval);
                if (eliteEnemy.getShootTime() >= eliteShootCycle) {
                    eliteEnemy.resetShootTime();
                    List<BaseBullet> newBullets = enemy.shoot();
                    enemyBullets.addAll(newBullets);
                    for (BaseBullet bullet : newBullets) {
                        bombPublisher.addSubscriber((BombSubscriber) bullet);
                    }
                }
            }
            // 普通敌机不射击
        }
    }

    private void heroShootAction() {
        // === 英雄机独立射击控制 ===
        heroShootTime += timeInterval;  // 每帧累加
        if (heroShootTime >= heroShootCycle) {
            heroShootTime %= heroShootCycle;
            heroBullets.addAll(heroAircraft.shoot());
                    // 播放射击音效
                    playSound("src/videos/bullet.wav");
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (bullet.crash(heroAircraft) || heroAircraft.crash(bullet)) {
                // 英雄机受伤
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
                // 如果英雄机死亡，停止所有背景音乐并播放死亡音效
                if (heroAircraft.getHp() <= 0) {
                    // 停止所有背景音乐并播放死亡音效
                    stopAllMusic();
                    // if (DifficultySelection.isMusicOn()) {
                    //     new MusicThread("src/videos/game_over.wav").start();
                    // }
                }
            }
        }
        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    bombPublisher.removeSubscriber((BombSubscriber) enemyAircraft);
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // 播放击中音效
                        // if (DifficultySelection.isMusicOn()) {
                        //     new MusicThread("src/videos/bullet_hit.wav").start();
                        // }
                        score += 10;

                        int propX = enemyAircraft.getLocationX();
                        int propY = enemyAircraft.getLocationY();
                        int speedY = 5;
                        PropGenerator propGenerator = new PropGenerator(bombPublisher);

                        // ====== 新增逻辑：Boss掉落多个道具 ======
                        if (enemyAircraft instanceof Boss) {
                            // Boss被击毁，切换回普通背景音乐
                            try {
                                if (bossBgmPlayer != null) {
                                    if (bossBgmPlayer.isPlaying()) bossBgmPlayer.stop();
                                    bossBgmPlayer.release();
                                    bossBgmPlayer = null;
                                }
                            } catch (Exception ignored) {
                            }
                            playBgm();
                            int propCount = (int) (Math.random() * 3) + 1; // 随机掉落1~3个
                            System.out.println("Boss 被击毁！掉落 " + propCount + " 个道具！");
                            for (int i = 0; i < propCount; i++) {
                                // 让道具在Boss附近位置随机分布
                                int offsetX = (int) (Math.random() * 60 - 30);
                                int offsetY = (int) (Math.random() * 30 - 15);
                                AbstractProp prop = propGenerator.generateProp(propX + offsetX, propY + offsetY, speedY);
                                if (prop != null) {
                                    props.add(prop);
                                }
                            }
                        }
                        // ====== 保留原逻辑：精英敌机有概率掉落 ======
                        else if (enemyAircraft instanceof EliteEnemy && Math.random() < 0.6) {
                            AbstractProp prop = propGenerator.generateProp(propX, propY, speedY);
                            if (prop != null) {
                                props.add(prop);
                            }
                        }
                        else if (enemyAircraft instanceof ElitePlusEnemy && Math.random() < 0.6) {
                            score += 10;
                            AbstractProp prop = propGenerator.generateProp(propX, propY, speedY);
                            if (prop != null) {
                                props.add(prop);
                            }
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                    // 停止所有背景音乐并播放死亡音效
                    stopAllMusic();
                    playSound("src/videos/game_over.wav");
                }
            }
        }
    }


    /**
     * 处理被炸弹杀死的敌机：加分、注销订阅者、（不生成道具）
     * 这个方法应在碰撞检测之后、postProcessAction() 之前调用
     */
    private void processBombKilledEnemies() {
        // 注意：这里我们遍历 enemyAircrafts，不立即删除元素（删除由 postProcessAction 统一处理）
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy.notValid() && enemy instanceof AbstractEnemy) {
                AbstractEnemy ae = (AbstractEnemy) enemy;
                if (ae.isKilledByBomb()) {
                    // 1) 按敌机类型加分（按你想要的分数规则）
                    if (ae instanceof ElitePlusEnemy) {
                        score += 20;
                    } else if (ae instanceof EliteEnemy) {
                        score += 10;
                    } else {
                        score += 10;
                    }

                    // 2) 播放击毁音效
                    // if (DifficultySelection.isMusicOn()) {
                    //     new MusicThread("src/videos/bullet_hit.wav").start();
                    // }

                    // 3) 从炸弹发布者中注销该敌机（避免残留）
                    try {
                        bombPublisher.removeSubscriber((BombSubscriber) ae);
                    } catch (ClassCastException ignored) {
                        // 如果不是 BombSubscriber 则忽略
                    }

                    // 4) 确保该敌机不会生成道具（无需操作，因为我们只在这里处理，通过不走原来的掉落逻辑）
                    // （原来掉落逻辑在 crashCheckAction() 的子弹分支，这里我们主动不调用 PropGenerator）
                }
            }
        }
    }





    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        // 使用迭代器统一处理无效对象的移除和订阅者的注销，减少内存分配
        Iterator<BaseBullet> enemyBulletIterator = enemyBullets.iterator();
        while (enemyBulletIterator.hasNext()) {
            BaseBullet bullet = enemyBulletIterator.next();
            if (bullet.notValid()) {
                if (bullet instanceof BombSubscriber) {
                    bombPublisher.removeSubscriber((BombSubscriber) bullet);
                }
                enemyBulletIterator.remove();
            }
        }

        Iterator<AbstractAircraft> enemyAircraftIterator = enemyAircrafts.iterator();
        while (enemyAircraftIterator.hasNext()) {
            AbstractAircraft enemy = enemyAircraftIterator.next();
            if (enemy.notValid()) {
                if (enemy instanceof BombSubscriber) {
                    bombPublisher.removeSubscriber((BombSubscriber) enemy);
                }
                enemyAircraftIterator.remove();
            }
        }

        heroBullets.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Android绘制
    //***********************

    /**
     * 实现BaseGame的drawGame方法
     * 绘制游戏画面
     *
     * @param canvas Android画布
     */
    @Override
    protected void drawGame(Canvas canvas) {
        try {
            // 绘制背景,图片滚动
            android.graphics.Bitmap bgBitmap = ImageManager.BACKGROUND_IMAGE;
            if (bgBitmap != null) {
                // 使用Rect进行拉伸绘制，适配屏幕尺寸
                android.graphics.Rect src = new android.graphics.Rect(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());
                
                // 绘制两张背景图片实现循环滚动
                android.graphics.Rect dst1 = new android.graphics.Rect(0, this.backGroundTop - screenHeight, screenWidth, this.backGroundTop);
                android.graphics.Rect dst2 = new android.graphics.Rect(0, this.backGroundTop, screenWidth, this.backGroundTop + screenHeight);
                
                canvas.drawBitmap(bgBitmap, src, dst1, null);
                canvas.drawBitmap(bgBitmap, src, dst2, null);
            }
            this.backGroundTop += 2; // 适当加快背景滚动速度，更丝滑
            if (this.backGroundTop >= screenHeight) {
                this.backGroundTop = 0;
            }

            // 先绘制子弹，后绘制飞机
            // 这样子弹显示在飞机的下层
            drawImageWithPositionRevised(canvas, enemyBullets);
            drawImageWithPositionRevised(canvas, heroBullets);

            drawImageWithPositionRevised(canvas, enemyAircrafts);
            drawImageWithPositionRevised(canvas, props);

            // 绘制英雄机
            if (heroAircraft != null) {
                android.graphics.Bitmap heroBitmap = ImageManager.HERO_IMAGE;
                if (heroBitmap != null) {
                    canvas.drawBitmap(heroBitmap, 
                        heroAircraft.getLocationX() - heroBitmap.getWidth() / 2,
                        heroAircraft.getLocationY() - heroBitmap.getHeight() / 2, null);
                }
            }

            // 绘制得分和生命值
            drawScoreAndLife(canvas);
        } catch (Exception e) {
            System.err.println("drawGame 异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawImageWithPositionRevised(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            android.graphics.Bitmap image = object.getImage();
            if (image != null) {
                canvas.drawBitmap(image,
                    object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
            }
        }
    }

    private void drawScoreAndLife(Canvas canvas) {
        int x = 20;
        int y = 50;
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(40);
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        
        canvas.drawText("SCORE:" + this.score, x, y, paint);
        y = y + 50;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, paint);
    }

    //***********************
    //      游戏更新逻辑
    //***********************

    /**
     * 实现BaseGame的update方法
     * 更新游戏逻辑（敌机、子弹、碰撞等）
     */
    @Override
    protected void update() {
        // 执行游戏主要逻辑（每帧同步调用）
        if (!gameOverFlag && heroAircraft != null) {
            try {
                action();
            } catch (Exception e) {
                System.err.println("游戏逻辑执行出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 实现BaseGame的initializeGameParams方法
     * 初始化游戏参数（由子类实现具体难度参数）
     */
    @Override
    protected void initializeGameParams() {
        // 更新英雄机位置以适应新屏幕尺寸
        HeroAircraftSingleton.resetInstancePosition(screenWidth, screenHeight);
        
        // 调用抽象方法，由子类实现难度相关参数初始化
        initDifficultyParams();
    }

    /**
     * 实现BaseGame的onTouchEventHandle方法
     * 处理Android触屏输入事件，控制英雄机移动
     */
    @Override
    protected void onTouchEventHandle(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            // 获取触屏坐标
            float x = event.getX();
            float y = event.getY();
            
            // 检查边界，防止超出屏幕范围
            if (x >= 0 && x <= screenWidth && y >= 0 && y <= screenHeight) {
                // 将触屏坐标传递给英雄机
                if (heroAircraft != null) {
                    heroAircraft.setLocation((int) x, (int) y);
                }
            }
        }
    }

    /**
     * 停止所有音乐
     */
    private void stopAllMusic() {
        try {
            if (bgmPlayer != null) {
                if (bgmPlayer.isPlaying()) bgmPlayer.stop();
                bgmPlayer.release();
                bgmPlayer = null;
            }
        } catch (Exception ignored) {
        }
        try {
            if (bossBgmPlayer != null) {
                if (bossBgmPlayer.isPlaying()) bossBgmPlayer.stop();
                bossBgmPlayer.release();
                bossBgmPlayer = null;
            }
        } catch (Exception ignored) {
        }
        try {
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }
            soundIdMap.clear();
        } catch (Exception ignored) {
        }
    }

    /**
     * 播放游戏音效
     */
    private void playSound(String filename) {
        // 简单映射：兼容原先传入的 "src/videos/*.wav" 路径，也支持直接传入资源关键字
        if (filename == null || filename.isEmpty()) return;
        String key = filename;
        if (filename.contains("bullet_hit") || filename.contains("bullet_hit.wav")) {
            key = "bullet_hit";
        } else if (filename.contains("bullet.wav") || filename.contains("bullet_shoot")) {
            key = "bullet_shoot";
        } else if (filename.contains("boom") || filename.contains("explosion")) {
            key = "explosion_boom";
        } else if (filename.contains("game_over")) {
            key = "game_over";
        } else if (filename.contains("get_supply")) {
            key = "get_supply";
        }

        // 通过资源名加载 short effect（期望位于 res/raw 下，例如 res/raw/bullet_shoot.wav）
        int resId = getResIdByName(key);
        if (resId != 0) {
            playShortEffect(resId);
        }
    }

    /**
     * 播放背景音乐
     */
    private synchronized void playBgm() {
        try {
            // stop boss bgm if playing
            if (bossBgmPlayer != null) {
                if (bossBgmPlayer.isPlaying()) bossBgmPlayer.stop();
                bossBgmPlayer.release();
                bossBgmPlayer = null;
            }
            if (bgmPlayer != null) {
                if (bgmPlayer.isPlaying()) return; // already playing
                bgmPlayer.release();
                bgmPlayer = null;
            }
            int resId = getResIdByName("bgm");
            if (resId != 0) {
                bgmPlayer = MediaPlayer.create(appContext, resId);
                if (bgmPlayer != null) {
                    bgmPlayer.setLooping(true);
                    bgmPlayer.start();
                }
            }
        } catch (Exception e) {
            System.err.println("playBgm error: " + e.getMessage());
        }
    }

    /**
     * 播放Boss音乐
     */
    private synchronized void playBossBgm() {
        try {
            // stop normal bgm
            if (bgmPlayer != null) {
                if (bgmPlayer.isPlaying()) bgmPlayer.stop();
                bgmPlayer.release();
                bgmPlayer = null;
            }
            if (bossBgmPlayer != null) {
                if (bossBgmPlayer.isPlaying()) return;
                bossBgmPlayer.release();
                bossBgmPlayer = null;
            }
            int resId = getResIdByName("bgm_boss");
            if (resId != 0) {
                bossBgmPlayer = MediaPlayer.create(appContext, resId);
                if (bossBgmPlayer != null) {
                    bossBgmPlayer.setLooping(true);
                    bossBgmPlayer.start();
                }
            }
        } catch (Exception e) {
            System.err.println("playBossBgm error: " + e.getMessage());
        }
    }

    /**
     * 初始化SoundPool并预加载常用短音效（如果资源存在）
     */
    private void initSoundPool() {
        try {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(attrs)
                    .build();

            // 预加载常见短音效，资源名期望位于 res/raw
            String[] names = new String[]{"bullet_shoot", "bullet_hit", "explosion_boom", "game_over", "get_supply"};
            for (String n : names) {
                int resId = getResIdByName(n);
                if (resId != 0) {
                    int soundId = soundPool.load(appContext, resId, 1);
                    soundIdMap.put(resId, soundId);
                }
            }
        } catch (Exception e) {
            System.err.println("initSoundPool error: " + e.getMessage());
        }
    }

    private int getResIdByName(String name) {
        if (name == null || name.isEmpty() || appContext == null) return 0;
        int resId = appContext.getResources().getIdentifier(name, "raw", appContext.getPackageName());
        return resId;
    }

    private void playShortEffect(int resId) {
        if (soundPool == null) {
            initSoundPool();
            if (soundPool == null) return;
        }
        try {
            int soundId = soundIdMap.get(resId, 0);
            if (soundId == 0) {
                soundId = soundPool.load(appContext, resId, 1);
                if (soundId != 0) soundIdMap.put(resId, soundId);
            }
            if (soundId != 0) {
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
            }
        } catch (Exception e) {
            System.err.println("playShortEffect error: " + e.getMessage());
        }
    }


}
