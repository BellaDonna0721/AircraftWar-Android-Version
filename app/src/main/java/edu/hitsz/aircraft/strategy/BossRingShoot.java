package edu.hitsz.aircraft.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class BossRingShoot implements Strategy{
    @Override
    public List<BaseBullet> shoot(int LocationX, int LocationY, int SpeedY, int direction, int shootNum, int power) {
        List<BaseBullet> res = new LinkedList<>();
        // 每发子弹的角度间隔（360°均匀分布）
        double angleStep = 2 * Math.PI / shootNum;

        for (int i = 0; i < shootNum; i++) {
            double angle = i * angleStep;
            int speedX = (int) (5 * Math.cos(angle));
            int speedY = (int) (5 * Math.sin(angle));

            BaseBullet bullet = new EnemyBullet(
                    LocationX,
                    LocationY,
                    speedX,
                    speedY,
                    power
            );
            res.add(bullet);
        }

        return res;
    }
}