package edu.hitsz.aircraft.factory;

import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.Main;
import edu.hitsz.application.ImageManager;

public class EliteEnemyFactory implements EnemyFactory {

    @Override
    public EliteEnemy createEnemy(int SpeedY, int HP) {
        return new EliteEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                SpeedY,
                HP
        );
    }
}
