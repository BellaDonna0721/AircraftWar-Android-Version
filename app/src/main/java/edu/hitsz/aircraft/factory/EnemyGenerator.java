package edu.hitsz.aircraft.factory;

import edu.hitsz.aircraft.AbstractEnemy;

/**
 * 敌机生成管理器
 * 封装敌机类型选择与生成概率逻辑
 *
 * 支持：
 * 1. 普通敌机（MobEnemy）
 * 2. 精英敌机（EliteEnemy）
 * 3. 超级精英敌机（ElitePlusEnemy）—— 每隔elitePlusInterval ms有elitePlusProb几率生成
 * 4. Boss敌机（Boss）—— 当累积bossScoreInterval分时生成
 *
 * 新增：支持难度配置，通过setter方法调整参数，实现模板方法模式下的难度差异
 *       - 包括敌机速度 (speedY) 和血量 (hp) 的 setter
 */
public class EnemyGenerator {
    private int enemyMaxNumber = 6; // 屏幕上允许的最大敌机数量

    // 敌机生成概率（mobProb + eliteProb 应接近1，生成时优先mob）
    private double mobProb = 0.7; // 普通敌机概率
    private double eliteProb = 0.3; // 精英敌机概率（备用，可用于扩展）

    // 超级精英敌机参数
    private int elitePlusInterval = 300; // 尝试生成间隔(ms)
    private double elitePlusProb = 0.8; // 生成概率

    // Boss参数
    private int bossScoreInterval = 150; // Boss触发分数间隔
    private int lastBossScore = 0; // 上次Boss生成分数

    // 新增：敌机速度和血量参数（默认值基于你的硬编码）
    private int mobSpeedY = 10;      // 普通敌机速度
    private int mobHp = 30;          // 普通敌机血量
    private int eliteSpeedY = 8;     // 精英敌机速度
    private int eliteHp = 30;        // 精英敌机血量
    private int elitePlusSpeedY = 8; // 超级精英敌机速度
    private int elitePlusHp = 60;    // 超级精英敌机血量
    private int bossSpeedY = 0;      // Boss速度
    private int bossHp = 300;        // Boss血量

    // 工厂实例
    private final EnemyFactory mobFactory;
    private final EnemyFactory eliteFactory;
    private final EnemyFactory elitePlusFactory;
    private final EnemyFactory bossFactory;

    // 计时控制
    private int timeSinceLastElitePlus = 0; // 距离上次尝试生成超级精英敌机的时间(ms)

    public EnemyGenerator() {
        this.mobFactory = new MobEnemyFactory();
        this.eliteFactory = new EliteEnemyFactory();
        this.elitePlusFactory = new ElitePlusEnemyFactory();
        this.bossFactory = new BossFactory();
    }

    /**
     * 敌机生成主逻辑
     * @param currentEnemyCount 当前敌机数量
     * @param currentScore 当前英雄机得分（用于Boss触发）
     * @param deltaTime 距离上一次调用的时间（毫秒，用于周期判断）
     * @return 新生成的敌机或 null
     */
    public AbstractEnemy generateEnemy(int currentEnemyCount, int currentScore, int deltaTime) {
        // Boss生成检测
        if (shouldGenerateBoss(currentScore, currentEnemyCount)) {
            lastBossScore = currentScore; // 记录本次触发的分数
            System.out.println("触发 Boss 生成！当前分数：" + currentScore);
            return bossFactory.createEnemy(bossSpeedY, bossHp);  // 使用可调参数
        }

        // 超级精英敌机生成检测
        timeSinceLastElitePlus += deltaTime;
        if (timeSinceLastElitePlus >= elitePlusInterval) {
            timeSinceLastElitePlus = 0; // 重置计时
            if (Math.random() < elitePlusProb && currentEnemyCount < enemyMaxNumber) {
                System.out.println("生成超级精英敌机！");
                return elitePlusFactory.createEnemy(elitePlusSpeedY, elitePlusHp);  // 使用可调参数
            }
        }

        // 普通敌机与精英敌机的随机生成逻辑
        if (currentEnemyCount >= enemyMaxNumber) {
            return null; // 达到上限，不生成
        }

        double rand = Math.random();
        if (rand < mobProb) {
            return mobFactory.createEnemy(mobSpeedY, mobHp);  // 使用可调参数
        } else {
            return eliteFactory.createEnemy(eliteSpeedY, eliteHp);  // 使用可调参数
        }
    }

