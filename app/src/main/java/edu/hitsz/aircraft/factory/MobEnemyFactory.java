package edu.hitsz.aircraft.factory;

import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.application.Main;
import edu.hitsz.application.ImageManager;

public class MobEnemyFactory implements EnemyFactory {

    @Override
    public MobEnemy createEnemy(int SpeedY, int HP) {
        return new MobEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                0,
                SpeedY,
                HP
        );
    }
}
