package edu.hitsz.aircraft.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class HeroRingShoot implements Strategy{
    @Override
    public List<BaseBullet> shoot(int LocationX, int LocationY, int SpeedY, int direction, int shootNum, int power) {
        List<BaseBullet> res = new LinkedList<>();
        // 每发子弹的角度间隔（360°均匀分布）
        double angleStep = 2 * Math.PI / Math.max(1, shootNum);  // 防止除以零
        double baseSpeed = 20;  // 进一步提高基础速度

        for (int i = 0; i < shootNum; i++) {
            double angle = i * angleStep;
            
            // 计算速度分量
            int speedX = (int) (baseSpeed * Math.cos(angle));
            int speedY = (int) (baseSpeed * Math.sin(angle));

            BaseBullet bullet = new HeroBullet(
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
