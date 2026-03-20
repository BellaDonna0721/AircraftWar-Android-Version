package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.observer.BombPublisher;

/**
 * 炸弹道具
 */
public class PropBomb extends AbstractProp {

    private BombPublisher bombPublisher;

    public PropBomb(int locationX, int locationY, int speedY, BombPublisher bombPublisher) {
        super(locationX, locationY, speedY);
        this.bombPublisher = bombPublisher;
    }

    @Override
    public void effect(HeroAircraft heroAircraft) {
        System.out.println("BombSupply active!");
        vanish();
        bombPublisher.bombEffect();
    }
}
