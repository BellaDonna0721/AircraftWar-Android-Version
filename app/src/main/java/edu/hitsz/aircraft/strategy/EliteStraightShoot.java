package edu.hitsz.aircraft.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class EliteStraightShoot implements Strategy {
    @Override
    public List<BaseBullet> shoot(int LocationX, int LocationY, int SpeedY, int direction, int shootNum, int power) {
        List<BaseBullet> res = new LinkedList<>();
        int x = LocationX;
        int y = LocationY + direction * 2;
        int speedX = 0;                       // 子弹水平速度
        int speedY = SpeedY + direction * 5;  // 子弹竖直速度
        BaseBullet bullet;
        for (int i = 0; i < shootNum; i++) {
            // 多发子弹时可左右分散
            bullet = new EnemyBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}