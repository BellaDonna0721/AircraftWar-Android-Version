package edu.hitsz.aircraft.factory;

import edu.hitsz.aircraft.Boss;
import edu.hitsz.application.ImageManager;

public class BossFactory implements EnemyFactory {

    @Override
    public Boss createEnemy(int SpeedY, int HP) {
        return new Boss(
                // 随机生成Boss的初始X坐标（确保不会超出右边界）
                (int) (Math.random() * (edu.hitsz.application.Game.SCREEN_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                // 固定在屏幕上方 5% 高度位置左右
                (int) (edu.hitsz.application.Game.SCREEN_HEIGHT * 0.05),
                // Boss血量可以根据难度调整
                HP
        );
    }
}
