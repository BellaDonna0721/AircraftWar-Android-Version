package edu.hitsz.application.GameExtend;

import android.content.Context;
import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.application.Game;

public class NormalGame extends Game {
    public NormalGame(Context context) {
        super(context);
    }

    @Override
    protected void initDifficultyParams() {
        difficulty = "Normal";

        enemyGenerator.setEnemyMaxNumber(6);
        enemyGenerator.setEliteProbability(0.3);
        enemyGenerator.setElitePlusInterval(300);
        enemyGenerator.setElitePlusProbability(0.8);
        enemyGenerator.setBossScoreInterval(150);

        enemyGenerator.setMobSpeedY(10);
        enemyGenerator.setMobHp(30);
        enemyGenerator.setEliteSpeedY(8);
        enemyGenerator.setEliteHp(30);
        enemyGenerator.setElitePlusSpeedY(8);
        enemyGenerator.setElitePlusHp(60);
        enemyGenerator.setBossSpeedY(0);
        enemyGenerator.setBossHp(300);

        heroShootCycle = 490;
        eliteShootCycle = 400;
        bossShootCycle = 1200;
        cycleDuration = 600;
        heroInitialHp = 1000;
    }

    @Override
    protected AbstractEnemy generateEnemyLogic(int enemyCount, int score, int timeInterval) {
        // 简单调用生成器
        return enemyGenerator.generateEnemy(enemyCount, score, timeInterval);
    }
}