package edu.hitsz.prop.factory;

import edu.hitsz.observer.BombPublisher;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBomb;

/**
 * 炸弹道具工厂
 */
public class PropBombFactory implements PropFactory {

    private final BombPublisher bombPublisher;

    public PropBombFactory(BombPublisher bombPublisher) {
        this.bombPublisher = bombPublisher;
    }

    @Override
    public AbstractProp createProp(int locationX, int locationY, int speedY) {
        return new PropBomb(locationX, locationY, speedY, bombPublisher);
    }
}
