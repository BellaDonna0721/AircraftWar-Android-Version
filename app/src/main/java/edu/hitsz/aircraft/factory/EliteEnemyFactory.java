package edu.hitsz.aircraft.factory;

import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.ImageManager;

public class EliteEnemyFactory implements EnemyFactory {

    @Override
    public EliteEnemy createEnemy(int SpeedY, int HP) {
        return new EliteEnemy(
                (int) (Math.random() * (edu.hitsz.application.Game.SCREEN_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * edu.hitsz.application.Game.SCREEN_HEIGHT * 0.05),
                SpeedY,
                HP
        );
    }
}
