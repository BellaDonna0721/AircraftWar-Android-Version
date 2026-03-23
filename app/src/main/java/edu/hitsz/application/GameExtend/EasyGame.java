package edu.hitsz.application.GameExtend;

import android.content.Context;
import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.Game;

public class EasyGame extends Game {
    
    public EasyGame(Context context) {
        super(context);
    }
    
    @Override
    protected void initDifficultyParams() {
        difficulty = "Easy";

        enemyGenerator.setEnemyMaxNumber(4);         // 屏幕上能同时存在的敌机最大数量
        enemyGenerator.setEliteProbability(0.4);     // 生成精英敌机的概率
        enemyGenerator.setElitePlusInterval(400);    // 尝试生成超级精英敌机的间隔时间
        enemyGenerator.setElitePlusProbability(0.5); // 生成超级精英敌机的概率
        enemyGenerator.setBossScoreInterval(150);    // 生成boss的分数

        enemyGenerator.setMobSpeedY(7);              // 普通敌机速度
        enemyGenerator.setMobHp(10);                 // 普通敌机血量
        enemyGenerator.setEliteSpeedY(5);            // 精英敌机速度
        enemyGenerator.setEliteHp(10);               // 精英敌机血量
        enemyGenerator.setElitePlusSpeedY(5);        // 超级精英敌机速度
        enemyGenerator.setElitePlusHp(10);           // 超级精英敌机血量
        enemyGenerator.setBossSpeedY(0);             // boss机速度
        enemyGenerator.setBossHp(60);                // boss机血量

        heroShootCycle = 600;                        // 英雄机射击周期
        eliteShootCycle = 1000;                      // （超级）精英敌机射击周期
        bossShootCycle = 2000;                       // boss机射击周期
        cycleDuration = 700;                         // 尝试生成敌机周期
        heroInitialHp = 1500;                        // 英雄机初始血量
    }

    @Override
    protected AbstractEnemy generateEnemyLogic(int enemyCount, int score, int timeInterval) {
        // 简单调用生成器（已配置参数）
        return enemyGenerator.generateEnemy(enemyCount, score, timeInterval);
    }
}