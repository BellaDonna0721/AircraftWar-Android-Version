package edu.hitsz.aircraft;

import edu.hitsz.aircraft.strategy.BossRingShoot;
import edu.hitsz.aircraft.strategy.Strategy;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.observer.BombSubscriber;

import java.util.List;

/**
 * Boss敌机：悬浮在上方左右移动，发射环形弹幕
 * @author
 */
public class Boss extends AbstractEnemy implements BombSubscriber {

    /** 射击策略 */
    private Strategy shootStrategy;

    /** 每次射击子弹数量（环形弹幕） */
    private int shootNum = 20;

    /** 子弹伤害 */
    private int power = 10;

    /** 射击方向（任意方向都可以） */
    private int direction = 0;


    public Boss(int locationX, int locationY, int hp) {
        super(locationX, locationY,
                (Math.random() < 0.5 ? -2 : 2), // 随机左右速度
                0, // 不向下移动
                hp);
        // 默认使用环形弹幕策略
        this.shootStrategy = new BossRingShoot();
    }

    public void setShootStrategy(Strategy strategy) {
        this.shootStrategy = strategy;
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(
            this.getLocationX(),
            this.getLocationY(),
            this.getSpeedY(),
            direction,
            shootNum,
            power
        );
    }

    @Override
    public void update() {
        // Boss 不受炸弹影响
    }
}
