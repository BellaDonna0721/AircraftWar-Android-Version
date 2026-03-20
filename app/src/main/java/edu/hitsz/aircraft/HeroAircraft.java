package edu.hitsz.aircraft;

import edu.hitsz.aircraft.strategy.HeroStraightShoot;
import edu.hitsz.aircraft.strategy.Strategy;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    /**攻击方式 */
    private Strategy shootStrategy;

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹伤害
     */
    private int power = 10;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1)
     */
    private int direction = -1;

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    public HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 默认使用直线射击策略
        this.shootStrategy = new HeroStraightShoot();
    }

    /**
     * 重置为默认射击策略（直线射击）
     */
    public void resetShootStrategy() {
        this.shootStrategy = new HeroStraightShoot();
        this.shootNum = 1;  // 重置为默认单发
    }

    /**
     * 设置子弹发射数量
     */
    public void setShootNum(int num) {
        this.shootNum = num;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    public void setShootStrategy(Strategy strategy) {
        this.shootStrategy = strategy;
    }

    public Strategy getShootStrategy() {
        return this.shootStrategy;
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
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

    public int getMaxHp() {
        return maxHp;
    }

    public void setHp(int newHp) {
        this.hp = Math.max(0, Math.min(newHp, maxHp));
    }
}
