package edu.hitsz.application.GameExtend;

import android.content.Context;
import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.application.Game;

public class HardGame extends Game {
    
    public HardGame(Context context) {
        super(context);
    }
    
    @Override
    protected void initDifficultyParams() {
        difficulty = "Hard";

        enemyGenerator.setEnemyMaxNumber(10);
        enemyGenerator.setEliteProbability(0.6);
        enemyGenerator.setElitePlusInterval(200);
        enemyGenerator.setElitePlusProbability(0.95);
        enemyGenerator.setBossScoreInterval(100);

        enemyGenerator.setMobSpeedY(15);
        enemyGenerator.setMobHp(50);
        enemyGenerator.setEliteSpeedY(12);
        enemyGenerator.setEliteHp(50);
        enemyGenerator.setElitePlusSpeedY(12);
        enemyGenerator.setElitePlusHp(100);
        enemyGenerator.setBossSpeedY(2);
        enemyGenerator.setBossHp(500);

        heroShootCycle = 400;
        eliteShootCycle = 300;
        bossShootCycle = 800;
        cycleDuration = 400;
        heroInitialHp = 800;
    }

    @Override
    protected AbstractEnemy generateEnemyLogic(int enemyCount, int score, int timeInterval) {
        // 简单调用生成器
        return enemyGenerator.generateEnemy(enemyCount, score, timeInterval);
    }
}