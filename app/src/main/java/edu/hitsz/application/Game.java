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

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class Game extends BaseGame {

    // ===== 屏幕尺寸常量（替代原Main类） =====
    public static final int SCREEN_WIDTH = 512;
    public static final int SCREEN_HEIGHT = 768;

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

    // 音乐线程
    private MusicThread bgmThread;
    private MusicThread bossBgmThread;


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
                    // if (newEnemy instanceof Boss && DifficultySelection.isMusicOn()) {
                    //     stopAllMusic();
                    //     bossBgmThread = new MusicThread("src/videos/bgm_boss.wav", true);
                    //     bossBgmThread.start();
                    // }
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
                    enemyBullets.addAll(enemy.shoot());
                    for (BaseBullet bullet : enemyBullets) {
                        bombPublisher.addSubscriber((BombSubscriber) bullet);
                    }
                }
            } else if (enemy instanceof EliteEnemy || enemy instanceof ElitePlusEnemy) {
                // 所有精英敌机（包括ElitePlus）独立计时
                AbstractEnemy eliteEnemy = (AbstractEnemy) enemy;
                eliteEnemy.increaseShootTime(timeInterval);
                if (eliteEnemy.getShootTime() >= eliteShootCycle) {
                    eliteEnemy.resetShootTime();
                    enemyBullets.addAll(enemy.shoot());
                    for (BaseBullet bullet : enemyBullets) {
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
                            if (bossBgmThread != null) {
                                bossBgmThread.stopAndClose();
                                bossBgmThread = null;
                                playBgm();
                            }
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
        // 注销无效的敌机订阅者
        for (BaseBullet bullet : new ArrayList<>(enemyBullets)) {
            if (bullet.notValid()) {
                try {
                    bombPublisher.removeSubscriber((BombSubscriber) bullet);
                } catch (ClassCastException ignored) {}
            }
        }

        for (AbstractAircraft enemy : new ArrayList<>(enemyAircrafts)) {
            if (enemy.notValid()) {
                try {
                    bombPublisher.removeSubscriber((BombSubscriber) enemy);
                } catch (ClassCastException ignored) {}
            }
        }


        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
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
                canvas.drawBitmap(bgBitmap, 0, this.backGroundTop - screenHeight, null);
                canvas.drawBitmap(bgBitmap, 0, this.backGroundTop, null);
            }
            this.backGroundTop += 1;
            if (this.backGroundTop == screenHeight) {
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
        int x = 10;
        int y = 25;
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(22);
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        
        canvas.drawText("SCORE:" + this.score, x, y, paint);
        y = y + 20;
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
        if (bgmThread != null) {
            bgmThread.stopMusic();
            bgmThread = null;
        }
        if (bossBgmThread != null) {
            bossBgmThread.stopMusic();
            bossBgmThread = null;
        }
    }

    /**
     * 播放游戏音效
     */
    private void playSound(String filename) {
        // if (DifficultySelection.isMusicOn()) {
        //     new MusicThread(filename).start();
        // }
    }

    /**
     * 播放背景音乐
     */
    private synchronized void playBgm() {
        // 暂时注释掉背景音乐播放
        // if (!DifficultySelection.isMusicOn()) {
        //     return;
        // }
        // if (bgmThread != null) {
        //     bgmThread.stopAndClose();
        // }
        // bgmThread = new MusicThread("src/videos/bgm.wav", true);
        // bgmThread.start();
    }

    /**
     * 播放Boss音乐
     */
    private synchronized void playBossBgm() {
        // if (!DifficultySelection.isMusicOn()) {
        //     return;
        // }
        // 暂时注释掉Boss音乐播放
        // // 停止普通背景音乐
        // if (bgmThread != null) {
        //     bgmThread.stopAndClose();
        //     bgmThread = null;
        // }
        // // 播放Boss音乐
        // if (bossBgmThread != null) {
        //     bossBgmThread.stopAndClose();
        // }
        // bossBgmThread = new MusicThread("src/videos/bgm_boss.wav", true);
        // bossBgmThread.start();
    }


}
