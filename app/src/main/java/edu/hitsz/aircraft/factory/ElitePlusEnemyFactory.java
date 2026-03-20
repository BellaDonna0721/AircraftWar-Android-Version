package edu.hitsz.aircraft.factory;

import edu.hitsz.aircraft.ElitePlusEnemy;
import edu.hitsz.application.Main;
import edu.hitsz.application.ImageManager;

public class ElitePlusEnemyFactory implements EnemyFactory {

    @Override
    public ElitePlusEnemy createEnemy(int SpeedY, int HP) {
        return new ElitePlusEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITEPLUS_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                SpeedY,
                HP
        );
    }
}
