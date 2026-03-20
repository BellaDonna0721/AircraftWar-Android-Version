package edu.hitsz.aircraft;

import edu.hitsz.aircraft.strategy.EliteStraightShoot;
import edu.hitsz.aircraft.strategy.Strategy;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.observer.BombSubscriber;

import java.util.List;

/**
 * 精英敌机
 * 能向下发射子弹
 * @author hitsz
 */
public class EliteEnemy extends AbstractEnemy implements BombSubscriber {

    /** 射击策略 */
    private Strategy shootStrategy;

    /** 每次射击子弹数量 */
    private int shootNum = 1;

    /** 子弹伤害 */
    private int power = 20;

    /** 子弹射击方向（向下发射：1） */
    private int direction = 1;

    public EliteEnemy(int locationX, int locationY, int speedY, int hp) {
        super(locationX, locationY,
                (Math.random() < 0.5 ? -1 : 1) * 2, // 随机左右速度
                speedY,
                hp);
        // 默认使用直线射击策略
        this.shootStrategy = new EliteStraightShoot();
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
        this.setKilledByBomb(true);
        // 被炸弹影响：直接销毁
        this.vanish();
    }
}