    /**
     * 判断是否需要生成Boss敌机
     */
    private boolean shouldGenerateBoss(int currentScore, int currentEnemyCount) {
        if (currentScore > 0 &&
                currentScore >= lastBossScore + bossScoreInterval &&
                currentEnemyCount < enemyMaxNumber) {
            return true;
        }
        return false;
    }

    // ========== Setter 方法，支持难度配置 ==========
    /** 设置屏幕最大敌机数量 */
    public void setEnemyMaxNumber(int enemyMaxNumber) {
        this.enemyMaxNumber = Math.max(1, enemyMaxNumber); // 防止<=0
    }

    /** 设置普通敌机生成概率 (0.0 ~ 1.0) */
    public void setMobProbability(double mobProb) {
        this.mobProb = Math.max(0.0, Math.min(1.0, mobProb));
        this.eliteProb = 1.0 - this.mobProb; // 自动调整精英概率
    }

    /** 设置精英敌机生成概率 (0.0 ~ 1.0)，会调整 mobProb = 1 - eliteProb */
    public void setEliteProbability(double eliteProb) {
        this.eliteProb = Math.max(0.0, Math.min(1.0, eliteProb));
        this.mobProb = 1.0 - this.eliteProb;
    }

    /** 设置超级精英敌机尝试间隔 (ms) */
    public void setElitePlusInterval(int elitePlusInterval) {
        this.elitePlusInterval = Math.max(100, elitePlusInterval); // 最小100ms
    }

    /** 设置超级精英敌机生成概率 (0.0 ~ 1.0) */
    public void setElitePlusProbability(double elitePlusProb) {
        this.elitePlusProb = Math.max(0.0, Math.min(1.0, elitePlusProb));
    }

    /** 设置Boss触发分数间隔 */
    public void setBossScoreInterval(int bossScoreInterval) {
        this.bossScoreInterval = Math.max(50, bossScoreInterval); // 最小50分
    }

    // ========== 新增 Setter：敌机速度和血量 ==========
    /** 设置普通敌机速度 (speedY) */
    public void setMobSpeedY(int mobSpeedY) {
        this.mobSpeedY = Math.max(1, mobSpeedY); // 防止<=0
    }

    /** 设置普通敌机血量 (hp) */
    public void setMobHp(int mobHp) {
        this.mobHp = Math.max(10, mobHp); // 最小10
    }

    /** 设置精英敌机速度 (speedY) */
    public void setEliteSpeedY(int eliteSpeedY) {
        this.eliteSpeedY = Math.max(1, eliteSpeedY);
    }

    /** 设置精英敌机血量 (hp) */
    public void setEliteHp(int eliteHp) {
        this.eliteHp = Math.max(10, eliteHp);
    }

    /** 设置超级精英敌机速度 (speedY) */
    public void setElitePlusSpeedY(int elitePlusSpeedY) {
        this.elitePlusSpeedY = Math.max(1, elitePlusSpeedY);
    }

    /** 设置超级精英敌机血量 (hp) */
    public void setElitePlusHp(int elitePlusHp) {
        this.elitePlusHp = Math.max(10, elitePlusHp);
    }

    /** 设置 Boss 速度 (speedY) */
    public void setBossSpeedY(int bossSpeedY) {
        this.bossSpeedY = Math.max(0, bossSpeedY); // Boss 可为0
    }

    /** 设置 Boss 血量 (hp) */
    public void setBossHp(int bossHp) {
        this.bossHp = Math.max(100, bossHp); // 最小100
    }

    // Getter 方法（可选，用于调试）
    // ... 可以添加类似 getMobSpeedY() 等，如果需要
}