package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import java.util.Collections;
import java.util.List;
import edu.hitsz.observer.BombSubscriber;

/**
 * 普通敌机：不会射击
 */
public class MobEnemy extends AbstractEnemy implements BombSubscriber {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<BaseBullet> shoot() {
        // 普通敌机不射击，返回空集合
        return Collections.emptyList();
    }

    @Override
    public void update() {
        // 被炸弹影响：直接销毁
        this.setKilledByBomb(true);
        this.vanish();
    }
}
