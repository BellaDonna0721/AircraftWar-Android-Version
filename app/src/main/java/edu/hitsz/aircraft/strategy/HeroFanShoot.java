package edu.hitsz.aircraft.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class HeroFanShoot implements Strategy{
    @Override
    public List<BaseBullet> shoot(int LocationX, int LocationY, int SpeedY, int direction, int shootNum, int power) {
        List<BaseBullet> res = new LinkedList<>();
        int x = LocationX;
        int y = LocationY + direction * 2;

        // 计算扇形范围（默认60度扇形）
        double baseSpeed = 20;  // 提高基础速度
        double fanAngle = Math.PI / 3;  // 60度扇形
        
        for(int i = 0; i < shootNum; i++){
            // 计算当前子弹的角度，均匀分布在扇形范围内
            double angle;
            if (shootNum == 1) {
                angle = 0;  // 单发子弹直接向前
            } else {
                angle = -fanAngle/2 + (fanAngle * i / (Math.max(1, shootNum-1)));
            }
            
            // 计算速度分量，增加垂直方向的速度
            double verticalMultiplier = 1.5;  // 垂直速度增强系数
            
            // 计算基础速度分量
            double speedX = baseSpeed * Math.sin(angle);
            double speedY = baseSpeed * verticalMultiplier * Math.cos(angle);
            
            // 向上发射时需要将垂直速度取反
            if (direction < 0) {
                speedY = -speedY;
            }
            
            res.add(new HeroBullet(
                    x + (i - shootNum/2) * 10,  // 均匀分散位置
                    y,
                    (int)speedX,
                    (int)speedY,
                    power
            ));

            BaseBullet bullet = new HeroBullet(
                    x + (i - shootNum/2) * 10,  // 均匀分散位置
                    y,
                    (int)speedX,
                    (int)speedY,
                    power
            );
            res.add(bullet);
        }
        return res;
    }
}