package edu.hitsz.aircraft.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class EliteFanShoot implements Strategy{
    @Override
    public List<BaseBullet> shoot(int LocationX, int LocationY, int SpeedY, int direction, int shootNum, int power) {
        List<BaseBullet> res = new LinkedList<>();
        int x = LocationX;
        int y = LocationY + direction * 2;

        // 三发子弹：中间直下，两边有水平速度分量
        int[][] bulletSpeed = {
                {-2, SpeedY + direction * 2},  // 左偏
                {0,  SpeedY + direction * 3},  // 中间
                {2,  SpeedY + direction * 2}   // 右偏
        };

        for (int i = 0; i < shootNum; i++) {
            BaseBullet bullet = new EnemyBullet(
                    x + (i - 1) * 10,  // 稍微分散位置
                    y,
                    bulletSpeed[i][0],
                    bulletSpeed[i][1],
                    power
            );
            res.add(bullet);
        }
        return res;
    }
}