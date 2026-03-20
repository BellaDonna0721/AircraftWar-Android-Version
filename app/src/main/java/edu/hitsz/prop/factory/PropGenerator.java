package edu.hitsz.prop.factory;

import edu.hitsz.observer.BombPublisher;
import edu.hitsz.prop.AbstractProp;
import java.util.Random;

/**
 * 道具生成器
 */
public class PropGenerator {

    private final PropFactory bloodFactory = new PropBloodFactory();
    private final PropFactory bulletFactory = new PropBulletFactory();
    private final PropFactory bulletPlusFactory = new PropBulletPlusFactory();
    private final PropFactory bombFactory;

    private final Random random = new Random();

    // ✅ 构造函数接收 BombPublisher
    public PropGenerator(BombPublisher bombPublisher) {
        this.bombFactory = new PropBombFactory(bombPublisher);
    }

    /**
     * 随机生成一种道具
     */
    public AbstractProp generateProp(int locationX, int locationY, int speedY) {
        int type = random.nextInt(4);
        switch (type) {
            case 0:
                return bloodFactory.createProp(locationX, locationY, speedY);
            case 1:
                return bulletFactory.createProp(locationX, locationY, speedY);
            case 2:
                return bombFactory.createProp(locationX, locationY, speedY);
            case 3:
                return bulletPlusFactory.createProp(locationX, locationY, speedY);
            default:
                return null;
        }
    }
}
