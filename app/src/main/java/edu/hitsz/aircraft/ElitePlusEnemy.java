package edu.hitsz.aircraft;

import edu.hitsz.aircraft.strategy.EliteFanShoot;
import edu.hitsz.aircraft.strategy.Strategy;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.observer.BombSubscriber;

import java.util.List;

/**
 * 加强版精英敌机
 * 能向下方发射三发扇形子弹
 * @author hitsz
 */
public class ElitePlusEnemy extends AbstractEnemy implements BombSubscriber {

    /** 射击策略 */
    private Strategy shootStrategy;

    /** 每次射击子弹数量 */
    private int shootNum = 3;

    /** 子弹伤害 */
    private int power = 10;

    /** 子弹射击方向（向下发射：1） */
    private int direction = 1;

    public ElitePlusEnemy(int locationX, int locationY, int speedY, int hp) {
        super(locationX, locationY,
                (Math.random() < 0.5 ? -1 : 1) * 2, // 随机左右速度
                speedY,
                hp);
        // 默认使用扇形射击策略
        this.shootStrategy = new EliteFanShoot();
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
        int hp = this.getHp();
        this.decreaseHp(hp / 2); // 依据你之前的策略
        if (this.notValid()) {
            this.setKilledByBomb(true);
            // vanish() 可能在 notValid() 的别的逻辑里被调用，
            // 若需要明确调用可加 this.vanish();
        }
    }
}
