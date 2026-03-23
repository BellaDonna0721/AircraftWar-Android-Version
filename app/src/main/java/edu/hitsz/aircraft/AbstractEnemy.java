package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * 所有敌机的抽象父类
 * 共同特性：沿 Y 轴向下移动，超出屏幕下边界后消失
 */
public abstract class AbstractEnemy extends AbstractAircraft {
    
    protected int shootTime;
    
    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 随机初始化射击计时器，这样敌机出场后就能在随机时间开始射击，避免同一时间产生弹幕墙
        this.shootTime = (int)(Math.random() * 600);
    }
    
    public void increaseShootTime(int increment) {
        shootTime += increment;
    }
    
    public int getShootTime() {
        return shootTime;
    }
    
    public void resetShootTime() {
        shootTime = 0;
    }

    @Override
    public void forward() {
        super.forward();
        // 敌机通用：飞出下边界即消失
        if (locationY >= edu.hitsz.application.Game.SCREEN_HEIGHT) {
            vanish();
        }
    }

    /**
     * 敌机的射击方式由具体子类实现
     */
    @Override
    public abstract List<BaseBullet> shoot();

    // 在 AbstractEnemy 或其共同父类中加入
    private boolean killedByBomb = false;

    public void setKilledByBomb(boolean killedByBomb) {
        this.killedByBomb = killedByBomb;
    }

    public boolean isKilledByBomb() {
        return killedByBomb;
    }


}
